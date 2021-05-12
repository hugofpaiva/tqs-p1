package com.hugopaiva.airqualityservice.resolver;

import com.hugopaiva.airqualityservice.connection.HttpClient;
import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import com.hugopaiva.airqualityservice.model.Measurement;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenWeatherMeasurementResolverTest {

    @Mock
    HttpClient httpClient;

    @Mock
    Environment environments;

    @InjectMocks
    OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    @Test
    public void testGetActualMeasurement() throws APINotRespondingException, IOException, URISyntaxException, ParseException {
        when(environments.getProperty("openweathermap.api.key")).thenReturn("token");

        when(httpClient.get("https://api.openweathermap.org/data/2.5/air_pollution?appid=" +
                environments.getProperty("openweathermap.api.key") + "&lat=" + 38.7167 + "&lon=" + -9.1333))
                .thenReturn("{\"coord\":{\"lon\":-9.1333,\"lat\":38.7167},\"list\":[{\"main\":{\"aqi\":2},\"components\":{\"co\":190.26,\"no\":0,\"no2\":2.59,\"o3\":90.12,\"so2\":1.74,\"pm2_5\":1.78,\"pm10\":6.14,\"nh3\":0.22},\"dt\":1620784800}]}");

        Measurement result = openWeatherMeasurementResolver.getActualMeasurement(38.7167, -9.1333);

        assertThat(result.getLatitude()).isEqualTo(38.7167);
        assertThat(result.getLongitude()).isEqualTo(-9.1333);
        assertThat(result.getCo()).isEqualTo(190.26);
        assertThat(result.getNo()).isEqualTo(0.0);
        assertThat(result.getNo2()).isEqualTo(2.59);
        assertThat(result.getO3()).isEqualTo(90.12);
        assertThat(result.getSo2()).isEqualTo(1.74);
        assertThat(result.getPm25()).isEqualTo(1.78);
        assertThat(result.getPm10()).isEqualTo(6.14);
        assertThat(result.getNh3()).isEqualTo(0.22);

        verify(httpClient, times(1)).get(anyString());
    }

    @Test
    public void testJsonToMeasurementErrorParse(){
        assertThrows(ParseException.class, () -> {
            openWeatherMeasurementResolver.jsonToMeasurement("}{}{}⁄", 40.0, 40.0);
        });
    }

    @Test
    public void testGetLocationCoordinates() throws APINotRespondingException, IOException, URISyntaxException, ParseException {
        when(environments.getProperty("openweathermap.api.key")).thenReturn("token");

        when(httpClient.get("https://api.openweathermap.org/geo/1.0/direct?appid=" +
                environments.getProperty("openweathermap.api.key") + "&q=" + "Lisboa"))
                .thenReturn("[{\"name\":\"Lisbon\",\"local_names\":{\"af\":\"Lissabon\",\"ar\":\"لشبونة\",\"ascii\":\"Lisbon\",\"az\":\"Lissabon\",\"bg\":\"Лисабон\",\"ca\":\"Lisboa\",\"da\":\"Lissabon\",\"de\":\"Lissabon\",\"el\":\"Λισαβώνα\",\"en\":\"Lisbon\",\"eu\":\"Lisboa\",\"fa\":\"لیسبون\",\"feature_name\":\"Lisbon\",\"fi\":\"Lissabon\",\"fr\":\"Lisbonne\",\"gl\":\"Lisboa\",\"he\":\"ליסבון\",\"hi\":\"लिस्बन\",\"hr\":\"Lisabon\",\"hu\":\"Lisszabon\",\"id\":\"Lisboa\",\"it\":\"Lisbona\",\"ja\":\"リスボン\",\"la\":\"Olisipo\",\"lt\":\"Lisabona\",\"nl\":\"Lissabon\",\"no\":\"Lisboa\",\"pl\":\"Lizbona\",\"pt\":\"Lisboa\",\"ro\":\"Lisabona\",\"ru\":\"Лиссабон\",\"sk\":\"Lisabon\",\"sl\":\"Lizbona\",\"sr\":\"Лисабон\",\"th\":\"ลิสบอน\",\"tr\":\"Lizbon\",\"vi\":\"Lisboa\"},\"lat\":38.7167,\"lon\":-9.1333,\"country\":\"PT\"}]");

        Map<String, String> result = openWeatherMeasurementResolver.getLocationCoordinates("Lisboa");

        assertThat(result.get("location")).isEqualTo("Lisbon, PT");
        assertThat(result.get("latitude")).isEqualTo(String.valueOf(38.7167));
        assertThat(result.get("longitude")).isEqualTo(String.valueOf(-9.1333));

        verify(httpClient, times(1)).get(anyString());
    }

    @Test
    public void testJsonToGeoCoordinatesErrorParse(){
        assertThrows(ParseException.class, () -> {
            openWeatherMeasurementResolver.jsonToGeoCoordinates("}{}{}⁄");
        });
    }

}