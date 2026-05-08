package SiemensTicketApp.App.Controller;

import SiemensTicketApp.App.Model.Route;
import SiemensTicketApp.App.Model.Train;
import SiemensTicketApp.App.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Trains
    @GetMapping("/trains")
    public ResponseEntity<?> getTrains() { return ResponseEntity.ok(adminService.getAllTrains()); }

    @PostMapping("/trains")
    public ResponseEntity<?> addTrain(@RequestBody Train train) { return ResponseEntity.ok(adminService.addTrain(train)); }

    @PutMapping("/trains/{id}")
    public ResponseEntity<?> updateTrain(@PathVariable Long id, @RequestBody Train train) { return ResponseEntity.ok(adminService.updateTrain(id, train)); }

    @DeleteMapping("/trains/{id}")
    public ResponseEntity<?> deleteTrain(@PathVariable Long id) { adminService.deleteTrain(id); return ResponseEntity.noContent().build(); }

    // Routes
    @GetMapping("/routes")
    public ResponseEntity<?> getRoutes() { return ResponseEntity.ok(adminService.getAllRoutes()); }

    @PostMapping("/routes")
    public ResponseEntity<?> addRoute(@RequestBody Route route) { return ResponseEntity.ok(adminService.addRoute(route)); }

    @PutMapping("/routes/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody Route route) { return ResponseEntity.ok(adminService.updateRoute(id, route)); }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) { adminService.deleteRoute(id); return ResponseEntity.noContent().build(); }

    // Bookings
    @GetMapping("/trains/{trainId}/bookings")
    public ResponseEntity<?> getBookings(@PathVariable Long trainId) { return ResponseEntity.ok(adminService.getBookingsForTrain(trainId)); }

    // Delays
    @PostMapping("/schedules/{scheduleId}/delay")
    public ResponseEntity<?> reportDelay(@PathVariable Long scheduleId, @RequestParam int minutes) {
        adminService.reportDelay(scheduleId, minutes);
        return ResponseEntity.ok("Delay reported and customers notified.");
    }
}