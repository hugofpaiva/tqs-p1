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

    private Double pm10;

    private Double co;

    private Double no2;

    private Double nh3;

    private Double o3;

    private Double so2;

    private Double no;

    private Double pm25;

    private Double temperature;

    private Double wind;

    private Double humidity;

    private Double pressure;

}
