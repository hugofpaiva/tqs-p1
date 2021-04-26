package com.hugopaiva.airqualityservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String location;

    @NotNull(message = "Latitude is mandatory")
    @Max(90)
    @Min(-90)
    private Double latitude;

    @NotNull(message = "Longitude is mandatory")
    @Max(180)
    @Min(-180)
    private Double longitude;

    @NotNull(message = "Air Quality Index is mandatory")
    @Max(0)
    @Min(500)
    private Integer airQualityIndex;

    @NotNull(message = "PM10 is mandatory")
    private Double pm10;

    @NotNull(message = "CO is mandatory")
    private Double co;

    @NotNull(message = "NO2 is mandatory")
    private Double no2;

    @NotNull(message = "O3 is mandatory")
    private Double o3;

    @NotNull(message = "SO2 is mandatory")
    private Double so2;

    public Measurement(String location, Double latitude, Double longitude, Integer airQualityIndex, Double pm10, Double co, Double no2, Double o3, Double so2) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.airQualityIndex = airQualityIndex;
        this.pm10 = pm10;
        this.co = co;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
    }

    public Measurement(Double latitude, Double longitude, Integer airQualityIndex, Double pm10, Double co, Double no2, Double o3, Double so2) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.airQualityIndex = airQualityIndex;
        this.pm10 = pm10;
        this.co = co;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
    }
}
