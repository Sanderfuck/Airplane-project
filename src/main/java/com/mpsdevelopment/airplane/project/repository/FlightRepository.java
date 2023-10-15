package com.mpsdevelopment.airplane.project.repository;

import com.mpsdevelopment.airplane.project.model.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FlightRepository extends MongoRepository<Flight, Long> {
}
