package com.mpsdevelopment.airplane.project.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("flights")
@Data
@Builder
public class Flight {
    @Id
    private Long number;
    private List<WayPoint> points;
    private List<TemporaryPoint> passedPoints;
    private double flightDuration;
}
