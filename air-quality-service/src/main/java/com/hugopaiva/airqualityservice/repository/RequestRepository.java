package com.hugopaiva.airqualityservice.repository;

import com.hugopaiva.airqualityservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
}
