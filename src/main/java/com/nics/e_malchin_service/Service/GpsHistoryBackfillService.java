package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.dto.BackfillResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GpsHistoryBackfillService {

    private static final Logger log = LoggerFactory.getLogger(GpsHistoryBackfillService.class);

    private static final Pattern GDATA_PATTERN = Pattern.compile(
        "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*?#(\\d{15})#.*?GDATA:A,(\\d+),(\\d{12}),([-\\d.]+),([-\\d.]+),(\\d+),(\\d+),(\\d+)"
    );
    private static final DateTimeFormatter LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final int BATCH_SIZE = 500;
    private static final int LOG_EVERY  = 5_000;

    @Value("${traccar.log.path:/traccar-logs/tracker-server.log}")
    private String defaultLogPath;

    private final JdbcTemplate jdbc;

    // --- progress state (readable via /backfill/status) ---
    private final AtomicBoolean  running       = new AtomicBoolean(false);
    private final AtomicLong     linesRead     = new AtomicLong(0);
    private final AtomicLong     parsed        = new AtomicLong(0);
    private final AtomicLong     inserted      = new AtomicLong(0);
    private volatile BackfillResultDto lastResult = null;

    public GpsHistoryBackfillService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Async — HTTP request returns immediately.
     * logFilePath: override path (null = use default from config).
     */
    @Async
    public void startBackfill(String logFilePath) {
        if (!running.compareAndSet(false, true)) {
            log.warn("GPS backfill already running — ignoring duplicate request");
            return;
        }

        linesRead.set(0);
        parsed.set(0);
        inserted.set(0);
        lastResult = null;

        String path = (logFilePath != null && !logFilePath.isBlank()) ? logFilePath : defaultLogPath;
        log.info("GPS backfill starting — file: {}", path);
        long startMs = System.currentTimeMillis();

        List<Object[]> batch = new ArrayList<>(BATCH_SIZE);

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                linesRead.incrementAndGet();
                if (!line.contains("GDATA:A")) continue;

                Matcher m = GDATA_PATTERN.matcher(line);
                if (!m.find()) continue;

                LocalDateTime fixTime = LocalDateTime.parse(m.group(1), LOG_TS);

                batch.add(new Object[]{
                    m.group(2),                        // imei
                    Double.parseDouble(m.group(5)),    // latitude
                    Double.parseDouble(m.group(6)),    // longitude
                    Double.parseDouble(m.group(7)),    // speed
                    Double.parseDouble(m.group(8)),    // course
                    Double.parseDouble(m.group(9)),    // altitude
                    fixTime,                           // fix_time
                    LocalDateTime.now()                // synced_at
                });
                parsed.incrementAndGet();

                if (batch.size() >= BATCH_SIZE) {
                    inserted.addAndGet(flushBatch(batch));
                    batch.clear();
                }

                long p = parsed.get();
                if (p > 0 && p % LOG_EVERY == 0) {
                    log.info("GPS backfill — lines: {}, parsed: {}, inserted: {}",
                            linesRead.get(), p, inserted.get());
                }
            }

            if (!batch.isEmpty()) {
                inserted.addAndGet(flushBatch(batch));
            }

        } catch (Exception e) {
            log.error("GPS backfill error: {}", e.getMessage(), e);
        } finally {
            long duration = System.currentTimeMillis() - startMs;
            long p = parsed.get();
            long ins = inserted.get();
            lastResult = new BackfillResultDto(linesRead.get(), p, ins, p - ins, duration);
            log.info("GPS backfill done — lines: {}, parsed: {}, inserted: {}, skipped(dup): {}, {}ms",
                    linesRead.get(), p, ins, p - ins, duration);
            running.set(false);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public BackfillResultDto getProgress() {
        if (running.get()) {
            long p   = parsed.get();
            long ins = inserted.get();
            // partial result while running
            return new BackfillResultDto(linesRead.get(), p, ins, p - ins, -1L);
        }
        return lastResult;
    }

    // Returns count of actually inserted rows (INSERT IGNORE: dup rows return 0)
    private int flushBatch(List<Object[]> batch) {
        int[] results = jdbc.batchUpdate(
            "INSERT IGNORE INTO gps_position " +
            "(imei, latitude, longitude, speed, course, altitude, fix_time, synced_at) " +
            "VALUES (?,?,?,?,?,?,?,?)",
            batch
        );
        return Arrays.stream(results).sum();
    }
}
