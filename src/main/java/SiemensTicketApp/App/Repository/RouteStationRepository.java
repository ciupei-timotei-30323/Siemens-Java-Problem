package SiemensTicketApp.App.Repository;

import SiemensTicketApp.App.Model.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
    List<RouteStation> findByStationId(Long stationId);
}