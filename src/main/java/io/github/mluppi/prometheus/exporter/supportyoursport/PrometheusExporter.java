/*
Copyright 2020-2021 M. Luppi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package io.github.mluppi.prometheus.exporter.supportyoursport;

import io.prometheus.client.exporter.HTTPServer;
import org.apache.commons.cli.ParseException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PrometheusExporter {

    private static final Logger logger = LoggerFactory.getLogger(PrometheusExporter.class);

    public static void main(String[] args) {
        try {
            final Configuration configuration = new Configuration(args);

            final HTTPServer httpServer = new HTTPServer(configuration.getPort());
            Runtime.getRuntime().addShutdownHook(new Thread(httpServer::stop));

            final CloseableHttpClient httpClient = HttpClientFactory.getCustomHttpClient(2 * configuration.getRefreshRateInSeconds());
            final Scraper scraper = new SupportYourSportScraper(httpClient);

            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(
                    () -> SupportYourSportMetrics.refresh(scraper, configuration.getClubIds()),
                    0, configuration.getRefreshRateInSeconds(), TimeUnit.SECONDS);

            logger.info("Scheduled refresh job according to configuration");

        } catch (ParseException e) {
            logger.error("Could not parse configuration", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("Could not start HTTP server", e);
            System.exit(1);
        }
    }
}
