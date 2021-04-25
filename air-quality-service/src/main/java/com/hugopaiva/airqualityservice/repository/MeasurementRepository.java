package com.hugopaiva.airqualityservice.repository;

import com.hugopaiva.airqualityservice.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

}
