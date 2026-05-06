package SiemensTicketApp.App.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSearchRequest {

    @NotBlank(message = "Origin station is required")
    private String origin;

    @NotBlank(message = "Destination station is required")
    private String destination;
}
