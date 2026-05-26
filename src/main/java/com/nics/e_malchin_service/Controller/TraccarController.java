package com.nics.e_malchin_service.Controller;

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
    private final TrackerDeviceService deviceService;

    public TraccarController(TraccarService traccarService,
                             GpsPositionSyncService syncService,
                             TrackerDeviceService deviceService) {
        this.traccarService = traccarService;
        this.syncService    = syncService;
        this.deviceService  = deviceService;
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

    // GET /api/tracker/route?deviceId=4&from=...&to=...
    // GET /api/tracker/route?imei=865412051234567&from=...&to=...  (synthetic / unregistered devices)
    @GetMapping("/route")
    public ResponseEntity<?> getRoute(
            @RequestParam(required = false) Integer deviceId,
            @RequestParam(required = false) String imei,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(traccarService.getRoute(deviceId, imei, from, to));
    }

    // ─── Manual sync trigger ─────────────────────────────────────────────────

    @PostMapping("/sync")
    public ResponseEntity<?> syncNow() {
        int count = syncService.syncFromTraccar();
        return ResponseEntity.ok(Map.of("synced", count));
    }
}
