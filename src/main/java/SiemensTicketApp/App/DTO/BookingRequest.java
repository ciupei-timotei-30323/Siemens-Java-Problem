package SiemensTicketApp.App.DTO;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String customerEmail;

    @Min(value = 1, message = "Must book at least 1 seat")
    private int seatsBooked;
}