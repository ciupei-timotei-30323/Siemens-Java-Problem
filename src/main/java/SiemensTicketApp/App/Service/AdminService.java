package SiemensTicketApp.App.Service;

import SiemensTicketApp.App.Model.*;
import SiemensTicketApp.App.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    // --- Train operations ---
    public Train addTrain(Train train) { return trainRepository.save(train); }
    public void deleteTrain(Long id) { trainRepository.deleteById(id); }
    public Train updateTrain(Long id, Train updated) {
        updated.setId(id);
        return trainRepository.save(updated);
    }
    public List<Train> getAllTrains() { return trainRepository.findAll(); }

    // --- Route operations ---
    public Route addRoute(Route route) { return routeRepository.save(route); }
    public void deleteRoute(Long id) { routeRepository.deleteById(id); }
    public Route updateRoute(Long id, Route updated) {
        updated.setId(id);
        return routeRepository.save(updated);
    }
    public List<Route> getAllRoutes() { return routeRepository.findAll(); }

    // --- Bookings view ---
    public List<Booking> getBookingsForTrain(Long trainId) {
        return bookingRepository.findByScheduleTrainId(trainId);
    }

    // --- Delay ---
    @Transactional
    public void reportDelay(Long scheduleId, int delayMinutes) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        schedule.setDelayMinutes(delayMinutes);
        scheduleRepository.save(schedule);

        // Notify all customers on this schedule
        List<Booking> bookings = bookingRepository.findByScheduleId(scheduleId);
        bookings.forEach(b -> emailService.sendDelayNotification(b, delayMinutes));
    }
}
