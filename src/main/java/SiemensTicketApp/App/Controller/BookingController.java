package SiemensTicketApp.App.Controller;

import SiemensTicketApp.App.DTO.BookingRequest;
import SiemensTicketApp.App.DTO.TripSearchRequest;
import SiemensTicketApp.App.Model.Booking;
import SiemensTicketApp.App.Service.BookingService;
import SiemensTicketApp.App.Service.RouteFinderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RouteFinderService routeFinderService;

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchTrips(@Valid @RequestBody TripSearchRequest request) {
        return ResponseEntity.ok(
                routeFinderService.findTrips(request.getOrigin(), request.getDestination())
        );
    }
}