package com.ll.statistics;

import com.ll.statistics.controller.AddDataPointsController;
import com.ll.statistics.controller.AddDataPointsRequest;
import com.ll.statistics.domain.DataPoints;
import com.ll.statistics.domain.Statistics;
import com.ll.statistics.domain.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PerformanceTest {

    private Random random;
    private RestClient restClient;

    private ExecutorService executorService;
    @BeforeEach
    public void setUp() {
        this.random = new Random();
        this.restClient = RestClient.create();
        executorService = Executors.newFixedThreadPool(10);
    }

    @Test
    @Tag("performance")
    void performanceTest() throws InterruptedException {
        System.out.println("Preparing test data.");
        int numberOfBatches = DataPoints.DEFAULT_MAX_NUMBER_OF_DATA_POINTS / AddDataPointsController.MAX_BATCH_SIZE;
        Arrays.stream(new Symbol[] {Symbol.S1, Symbol.S2}).forEach(
                symbol -> {
                    for (int i = 0; i < numberOfBatches; i++) {
                        int batchIndex = i +1;
                        executorService.execute(()-> {
                            List<Float> dataPoints = new ArrayList<>(AddDataPointsController.MAX_BATCH_SIZE);
                            for (int j = 0; j < AddDataPointsController.MAX_BATCH_SIZE; j++) {
                                dataPoints.add((random.nextFloat(1000f)));
                            }
                            ResponseEntity<Void> addResponse = restClient.post().uri("http://localhost:8080/add_batch")
                                    .body(new AddDataPointsRequest(symbol, dataPoints)).retrieve().toEntity(Void.class);
                            assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
                            System.out.println("Processed batch no: " + batchIndex + "/" + numberOfBatches + " for symbol " + symbol);
                        });
                    }
                }
        );
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        System.out.println("Test data ready");

        executorService = Executors.newFixedThreadPool(20);
        int numberOfOperations = 1;
        long startTime = System.nanoTime();
        runStats(random, numberOfOperations);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        long endTime = System.nanoTime();
        System.out.println(numberOfOperations * 5 + " operations: " + ((endTime - startTime) / 1000000) + " milliseconds");

        executorService = Executors.newFixedThreadPool(20);
        numberOfOperations = 10;
        startTime = System.nanoTime();
        runStats(random, numberOfOperations);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        endTime = System.nanoTime();
        System.out.println(numberOfOperations * 5 + " operations: " + ((endTime - startTime) / 1000000) + " milliseconds");

        executorService = Executors.newFixedThreadPool(20);
        numberOfOperations = 100;
        startTime = System.nanoTime();
        runStats(random, numberOfOperations);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        endTime = System.nanoTime();
        System.out.println(numberOfOperations * 5 + " operations: " + ((endTime - startTime) / 1000000) + " milliseconds");

        executorService = Executors.newFixedThreadPool(20);
        numberOfOperations = 1000;
        startTime = System.nanoTime();
        runStats(random, numberOfOperations);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        endTime = System.nanoTime();
        System.out.println(numberOfOperations * 5 + " operations: " + ((endTime - startTime) / 1000000) + " milliseconds");
    }

    private void runStats(Random random, int numberOfOperations) {
        IntStream.rangeClosed(0, numberOfOperations - 1).forEach(integer -> {
            executorService.execute(() -> {
                Symbol symbol = Symbol.values()[random.nextInt(0, 2)];
                ResponseEntity<Statistics> statisticsResponse = restClient.get().uri("http://localhost:8080/stats?symbol=" + symbol + "&k=" + random.nextInt(1, 9)).retrieve().toEntity(Statistics.class);
                assertThat(statisticsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
            });
        });
    }
}
