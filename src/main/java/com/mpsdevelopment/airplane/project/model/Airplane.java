package com.mpsdevelopment.airplane.project.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("airplanes")
@Data
@Builder
public class Airplane {
    @Id
    private Long id;
    private AirplaneCharacteristics airplaneCharacteristics;
    private TemporaryPoint position;
    private List<Flight> flights;
}
