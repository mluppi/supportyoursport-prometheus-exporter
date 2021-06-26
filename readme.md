# Support Your Sport Prometheus Exporter

This exporter exposes Prometheus metrics of the *Support Your Sport* promotion.

## Configuration
The following configuration can be supplied on the command line:

| Parameter        | Mandatory | Explanation |
|------------------|-----------|-------------|
| `--club-ids`     | no        | Limit processing to one or multiple clubs according to the API (`publicId` field) |
| `--refresh-rate` | no        | Internal refresh rate in seconds (default: 600) |
| `--port`         | no        | Port on which the metrics are exposed (default: 9788) |


## Example usage
1. Run the JAR with appropriate configuration parameters:
   ```
   java -jar supportyoursport-prometheus-exporter.jar --club-ids XXXX YYYY
   ```

2. Configure a scrape job in Prometheus (`prometheus.yml`):
   ```
   scrape_configs:
     
     - job_name: 'supportyoursport'
   
       scrape_interval: 600s
       scrape_timeout: 30s
   
       static_configs:
         - targets: ['localhost:9788']
   ```

3. Display results in Prometheus or use them in another application (e.g. Grafana).

## Build

#### JAR
```
mvn clean package
```

#### Docker-Images
```
mvn clean package -Pbuild-docker-image -DskipTests=true -Ddocker-repository=abc
```

Set *docker-repository* to the desired Docker repository (e.g. `docker-hub-username` or `xyz.azurecr.io`)

## License
This project is licensed under the Apache 2.0 license, see [LICENSE](LICENSE).
