package com.mpsdevelopment.airplane.project.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirplaneCharacteristics {
    private double maxSpeed;
    private double maxAcceleration;
    private double altitudeChangeSpeed;
    private double changeDirectionSpeed;
}
