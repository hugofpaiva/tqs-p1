package com.hugopaiva.airqualityservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date date;

    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Cache Response is mandatory")
    private CacheResponseState cacheResponse;

    private String airQualityLocationAccessed;

    @NotNull(message = "Latitude is mandatory")
    @Max(90)
    @Min(-90)
    private Double latitude;

    @NotNull(message = "Longitude is mandatory")
    @Max(180)
    @Min(-180)
    private Double longitude;

    public Request(CacheResponseState cacheResponse, Double latitude, Double longitude) {
        this.cacheResponse = cacheResponse;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Request(CacheResponseState cacheResponse, Double latitude, Double longitude, String airQualityLocationAccessed) {
        this.cacheResponse = cacheResponse;
        this.latitude = latitude;
        this.longitude = longitude;
        this.airQualityLocationAccessed = airQualityLocationAccessed;
    }
}
