package SiemensTicketApp.App.DTO;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripOption {
    private List<String> stations;   // full path e.g. [Budapest, Bratislava, Vienna, Prague]
    private List<String> trainNames; // trains used e.g. [IC-101, IC-202]
    private List<String> departureTimes;
    private boolean requiresChangeover;
}
