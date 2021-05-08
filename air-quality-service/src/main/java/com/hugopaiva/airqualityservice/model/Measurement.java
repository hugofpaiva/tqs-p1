package com.hugopaiva.airqualityservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date date;

    private String location;

    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Response Source is mandatory")
    private ResponseSource responseSource;

    @NotNull(message = "Latitude is mandatory")
    @Max(90)
    @Min(-90)
    private Double latitude;

    @NotNull(message = "Longitude is mandatory")
    @Max(180)
    @Min(-180)
    private Double longitude;

    @NotNull(message = "Air Quality Index is mandatory")
    @Min(0)
    @Max(500)
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
