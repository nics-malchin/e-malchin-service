package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Service.GpsHistoryBackfillService;
import com.nics.e_malchin_service.Service.GpsPositionSyncService;
import com.nics.e_malchin_service.Service.TraccarService;
import com.nics.e_malchin_service.Service.TrackerDeviceService;
import com.nics.e_malchin_service.dto.TrackerDeviceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracker")
public class TraccarController {

    private final TraccarService traccarService;
    private final GpsPositionSyncService syncService;
    private final GpsHistoryBackfillService backfillService;
    private final TrackerDeviceService deviceService;

    public TraccarController(TraccarService traccarService,
                             GpsPositionSyncService syncService,
                             GpsHistoryBackfillService backfillService,
                             TrackerDeviceService deviceService) {
        this.traccarService  = traccarService;
        this.syncService     = syncService;
        this.backfillService = backfillService;
        this.deviceService   = deviceService;
    }

    // ─── Device registry CRUD ────────────────────────────────────────────────

    @GetMapping("/devices")
    public ResponseEntity<?> getDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @PostMapping("/devices")
    public ResponseEntity<?> createDevice(@RequestBody TrackerDeviceDto dto) {
        return ResponseEntity.ok(deviceService.create(dto));
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Integer id,
                                          @RequestBody TrackerDeviceDto dto) {
        return ResponseEntity.ok(deviceService.update(id, dto));
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Integer id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Positions ───────────────────────────────────────────────────────────

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

    // ─── Sync / backfill ─────────────────────────────────────────────────────

    @PostMapping("/sync")
    public ResponseEntity<?> syncNow() {
        int count = syncService.syncFromLog();
        return ResponseEntity.ok(Map.of("synced", count));
    }

    @PostMapping("/backfill")
    public ResponseEntity<?> startBackfill(@RequestParam(required = false) String logPath) {
        if (backfillService.isRunning()) {
            return ResponseEntity.ok(Map.of("status", "already_running",
                    "progress", backfillService.getProgress()));
        }
        backfillService.startBackfill(logPath);
        return ResponseEntity.ok(Map.of("status", "started"));
    }

    @GetMapping("/backfill/status")
    public ResponseEntity<?> backfillStatus() {
        return ResponseEntity.ok(Map.of(
                "running",  backfillService.isRunning(),
                "progress", backfillService.getProgress()
        ));
    }
}
