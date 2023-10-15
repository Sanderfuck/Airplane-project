package com.mpsdevelopment.airplane.project.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WayPoint {
    private double latitude;
    private double longitude;
    private double height;
    private double speed;
}
