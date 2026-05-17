package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.Entity.GpsPosition;
import com.nics.e_malchin_service.repository.GpsPositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GpsPositionSyncService {

    private static final Logger log = LoggerFactory.getLogger(GpsPositionSyncService.class);

    // Groups:
    //  1 = log timestamp (yyyy-MM-dd HH:mm:ss)
    //  2 = IMEI (15 digits)
    //  3 = sats
    //  4 = fix datetime (YYMMDDHHmmss, 12 digits)
    //  5 = latitude
    //  6 = longitude
    //  7 = speed
    //  8 = course/heading
    //  9 = altitude
    // 10 = battery voltage (optional, e.g. BAT:3.76)
    // 11 = signal level    (optional, e.g. SIGNAL:18)
    private static final Pattern GDATA_PATTERN = Pattern.compile(
        "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*?#(\\d{15})#" +
        ".*?GDATA:A,(\\d+),(\\d{12}),([-\\d.]+),([-\\d.]+),(\\d+),(\\d+),(\\d+)" +
        "(?:[^\\r\\n]*?BAT:([\\d.]+))?" +
        "(?:[^\\r\\n]*?SIGNAL:(\\d+))?"
    );
    private static final DateTimeFormatter LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${traccar.log.path:/traccar-logs/tracker-server.log}")
    private String logPath;

    private final GpsPositionRepository positionRepository;

    public GpsPositionSyncService(GpsPositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Scheduled(fixedDelayString = "${traccar.sync.interval-ms:60000}")
    public int syncFromLog() {
        int saved = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("GDATA:A")) continue;

                Matcher m = GDATA_PATTERN.matcher(line);
                if (!m.find()) continue;

                LocalDateTime fixTime = LocalDateTime.parse(m.group(1), LOG_TS);
                String imei = m.group(2);

                if (positionRepository.existsByImeiAndFixTime(imei, fixTime)) {
                    skipped++;
                    continue;
                }

                GpsPosition pos = new GpsPosition();
                pos.setImei(imei);
                pos.setFixTime(fixTime);
                pos.setSats(Integer.parseInt(m.group(3)));
                pos.setLatitude(Double.parseDouble(m.group(5)));
                pos.setLongitude(Double.parseDouble(m.group(6)));
                pos.setSpeed(Double.parseDouble(m.group(7)));
                pos.setCourse(Double.parseDouble(m.group(8)));
                pos.setAltitude(Double.parseDouble(m.group(9)));
                if (m.group(10) != null) pos.setBatteryVolt(Double.parseDouble(m.group(10)));
                if (m.group(11) != null) pos.setSignalLevel(Integer.parseInt(m.group(11)));
                pos.setSyncedAt(LocalDateTime.now());
                positionRepository.save(pos);
                saved++;
            }
        } catch (Exception e) {
            log.error("GpsPositionSyncService: log parse error — {}", e.getMessage());
            return saved;
        }

        if (saved > 0 || skipped > 0) {
            log.info("GPS sync done — saved: {}, skipped (dup): {}", saved, skipped);
        }
        return saved;
    }
}
