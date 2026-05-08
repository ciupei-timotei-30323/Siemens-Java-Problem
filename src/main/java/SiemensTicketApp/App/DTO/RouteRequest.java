package SiemensTicketApp.App.DTO;

import SiemensTicketApp.App.Model.RouteStation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RouteRequest(
        @NotNull(message="Name cannot be null")
        String name,

        @NotNull(message="Route stations cannot be null")
        List<Long> routeStationsId

) {
}
