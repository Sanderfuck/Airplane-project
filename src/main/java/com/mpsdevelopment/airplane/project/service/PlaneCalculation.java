package com.mpsdevelopment.airplane.project.service;

import com.mpsdevelopment.airplane.project.model.AirplaneCharacteristics;
import com.mpsdevelopment.airplane.project.model.TemporaryPoint;
import com.mpsdevelopment.airplane.project.model.WayPoint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaneCalculation {
    @Value("${time.step.temporary_point}")
    @Getter
    private double TIME_POINT_STEP;
    private WayPoint startPoint;
    private WayPoint endPoint;
    private double flyOfPointsDuration;
    @Getter
    private double totalFlyDuration;

    public List<TemporaryPoint> calculateRoute(AirplaneCharacteristics airplaneCharacteristics,
                                               List<WayPoint> wayPoints) {

        List<TemporaryPoint> points = new ArrayList<>();
        for (int i = 1; i < wayPoints.size(); i++) {
            startPoint = wayPoints.get(i);
            endPoint = wayPoints.get(i - 1);
            flyOfPointsDuration = calculatePointsTime(airplaneCharacteristics);
            totalFlyDuration += flyOfPointsDuration;
            for (double spentTime = 0; spentTime < flyOfPointsDuration; spentTime += TIME_POINT_STEP) {
                TemporaryPoint temporaryPoint = buildTemporaryPoint(spentTime);
                points.add(temporaryPoint);
            }
        }
        return points;
    }

    private TemporaryPoint buildTemporaryPoint(double spentTime) {
        double coordinateLatitudeTemporaryPoint =
                getCoordinateTemporaryPoint(startPoint.getLatitude(), endPoint.getLatitude(), spentTime);

        double coordinateLongitudeTemporaryPoint =
                getCoordinateTemporaryPoint(startPoint.getLongitude(), endPoint.getLongitude(), spentTime);

        double coordinateHeightTemporaryPoint =
                getCoordinateTemporaryPoint(startPoint.getHeight(), endPoint.getHeight(), spentTime);

        return TemporaryPoint.builder()
                .latitude(coordinateLatitudeTemporaryPoint)
                .longitude(coordinateLongitudeTemporaryPoint)
                .flightHeight(coordinateHeightTemporaryPoint)
                .flightDirection(defineFlightDirection())
                .build();
    }

    private double calculatePointsTime(AirplaneCharacteristics airplaneCharacteristics) {
        return calculatePointsDistance() / airplaneCharacteristics.getMaxSpeed();
    }

    private double getCoordinateTemporaryPoint(double prevCoordinate, double nextCoordinate, double spentTime) {
        return prevCoordinate + (nextCoordinate - prevCoordinate) * spentTime / flyOfPointsDuration;
    }

    private double defineFlightDirection() {
        double latitudeDelta = endPoint.getLatitude() - startPoint.getLatitude();
        double longitudeDelta = endPoint.getLongitude() - startPoint.getLongitude();
        return Math.abs(Math.atan2(latitudeDelta, longitudeDelta));
    }
    private double calculatePointsDistance() {
        return Math.sqrt(Math.pow(endPoint.getHeight() - startPoint.getHeight(), 2)
                + Math.pow(endPoint.getLatitude() - startPoint.getLatitude(), 2)
                + Math.pow(endPoint.getLongitude() - startPoint.getLongitude(), 2));
    }
}
