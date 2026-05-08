package SiemensTicketApp.App.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainRequest(
        @NotBlank(message = "Train name is required")
        String name,

        @Min(value = 1, message = "Capacity must be greater than 0")
        int capacity,

        @NotNull(message = "Route ID is required")
        Long routeId
) {}