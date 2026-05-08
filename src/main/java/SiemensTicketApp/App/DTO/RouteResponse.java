package SiemensTicketApp.App.DTO;

import java.util.List;

public record RouteResponse(
        Long id,
        String name,
        List<String> stations
) {}