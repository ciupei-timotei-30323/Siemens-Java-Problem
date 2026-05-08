package SiemensTicketApp.App.Repository;

import SiemensTicketApp.App.Model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByRouteId(Long routeId);
}