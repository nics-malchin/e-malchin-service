package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Service.GpsHistoryBackfillService;
import com.nics.e_malchin_service.Service.GpsPositionSyncService;
import com.nics.e_malchin_service.Service.TraccarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracker")
public class TraccarController {

    private final TraccarService traccarService;
    private final GpsPositionSyncService syncService;
    private final GpsHistoryBackfillService backfillService;

    public TraccarController(TraccarService traccarService,
                             GpsPositionSyncService syncService,
                             GpsHistoryBackfillService backfillService) {
        this.traccarService  = traccarService;
        this.syncService     = syncService;
        this.backfillService = backfillService;
    }

    @GetMapping("/devices")
    public ResponseEntity<?> getDevices() {
        return ResponseEntity.ok(traccarService.getDevices());
    }

    @GetMapping("/positions")
    public ResponseEntity<?> getLatestPositions() {
        return ResponseEntity.ok(traccarService.getLatestPositions());
    }

    // GET /api/tracker/route?deviceId=4&from=2026-05-01T00:00:00.000Z&to=2026-05-14T23:59:59.000Z
    @GetMapping("/route")
    public ResponseEntity<?> getRoute(
            @RequestParam Integer deviceId,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(traccarService.getRoute(deviceId, from, to));
    }

    // POST /api/tracker/sync — manual incremental sync (log → DB, new entries only)
    @PostMapping("/sync")
    public ResponseEntity<?> syncNow() {
        int count = syncService.syncFromLog();
        return ResponseEntity.ok(Map.of("synced", count));
    }

    // POST /api/tracker/backfill              — use default log path from config
    // POST /api/tracker/backfill?logPath=...  — specify a different file (rotated logs)
    @PostMapping("/backfill")
    public ResponseEntity<?> startBackfill(@RequestParam(required = false) String logPath) {
        if (backfillService.isRunning()) {
            return ResponseEntity.ok(Map.of("status", "already_running",
                    "progress", backfillService.getProgress()));
        }
        backfillService.startBackfill(logPath);
        return ResponseEntity.ok(Map.of("status", "started",
                "message", "백필 시작됨. /api/tracker/backfill/status 에서 진행 상황 확인"));
    }

    // GET /api/tracker/backfill/status — check progress or last result
    @GetMapping("/backfill/status")
    public ResponseEntity<?> backfillStatus() {
        return ResponseEntity.ok(Map.of(
                "running",  backfillService.isRunning(),
                "progress", backfillService.getProgress()
        ));
    }
}
