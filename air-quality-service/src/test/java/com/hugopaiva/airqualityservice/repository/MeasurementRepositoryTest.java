package com.hugopaiva.airqualityservice.repository;

import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.ResponseSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MeasurementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Test
    public void testWhenFindByValidLocation_thenReturnMeasurement() {
        Measurement m = new Measurement();
        m.setLatitude(41.234432);
        m.setLongitude(43.234432);
        m.setAirQualityIndex(50);
        m.setResponseSource(ResponseSource.AQICN);
        m.setPm25(12.21);
        m.setNh3(10.1);
        m.setNo(0.4);

        entityManager.persistAndFlush(m);

        Optional<Measurement> found = measurementRepository.findByLatitudeAndLongitude(m.getLatitude(), m.getLongitude());
        assertThat(found).isNotNull();
        assertThat(found.get().equals(m));
    }

    @Test
    public void testWhenFindByInvalidLocation_thenReturnNull() {
        Measurement fromDb = measurementRepository.findByLatitudeAndLongitude(-21.423, 2123.21).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    public void testWhenFindByMeasurementByExistingId_thenReturnMeasurement() {
        Measurement m = new Measurement();
        m.setLatitude(41.234432);
        m.setLongitude(43.234432);
        m.setAirQualityIndex(50);
        m.setResponseSource(ResponseSource.AQICN);
        m.setPm25(12.21);
        m.setNh3(10.1);
        m.setNo(0.4);

        entityManager.persistAndFlush(m);

        Measurement fromDb = measurementRepository.findById(m.getId()).orElse(null);
        assertThat(fromDb).isNotNull().isEqualTo(m);
    }

    @Test
    public void testWhenFindByInvalidId_thenReturnNull() {
        Measurement fromDb = measurementRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    public void testGivenSetOfMeasurements_whenFindAllByDateIsLessThanEqual_thenReturnAllValidMeasurements() throws InterruptedException {
        // It is using @CreationTimestamp so the date it's not set

        Measurement m1 = new Measurement();
        m1.setLatitude(41.234432);
        m1.setLongitude(43.234432);
        m1.setAirQualityIndex(50);
        m1.setResponseSource(ResponseSource.AQICN);
        m1.setPm25(12.21);
        m1.setNh3(10.1);
        m1.setNo(0.4);

        Measurement m2 = new Measurement();
        m2.setLatitude(42.234432);
        m2.setLongitude(44.234432);
        m2.setResponseSource(ResponseSource.OPENWEATHER);
        m2.setAirQualityIndex(56);
        m2.setPm25(13.21);
        m2.setNh3(11.1);
        m2.setNo(1.4);

        Measurement m3 = new Measurement();
        m3.setLatitude(45.234432);
        m3.setLongitude(46.234432);
        m3.setAirQualityIndex(76);
        m3.setResponseSource(ResponseSource.AQICN);
        m3.setPm25(15.21);
        m3.setNh3(14.1);
        m3.setNo(3.4);

        entityManager.persist(m1);
        entityManager.persist(m2);
        entityManager.flush();

        Date afterSaveM1M2 = new Date(System.currentTimeMillis());

        TimeUnit.SECONDS.sleep(1);

        entityManager.persist(m3);
        entityManager.flush();

        List<Measurement> allValidMeasurements = measurementRepository.findAllByDateIsLessThanEqual(afterSaveM1M2);

        assertThat(allValidMeasurements).hasSize(2).extracting(Measurement::getLatitude).containsOnly(m1.getLatitude(),
                m2.getLatitude());
    }

    @Test
    public void testGivenSetOfMeasurements_whenFindAll_thenReturnAllMeasurements() {
        Measurement m1 = new Measurement();
        m1.setLatitude(41.234432);
        m1.setLongitude(43.234432);
        m1.setAirQualityIndex(50);
        m1.setResponseSource(ResponseSource.AQICN);
        m1.setPm25(12.21);
        m1.setNh3(10.1);
        m1.setNo(0.4);

        Measurement m2 = new Measurement();
        m2.setLatitude(42.234432);
        m2.setLongitude(44.234432);
        m2.setResponseSource(ResponseSource.OPENWEATHER);
        m2.setAirQualityIndex(56);
        m2.setPm25(13.21);
        m2.setNh3(11.1);
        m2.setNo(1.4);

        Measurement m3 = new Measurement();
        m3.setLatitude(45.234432);
        m3.setLongitude(46.234432);
        m3.setAirQualityIndex(76);
        m3.setResponseSource(ResponseSource.AQICN);
        m3.setPm25(15.21);
        m3.setNh3(14.1);
        m3.setNo(3.4);

        entityManager.persist(m1);
        entityManager.persist(m2);
        entityManager.persist(m3);
        entityManager.flush();

        List<Measurement> allMeasurements = measurementRepository.findAll();

        assertThat(allMeasurements).hasSize(3).extracting(Measurement::getLatitude).containsOnly(m1.getLatitude(),
                m2.getLatitude(), m3.getLatitude());
    }

}