package SiemensTicketApp.App.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainResponse(

        @NotBlank(message = "Train name is required.")
        String name,

        @Min(value = 1, message = "Capacity must be greater than 0")
        int capacity,

        @NotNull
        Long id,

        @NotNull(message = "Route ID is required")
        Long routeId,

        @NotNull(message = "Route name is required")
        String routeName
) {
}
