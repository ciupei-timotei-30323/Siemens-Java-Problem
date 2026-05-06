package SiemensTicketApp.App.Repository;

import SiemensTicketApp.App.Model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
