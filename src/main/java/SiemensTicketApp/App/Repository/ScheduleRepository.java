package SiemensTicketApp.App.Repository;

import SiemensTicketApp.App.Model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByTrainId(Long trainId);
}
