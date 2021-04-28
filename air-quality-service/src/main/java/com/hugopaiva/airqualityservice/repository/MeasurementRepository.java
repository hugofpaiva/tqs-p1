package com.hugopaiva.airqualityservice.repository;

import com.hugopaiva.airqualityservice.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findAllByDateIsLessThanEqual(Date date);
    Optional<Measurement> findByLatitudeAndLongitude(Double latitude, Double longitude);

}
