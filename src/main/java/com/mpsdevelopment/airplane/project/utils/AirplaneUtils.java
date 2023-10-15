package com.mpsdevelopment.airplane.project.utils;

import com.mpsdevelopment.airplane.project.model.Airplane;
import com.mpsdevelopment.airplane.project.model.AirplaneCharacteristics;
import com.mpsdevelopment.airplane.project.model.Flight;
import com.mpsdevelopment.airplane.project.model.WayPoint;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class AirplaneUtils {
    public static Airplane buildAirplane(int id, AirplaneCharacteristics airplaneCharacteristics) {
        return Airplane.builder()
                .id((long) id)
                .airplaneCharacteristics(airplaneCharacteristics)
                .flights(new ArrayList<>())
                .build();
    }

    public static AirplaneCharacteristics buildCharacteristics(double maxSpeed, double changeAltitudeSpeed) {
        return AirplaneCharacteristics
                .builder()
                .maxSpeed(maxSpeed)
                .altitudeChangeSpeed(changeAltitudeSpeed)
                .build();
    }

    public static WayPoint buildWaypoint(double latitude, double longitude, double height) {
        return WayPoint.builder()
                .latitude(latitude)
                .longitude(longitude)
                .height(height)
                .build();
    }

    public static Flight buildFlight(List<WayPoint> wayPoints) {
        return Flight.builder()
                .number(System.currentTimeMillis())
                .points(wayPoints)
                .passedPoints(new ArrayList<>())
                .build();
    }
}
