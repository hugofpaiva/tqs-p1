package com.hugopaiva.airqualityservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MeasurementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MeasurementRepository measurementRepository;

}