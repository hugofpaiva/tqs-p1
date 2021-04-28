package com.hugopaiva.airqualityservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Date;

@Data
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @NotNull(message = "IP is mandatory")
    private URL ip;

    public Request(CacheResponseState cacheResponse, Double latitude, Double longitude) {
        this.cacheResponse = cacheResponse;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Request(CacheResponseState cacheResponse, String airQualityLocationAccessed, Double latitude, Double longitude) {
        this.cacheResponse = cacheResponse;
        this.airQualityLocationAccessed = airQualityLocationAccessed;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
