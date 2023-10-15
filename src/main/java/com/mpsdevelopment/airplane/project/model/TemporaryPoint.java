package com.mpsdevelopment.airplane.project.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemporaryPoint {
    private double latitude;
    private double longitude;
    private double flightHeight;
    private double flightSpeed;
    private double flightDirection;
}
