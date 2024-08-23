package com.ll.statistics;

import com.ll.statistics.controller.AddDataPointsRequest;
import com.ll.statistics.domain.Statistics;
import com.ll.statistics.domain.Symbol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsRestApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAddAndReturnStats() {
        ResponseEntity<Void> addResponse = this.restTemplate.postForEntity("http://localhost:" + port +"/add_batch", new AddDataPointsRequest(Symbol.S1, List.of(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f)), Void.class);
        assertThat(addResponse.getStatusCode()).withFailMessage("/add_batch should return 200 OK, but returned: " + addResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        ResponseEntity<Statistics> statsResponse = this.restTemplate.getForEntity("http://localhost:" + port + "/stats?symbol=S1&k=1",
                Statistics.class);
        assertThat(statsResponse.getStatusCode()).withFailMessage("/stats should return 200 OK, but returned: " + statsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }

    @Test
    void shouldReturnErrorStatus() {
        ResponseEntity<Void> addResponse = this.restTemplate.postForEntity("http://localhost:" + port +"/add_batch", new AddDataPointsRequest(null, null), Void.class);
        assertThat(addResponse.getStatusCode()).withFailMessage("/stats should return 400 BAD REQUEST, but returned" + addResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        addResponse = this.restTemplate.postForEntity("http://localhost:" + port +"/add_batch", new AddDataPointsRequest(Symbol.S1, new ArrayList<>()), Void.class);
        assertThat(addResponse.getStatusCode()).withFailMessage("/stats should return 400 BAD REQUEST, but returned: " + addResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));

        ResponseEntity<Statistics> statsResponse = this.restTemplate.getForEntity("http://localhost:" + port + "/stats",
                Statistics.class);
        assertThat(statsResponse.getStatusCode()).withFailMessage("/stats should return 400 BAD REQUEST, but returned: " + statsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        statsResponse = this.restTemplate.getForEntity("http://localhost:" + port + "/stats?symbol=S1&k=1",
                Statistics.class);
        assertThat(statsResponse.getStatusCode()).withFailMessage("/stats should return 400 BAD REQUEST, but returned: " + statsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        statsResponse = this.restTemplate.getForEntity("http://localhost:" + port + "/stats?symbol=S1&k=0",
                Statistics.class);
        assertThat(statsResponse.getStatusCode()).withFailMessage("/stats should return 400 BAD REQUEST, but returned: " + statsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        statsResponse = this.restTemplate.getForEntity("http://localhost:" + port + "/stats?symbol=S1&k=9",
                Statistics.class);
        assertThat(statsResponse.getStatusCode()).withFailMessage("/stats should return 400 BAD REQUEST, but returned: " + statsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }
}
