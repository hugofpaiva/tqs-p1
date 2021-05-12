package com.hugopaiva.airqualityservice.connection;

import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class HttpClientTest {

    private HttpClient httpClient = new HttpClient();

    @Test
    void testGetInvalidUrl() {
        assertThrows(APINotRespondingException.class, () -> {
            httpClient.get("asdasda");
        });
    }

    @Test
    void testGetValidUrl() throws APINotRespondingException, IOException {
        assertThat(httpClient.get("http://google.com")).isNotEmpty();
    }

}