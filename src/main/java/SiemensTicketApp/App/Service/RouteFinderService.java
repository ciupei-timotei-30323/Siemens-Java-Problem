package SiemensTicketApp.App.Service;

import SiemensTicketApp.App.DTO.TripOption;
import SiemensTicketApp.App.Model.*;
import SiemensTicketApp.App.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteFinderService {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;

    public List<TripOption> findTrips(String originName, String destinationName) {
        Station origin = stationRepository.findByName(originName)
                .orElseThrow(() -> new IllegalArgumentException("Origin station not found: " + originName));
        Station destination = stationRepository.findByName(destinationName)
                .orElseThrow(() -> new IllegalArgumentException("Destination station not found: " + destinationName));

        List<TripOption> results = new ArrayList<>();
        List<Route> allRoutes = routeRepository.findAll();

        // Checking direct connections first
        for (Route route : allRoutes) {
            List<RouteStation> stops = route.getRouteStations();
            int originIndex = indexOfStation(stops, origin.getId());
            int destIndex = indexOfStation(stops, destination.getId());

            List<Train> trains = trainRepository.findByRouteId(route.getId());

            if (originIndex != -1 && destIndex != -1 && originIndex < destIndex) {
                // Direct connection found on this route
                List<String> path = stops.subList(originIndex, destIndex + 1)
                        .stream().map(rs -> rs.getStation().getName()).toList();

                List<Schedule> schedules = new ArrayList<>();
                for (Train train : trains) {
                    schedules.addAll(scheduleRepository.findByTrainId(train.getId()));
                }


                List<String> trainNames = trains.stream().map(Train::getName).toList();
                List<String> departureTimes = schedules.stream()
                        .map(s -> s.getDepartureTime().toString()).toList();

                results.add(new TripOption(path, trainNames, departureTimes, false));
            }
        }

        // Check one-changeover connections
        for (Route routeA : allRoutes) {
            List<RouteStation> stopsA = routeA.getRouteStations();
            int originIndex = indexOfStation(stopsA, origin.getId());
            if (originIndex == -1) continue;

            for (Route routeB : allRoutes) {
                if (routeA.getId().equals(routeB.getId())) continue;
                List<RouteStation> stopsB = routeB.getRouteStations();
                int destIndex = indexOfStation(stopsB, destination.getId());
                if (destIndex == -1) continue;

                // Find a shared station (changeover point)
                for (int i = originIndex + 1; i < stopsA.size(); i++) {
                    Long changeoverStationId = stopsA.get(i).getStation().getId();
                    int changeoverIndexB = indexOfStation(stopsB, changeoverStationId);

                    if (changeoverIndexB != -1 && changeoverIndexB < destIndex) {
                        // Valid changeover found
                        List<String> pathA = stopsA.subList(originIndex, i + 1)
                                .stream().map(rs -> rs.getStation().getName()).toList();
                        List<String> pathB = stopsB.subList(changeoverIndexB + 1, destIndex + 1)
                                .stream().map(rs -> rs.getStation().getName()).toList();

                        List<String> fullPath = new ArrayList<>(pathA);
                        fullPath.addAll(pathB);

                        results.add(TripOption.builder()
                                .stations(fullPath)
                                .trainNames(List.of(getTrainNameForRoute(routeA), getTrainNameForRoute(routeB)))
                                .departureTimes(getDepartureTimes(routeA, routeB))
                                .requiresChangeover(true)
                                .build());
                    }
                }
            }
        }

        if (results.isEmpty()) {
            throw new IllegalArgumentException(
                    "No connection found between " + originName + " and " + destinationName
            );
        }

        return results;
    }

    private int indexOfStation(List<RouteStation> stops, Long stationId) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStation().getId().equals(stationId)) return i;
        }
        return -1;
    }

    private String getTrainNameForRoute(Route route) {
        if(route.getRouteStations().isEmpty()){
            return "No train";
        }
        List<Train> trains = trainRepository.findByRouteId(route.getId());

        if(trains.isEmpty()){
            return "No train";
        }
        return trains.get(0).getName();
    }

    private List<String> getDepartureTimes(Route... routes) {

        List<String> departureTimes = new ArrayList<>();
        for (Route route : routes) {

            List<Train> trains = trainRepository.findByRouteId(route.getId());
            if(trains.isEmpty()){ continue; }

            for(Train train : trains){
                scheduleRepository.findByTrainId(train.getId())
                        .forEach(s -> departureTimes.add(s.getDepartureTime().toString()));
            }
        }

        return departureTimes;

    }
}
