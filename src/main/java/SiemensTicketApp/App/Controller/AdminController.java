package SiemensTicketApp.App.Controller;

import SiemensTicketApp.App.DTO.RouteRequest;
import SiemensTicketApp.App.DTO.TrainRequest;
import SiemensTicketApp.App.Model.Station;
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
    public ResponseEntity<?> addTrain(@RequestBody TrainRequest train) { return ResponseEntity.ok(adminService.addTrain(train)); }

    @PutMapping("/trains/{id}")
    public ResponseEntity<?> updateTrain(@PathVariable Long id, @RequestBody TrainRequest train) { return ResponseEntity.ok(adminService.updateTrain(id, train)); }

    @DeleteMapping("/trains/{id}")
    public ResponseEntity<?> deleteTrain(@PathVariable Long id) { adminService.deleteTrain(id); return ResponseEntity.noContent().build(); }

    // Routes
    @GetMapping("/routes")
    public ResponseEntity<?> getRoutes() { return ResponseEntity.ok(adminService.getAllRoutes()); }

    @PostMapping("/routes")
    public ResponseEntity<?> addRoute(@RequestBody RouteRequest route) { return ResponseEntity.ok(adminService.addRoute(route)); }

    @PutMapping("/routes/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody RouteRequest route) { return ResponseEntity.ok(adminService.updateRoute(id, route)); }

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

    // Stations
    @PostMapping("/station")
    public ResponseEntity<?> addStation(@RequestBody Station station) {return ResponseEntity.ok(adminService.addStation(station));}

    @PutMapping("/station/{stationId}")
    public ResponseEntity<?> modifyStation(@PathVariable Long stationId, @RequestBody Station station) {return ResponseEntity.ok(adminService.updateStation(stationId, station));}

    @DeleteMapping("/station/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {adminService.deleteStation(id); return ResponseEntity.noContent().build();}

}