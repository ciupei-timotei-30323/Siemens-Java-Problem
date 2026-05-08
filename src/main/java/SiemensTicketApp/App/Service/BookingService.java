package SiemensTicketApp.App.Service;


import SiemensTicketApp.App.DTO.BookingRequest;
import SiemensTicketApp.App.Model.Booking;
import SiemensTicketApp.App.Model.Schedule;
import SiemensTicketApp.App.Repository.BookingRepository;
import SiemensTicketApp.App.Repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final EmailService emailService;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        Schedule schedule = scheduleRepository.findByIdWithLock(request.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        int totalCapacity = schedule.getTrain().getCapacity();
        int seatsAlreadyBooked = bookingRepository.sumSeatsBookedByScheduleId(schedule.getId());
        int seatsRemaining = totalCapacity - seatsAlreadyBooked;

        if (request.getSeatsBooked() > seatsRemaining) {
            throw new IllegalStateException(
                    "Not enough seats available. Requested: " + request.getSeatsBooked() +
                            ", Available: " + seatsRemaining
            );
        }

        Booking booking = Booking.builder()
                .schedule(schedule)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .seatsBooked(request.getSeatsBooked())
                .build();

        Booking saved = bookingRepository.save(booking);
        emailService.sendBookingConfirmation(saved);
        return saved;
    }

    public List<Booking> getBookingsForSchedule(Long scheduleId) {
        return bookingRepository.findByScheduleId(scheduleId);
    }

    public List<Booking> getBookingsForTrain(Long trainId) {
        return bookingRepository.findByScheduleTrainId(trainId);
    }
}
