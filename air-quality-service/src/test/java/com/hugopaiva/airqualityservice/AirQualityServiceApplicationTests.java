package com.hugopaiva.airqualityservice;

import com.hugopaiva.airqualityservice.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
class AirQualityServiceApplicationTests {

    @SpyBean
    private Cache cache;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMain() {
        AirQualityServiceApplication.main(new String[]{});
    }

    @Test
    void testSchedulerWithContext() {
        await()
                .atMost(Duration.ofSeconds(65))
                .untilAsserted(() -> verify(cache, atLeast(1))
                        .cleanExpiredCachedMeasurements());

    }

}
