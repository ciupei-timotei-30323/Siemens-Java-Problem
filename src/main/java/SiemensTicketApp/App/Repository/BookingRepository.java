package SiemensTicketApp.App.Repository;

import SiemensTicketApp.App.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByScheduleId(Long scheduleId);

    // Sums all seats booked for a given schedule — used for overbooking check
    @Query("SELECT COALESCE(SUM(b.seatsBooked), 0) FROM Booking b WHERE b.schedule.id = :scheduleId")
    int sumSeatsBookedByScheduleId(Long scheduleId);

    List<Booking> findByScheduleTrainId(Long trainId);
}
