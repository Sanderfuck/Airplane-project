package com.mpsdevelopment.airplane.project.service;

import com.mpsdevelopment.airplane.project.exception.CustomFlightException;
import com.mpsdevelopment.airplane.project.model.Airplane;
import com.mpsdevelopment.airplane.project.model.AirplaneCharacteristics;
import com.mpsdevelopment.airplane.project.model.Flight;
import com.mpsdevelopment.airplane.project.model.TemporaryPoint;
import com.mpsdevelopment.airplane.project.model.WayPoint;
import com.mpsdevelopment.airplane.project.repository.AirplaneRepository;
import com.mpsdevelopment.airplane.project.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.mpsdevelopment.airplane.project.constants.FlightConstants.MAX_ALTITUDE_SPEED;
import static com.mpsdevelopment.airplane.project.constants.FlightConstants.MAX_FLY_SPEED;
import static com.mpsdevelopment.airplane.project.constants.FlightConstants.MAX_WAYPOINTS;
import static com.mpsdevelopment.airplane.project.constants.FlightConstants.MIN_ALTITUDE_SPEED;
import static com.mpsdevelopment.airplane.project.constants.FlightConstants.MIN_FLY_SPEED;
import static com.mpsdevelopment.airplane.project.constants.FlightConstants.MIN_WAYPOINTS;
import static com.mpsdevelopment.airplane.project.utils.AirplaneUtils.buildAirplane;
import static com.mpsdevelopment.airplane.project.utils.AirplaneUtils.buildCharacteristics;
import static com.mpsdevelopment.airplane.project.utils.AirplaneUtils.buildFlight;
import static com.mpsdevelopment.airplane.project.utils.AirplaneUtils.buildWaypoint;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightService {
    private final PlaneCalculation planeCalculation;
    private final AirplaneRepository airplaneRepository;
    private final FlightRepository flightRepository;
    private final Random random = new Random();
    private final List<Airplane> airplanes = new ArrayList<>();
    private Map<Airplane, List<TemporaryPoint>> airplaneFlightMap;
    @Value("${airplane.quantity}")
    private int airplanesQuantity;

    @Scheduled(fixedDelay = 600000)
    public void prepareStartFlight() {
        List<WayPoint> wayPoints = createWayPoints();

        if (airplanes.isEmpty()) {
            createAirplanePool(airplanesQuantity, wayPoints);
        }

        initAirplaneFlightMap(wayPoints);

        printPreviousFlightsInfo();

        try {
            fly();
        } catch (CustomFlightException exception) {
            log.error("Flight data didn`t saved to DB", exception);
        }
    }

    private void initAirplaneFlightMap(List<WayPoint> wayPoints) {
        airplaneFlightMap = airplanes.stream()
                .collect(Collectors.toMap(airplane -> airplane,
                        airplane -> planeCalculation.calculateRoute(
                                airplane.getAirplaneCharacteristics(), wayPoints)));
    }

    private void createAirplanePool(int numOfAirplanes, List<WayPoint> wayPoints) {
        for (int airplaneNum = 1; airplaneNum <= numOfAirplanes; airplaneNum++) {
            Airplane airplane = createAirplane(airplaneNum, random.nextInt(MIN_FLY_SPEED, MAX_FLY_SPEED),
                    random.nextInt(MIN_ALTITUDE_SPEED, MAX_ALTITUDE_SPEED));
//            Flight flight = buildFlight(wayPoints);
//            airplane.getFlights().add(flight);
            airplanes.add(airplane);
        }
    }

    private void fly() {
        for (Map.Entry<Airplane, List<TemporaryPoint>> entry : airplaneFlightMap.entrySet()) {
            Airplane airplane = entry.getKey();
            List<Flight> flightList = airplane.getFlights();

            Flight flight = buildFlight(wayPoints);
//            Flight currentFlight = flightList.get(flightList.size() - 1);
            List<TemporaryPoint> temporaryPoints = entry.getValue();

            for (TemporaryPoint temporaryPoint : temporaryPoints) {
                airplane.setPosition(temporaryPoint);
                currentFlight.getPassedPoints().add(temporaryPoint);

                airplaneRepository.save(airplane);
            }
            currentFlight.setFlightDuration(temporaryPoints.size() * planeCalculation.getTIME_POINT_STEP());
            flightRepository.save(currentFlight);
        }
    }

    private void printPreviousFlightsInfo() {
        for (Airplane airplane : airplanes) {
            Long airplaneId = airplane.getId();
            List<Flight> prevFlights = airplane.getFlights();
            double totalFlightsDuration = prevFlights.stream()
                    .map(Flight::getFlightDuration)
                    .collect(Collectors.summarizingDouble(Double::doubleValue))
                    .getSum();

            log.info("Previous flights for Airplane ID {} : quantity - {}, total duration - {}",
                    airplaneId, prevFlights.size(), totalFlightsDuration);
        }
    }

    private Airplane createAirplane(int airplaneId, double speedFlying, double speedAltitude) {
        Airplane airplane = buildAirplane(airplaneId, buildCharacteristics(speedFlying, speedAltitude));
        log.info("Created airplane {} ", airplane.toString());
        return airplane;
    }

    private List<WayPoint> createWayPoints() {
        List<WayPoint> wayPoints = new ArrayList<>();

        int wayPointsNumber = random.nextInt(MIN_WAYPOINTS, MAX_WAYPOINTS);
        for (int i = 0; i < wayPointsNumber; i++) {
            int latitude = random.nextInt(90);
            int longitude = random.nextInt(90);
            int height = random.nextInt(90);
            WayPoint wayPoint = buildWaypoint(latitude, longitude, height);
            wayPoints.add(wayPoint);
            log.info("Created waypoint {} ", wayPoint.toString());
        }
        return wayPoints;
    }
}


