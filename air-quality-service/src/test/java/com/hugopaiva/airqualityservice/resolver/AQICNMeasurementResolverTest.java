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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AQICNMeasurementResolverTest {

    @Mock
    HttpClient httpClient;

    @Mock
    Environment environments;

    @InjectMocks
    AQICNMeasurementResolver aqicnMeasurementResolver;

    @Test
    public void testGetActualMeasurement() throws APINotRespondingException, IOException, URISyntaxException, ParseException {
        when(environments.getProperty("aqicn.api.key")).thenReturn("token");

        when(httpClient.get("https://api.waqi.info/feed/geo:" + 38.7167 + ";" + -9.1333 + "/?token=" +
                environments.getProperty("aqicn.api.key")))
                .thenReturn("{\"status\":\"ok\",\"data\":{\"aqi\":25,\"idx\":8379,\"attributions\":[{\"url\":\"http://qualar.apambiente.pt/\",\"name\":\"Portugal -Agencia Portuguesa do Ambiente - Qualidade do Ar\",\"logo\":\"portugal-qualar.png\"},{\"url\":\"http://www.eea.europa.eu/themes/air/\",\"name\":\"European Environment Agency\",\"logo\":\"Europe-EEA.png\"},{\"url\":\"https://waqi.info/\",\"name\":\"World Air Quality Index Project\"}],\"city\":{\"geo\":[38.748611111111,-9.1488888888889],\"name\":\"Entrecampos, Lisboa, Portugal\",\"url\":\"https://aqicn.org/city/portugal/lisboa/entrecampos\"},\"dominentpol\":\"o3\",\"iaqi\":{\"dew\":{\"v\":10},\"h\":{\"v\":72},\"no2\":{\"v\":16},\"o3\":{\"v\":24.8},\"p\":{\"v\":1022},\"co\":{\"v\":15},\"pm10\":{\"v\":14},\"pm25\":{\"v\":17},\"so2\":{\"v\":0.6},\"t\":{\"v\":15},\"w\":{\"v\":6.1},\"wg\":{\"v\":10.2}},\"time\":{\"s\":\"2021-05-11 22:00:00\",\"tz\":\"+01:00\",\"v\":1620770400,\"iso\":\"2021-05-11T22:00:00+01:00\"},\"forecast\":{\"daily\":{\"o3\":[{\"avg\":32,\"day\":\"2021-05-11\",\"max\":41,\"min\":26},{\"avg\":34,\"day\":\"2021-05-12\",\"max\":41,\"min\":31},{\"avg\":31,\"day\":\"2021-05-13\",\"max\":38,\"min\":21},{\"avg\":34,\"day\":\"2021-05-14\",\"max\":43,\"min\":27},{\"avg\":31,\"day\":\"2021-05-15\",\"max\":31,\"min\":27}],\"pm10\":[{\"avg\":18,\"day\":\"2021-05-11\",\"max\":22,\"min\":14},{\"avg\":15,\"day\":\"2021-05-12\",\"max\":20,\"min\":10},{\"avg\":15,\"day\":\"2021-05-13\",\"max\":18,\"min\":11},{\"avg\":11,\"day\":\"2021-05-14\",\"max\":18,\"min\":8},{\"avg\":10,\"day\":\"2021-05-15\",\"max\":10,\"min\":9}],\"pm25\":[{\"avg\":38,\"day\":\"2021-05-11\",\"max\":56,\"min\":26},{\"avg\":28,\"day\":\"2021-05-12\",\"max\":33,\"min\":20},{\"avg\":35,\"day\":\"2021-05-13\",\"max\":54,\"min\":23},{\"avg\":29,\"day\":\"2021-05-14\",\"max\":39,\"min\":19},{\"avg\":34,\"day\":\"2021-05-15\",\"max\":34,\"min\":31}],\"uvi\":[{\"avg\":0,\"day\":\"2021-05-11\",\"max\":6,\"min\":0},{\"avg\":1,\"day\":\"2021-05-12\",\"max\":7,\"min\":0},{\"avg\":1,\"day\":\"2021-05-13\",\"max\":4,\"min\":0},{\"avg\":1,\"day\":\"2021-05-14\",\"max\":9,\"min\":0},{\"avg\":1,\"day\":\"2021-05-15\",\"max\":9,\"min\":0},{\"avg\":1,\"day\":\"2021-05-16\",\"max\":9,\"min\":0}]}},\"debug\":{\"sync\":\"2021-05-12T09:33:15+09:00\"}}}");

        Measurement result = aqicnMeasurementResolver.getActualMeasurement(38.7167, -9.1333);

        assertThat(result.getLatitude()).isEqualTo(38.7167);
        assertThat(result.getLongitude()).isEqualTo(-9.1333);
        assertThat(result.getO3()).isEqualTo(24.8);
        assertThat(result.getSo2()).isEqualTo(0.6);
        assertThat(result.getHumidity()).isEqualTo(72);
        assertThat(result.getPressure()).isEqualTo(1022);
        assertThat(result.getPm10()).isEqualTo(14);
        assertThat(result.getPm25()).isEqualTo(17);
        assertThat(result.getTemperature()).isEqualTo(15);
        assertThat(result.getSo2()).isEqualTo(0.6);
        assertThat(result.getWind()).isEqualTo(6.1);
        assertThat(result.getCo()).isEqualTo(15);

        verify(httpClient, times(1)).get(anyString());
    }

    @Test
    public void testJsonToMeasurementErrorParse(){
        assertThrows(ParseException.class, () -> {
            aqicnMeasurementResolver.jsonToMeasurement("}{}{}⁄", 40.0, 40.0);
        });
    }

}