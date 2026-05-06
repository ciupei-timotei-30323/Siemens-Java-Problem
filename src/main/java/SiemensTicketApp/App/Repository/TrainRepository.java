package SiemensTicketApp.App.Repository;

import SiemensTicketApp.App.Model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Long> {
}