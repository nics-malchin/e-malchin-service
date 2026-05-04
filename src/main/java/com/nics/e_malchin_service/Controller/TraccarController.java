package com.nics.e_malchin_service.Controller;

import com.nics.e_malchin_service.Service.TraccarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracker")
public class TraccarController {

    private final TraccarService traccarService;

    public TraccarController(TraccarService traccarService) {
        this.traccarService = traccarService;
    }

    @GetMapping("/devices")
    public ResponseEntity<?> getDevices() {
        return ResponseEntity.ok(traccarService.getDevices());
    }

    @GetMapping("/positions")
    public ResponseEntity<?> getLatestPositions() {
        return ResponseEntity.ok(traccarService.getLatestPositions());
    }

    // GET /api/tracker/route?deviceId=4&from=2026-05-01T00:00:00.000Z&to=2026-05-04T23:59:59.000Z
    @GetMapping("/route")
    public ResponseEntity<?> getRoute(
            @RequestParam Integer deviceId,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(traccarService.getRoute(deviceId, from, to));
    }
}
