package SiemensTicketApp.App.Service;

import SiemensTicketApp.App.DTO.RouteRequest;
import SiemensTicketApp.App.DTO.RouteResponse;
import SiemensTicketApp.App.DTO.TrainRequest;
import SiemensTicketApp.App.DTO.TrainResponse;
import SiemensTicketApp.App.Model.*;
import SiemensTicketApp.App.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final StationRepository stationRepository;
    private final EmailService emailService;

    // --- Train operations ---
    public Train addTrain(TrainRequest trainRequest) {
        Route route = routeRepository.findById(trainRequest.routeId()).orElseThrow( ()
                -> new IllegalArgumentException("Route not found with ID: " + trainRequest.routeId()));

        Train train = Train.builder()
                .name(trainRequest.name())
                .capacity(trainRequest.capacity())
                .route(route)
                .build();

        return trainRepository.save(train);
    }
    public void deleteTrain(Long id) { trainRepository.deleteById(id); }
    public Train updateTrain(Long id, TrainRequest updated) {

        Train train = trainRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Train not found with ID: " + id));

        Route route = routeRepository.findById(updated.routeId()).orElseThrow(() -> new IllegalArgumentException("Route not found with ID: " + updated.routeId()));

        train.setName(updated.name());
        train.setCapacity(updated.capacity());
        train.setRoute(route);

        return trainRepository.save(train);
    }
    public List<TrainResponse> getAllTrains() {

        List<TrainResponse> trains = trainRepository.findAll()
                .stream().map(train -> new TrainResponse(
                        train.getName(),
                        train.getCapacity(),
                        train.getId(),
                        train.getRoute().getId(),
                        train.getRoute().getName()
                        )
                )
                .toList();
        return trains;
    }

    // --- Route operations ---
    @Transactional
    public Route addRoute(RouteRequest request) {
        Route route = Route.builder()
                .name(request.name())
                .build();

        List<RouteStation> routeStations = new ArrayList<>();

        for (int i = 0; i < request.routeStationsId().size(); i++) {
            Long stationId = request.routeStationsId().get(i);

            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new IllegalArgumentException("Station not found with ID: " + stationId));

            RouteStation routeStation = RouteStation.builder()
                    .route(route)
                    .station(station)
                    .stopOrder(i)
                    .build();

            routeStations.add(routeStation);
        }
        route.setRouteStations(routeStations);
        return routeRepository.save(route);

    }
    public void deleteRoute(Long id) { routeRepository.deleteById(id); }
    @Transactional
    public Route updateRoute(Long id, RouteRequest request) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with ID: " + id));

        existingRoute.setName(request.name());

        if (existingRoute.getRouteStations() != null) {
            existingRoute.getRouteStations().clear();
        } else {
            existingRoute.setRouteStations(new ArrayList<>());
        }

        for (int i = 0; i < request.routeStationsId().size(); i++) {
            Long stationId = request.routeStationsId().get(i);

            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new IllegalArgumentException("Station not found with ID: " + stationId));

            RouteStation routeStation = RouteStation.builder()
                    .route(existingRoute)
                    .station(station)
                    .stopOrder(i)
                    .build();

            existingRoute.getRouteStations().add(routeStation);
        }

        return routeRepository.save(existingRoute);
    }
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(route -> new RouteResponse(
                        route.getId(),
                        route.getName(),
                        // Extracting the station names in order
                        route.getRouteStations().stream()
                                .map(routeStation -> routeStation.getStation().getName())
                                .toList()
                ))
                .toList();
    }

    // --- Bookings view ---
    public List<Booking> getBookingsForTrain(Long trainId) {
        return bookingRepository.findByScheduleTrainId(trainId);
    }

    // --- Delay ---
    @Transactional
    public void reportDelay(Long scheduleId, int delayMinutes) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        schedule.setDelayMinutes(delayMinutes);
        scheduleRepository.save(schedule);

        // Notify all customers on this schedule
        List<Booking> bookings = bookingRepository.findByScheduleId(scheduleId);
        bookings.forEach(b -> emailService.sendDelayNotification(b, delayMinutes));
    }


    // --- Stations ---
    public Station addStation(Station request) {
        return stationRepository.save(request);}
    public void deleteStation(Long id) { stationRepository.deleteById(id); }
    public Station updateStation(Long id, Station request) {stationRepository.save(request); return stationRepository.findById(id).orElseThrow( () -> new IllegalArgumentException("Station not found with ID: " + id));}

}
