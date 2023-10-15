package com.mpsdevelopment.airplane.project.service;

import com.mpsdevelopment.airplane.project.exception.CustomFlightException;
import com.mpsdevelopment.airplane.project.model.Airplane;
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
    private List<WayPoint> wayPoints;
    @Value("${airplane.quantity}")
    private int airplanesQuantity;

    @Scheduled(fixedDelay = 60000)
    public void prepareStartFlight() {
        createWayPoints();

        if (airplanes.isEmpty()) {
            createAirplanePool(airplanesQuantity);
        }

        initAirplaneFlightMap();

        printPreviousFlightsInfo();

        try {
            fly();
        } catch (CustomFlightException exception) {
            log.error("Flight data didn`t saved to DB", exception);
        }
    }

    private void initAirplaneFlightMap() {
        airplaneFlightMap = airplanes.stream()
                .collect(Collectors.toMap(airplane -> airplane,
                        airplane -> planeCalculation.calculateRoute(
                                airplane.getAirplaneCharacteristics(), wayPoints)));
    }

    private void createAirplanePool(int numOfAirplanes) {
        for (int airplaneNum = 1; airplaneNum <= numOfAirplanes; airplaneNum++) {
            Airplane airplane = createAirplane(airplaneNum, random.nextInt(MIN_FLY_SPEED, MAX_FLY_SPEED),
                    random.nextInt(MIN_ALTITUDE_SPEED, MAX_ALTITUDE_SPEED));
            airplanes.add(airplane);
        }
    }

    private void fly() {
        for (Map.Entry<Airplane, List<TemporaryPoint>> entry : airplaneFlightMap.entrySet()) {
            Airplane airplane = entry.getKey();
            List<TemporaryPoint> temporaryPoints = entry.getValue();
            Flight currentFlight = buildFlight(wayPoints);

            for (TemporaryPoint temporaryPoint : temporaryPoints) {
                airplane.setPosition(temporaryPoint);
                currentFlight.getPassedPoints().add(temporaryPoint);
            }

            currentFlight.setFlightDuration(temporaryPoints.size() * planeCalculation.getTIME_POINT_STEP());
            airplane.getFlights().add(currentFlight);

            airplaneRepository.save(airplane);
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
        wayPoints = new ArrayList<>();

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


