package com.mpsdevelopment.airplane.project.repository;

import com.mpsdevelopment.airplane.project.model.Airplane;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirplaneRepository extends MongoRepository<Airplane, Long> {

}
