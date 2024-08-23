# Statistics application

## Building and running
* Please run ```./gradlew bootRun``` to run the application, the server will be available at [http://localhost:8080](http://localhost:8080/swagger-ui/index.html)
* Please run ```./gradlew test``` to run unit and integration tests
* Please run ```./gradlew performance``` to run performance test
## Additional Information

* There is a [Swagger UI ](http://localhost:8080/swagger-ui/index.html) included in the build to help run POST and GET queries.
* Default max number of data points for every symbol is set to 10e8. Should you need to increase it please tweak the ```DEFAULT_MAX_NUMBER_OF_DATA_POINTS``` constant in DataPoints.java as well as memory settings in ```build.gradle```
* Default max number of data points to be added in a batch is set in ```MAX_BATCH_SIZE``` constant in ```AddDataPointsController.java```. Currently, it is set to 10000.
* Parameter k in /stats specifies the index to calculate the stats from, counting from the last data point. It is the power of 10 (from 1 to 8, meaning index 10-100000000). Last index is 1 based, therefore k=1 is equivalent to lastIndex 10 will fetch 10 last data points.
* Example call to ```/addBatch``` - ```curl -X "POST" "http://localhost:8080/add_batch"   -H "Content-Type: application/json" -d "{\"symbol\": \"S1\", \"values\": [10, 1, 3.5, 15, 100.2, 23, 52, 18.7, -0.5, 12]}"```
* Example call to ```/stats``` - ```curl -X "GET" "http://localhost:8080/stats?symbol=S1&k=1"```
* /stats returns a 400 BAD REQUEST if the required parameters are not specified, or they are invalid - wrong symbol name or k outside <0,8> range or if the last index denoted by k is greater than the number of data points.
* /addBatch returns a 400 BAD REQUEST if the list of data points is empty or if the symbol name is wrong or if the number of data points exceeds the max batch size (currently 10000)
* Symbols are defined in ```Symbol``` enum, they are named from ```S1``` to ```S10```

### Algorithms specification

* Variance and mean are calculated using Weltford's algorithm for calculating running mean and variance.
* All statistics are buffered, skipping stats for every ```Calculator.DEFAULT_BUFFER_SKIP``` points (currently set to 10). Therefore, if the number of data points is 10e8, 10e7 stats will be cached for each symbol. The statistics for the given last data point are calculated as follows - the nearest statistics from the buffers are taken, and then all stats are calculated running from that initial values. Therefore, the time for getting stats for the given last data point denoted by k is constant and independent of the total number of data points, as long as statistics have been already buffered, which is done when data points are being added. The algorithm requires the data points to be stored as well.
* Memory complexity is O(n) - it increases linearly with the number of data points.


