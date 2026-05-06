package SiemensTicketApp.App.Service;

import SiemensTicketApp.App.Model.Booking;
import SiemensTicketApp.App.Model.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getCustomerEmail());
        message.setSubject("Booking Confirmation - Train " + booking.getSchedule().getTrain().getName());
        message.setText(
                "Dear " + booking.getCustomerName() + ",\n\n" +
                        "Your booking has been confirmed!\n\n" +
                        "Train: " + booking.getSchedule().getTrain().getName() + "\n" +
                        "Departure: " + booking.getSchedule().getDepartureTime() + "\n" +
                        "Seats booked: " + booking.getSeatsBooked() + "\n\n" +
                        "Thank you for travelling with us!"
        );
        try {
            mailSender.send(message);
            log.info("Confirmation email sent to {}", booking.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}", booking.getCustomerEmail(), e);
        }
    }

    public void sendDelayNotification(Booking booking, int delayMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getCustomerEmail());
        message.setSubject("Delay Notice - Train " + booking.getSchedule().getTrain().getName());
        message.setText(
                "Dear " + booking.getCustomerName() + ",\n\n" +
                        "We regret to inform you that your train has been delayed.\n\n" +
                        "Train: " + booking.getSchedule().getTrain().getName() + "\n" +
                        "Original departure: " + booking.getSchedule().getDepartureTime() + "\n" +
                        "Delay: " + delayMinutes + " minutes\n\n" +
                        "We apologize for the inconvenience."
        );
        try {
            mailSender.send(message);
            log.info("Delay notification sent to {}", booking.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send delay email to {}", booking.getCustomerEmail(), e);
        }
    }
}