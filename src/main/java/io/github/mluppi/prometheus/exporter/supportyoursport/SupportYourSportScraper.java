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

import io.github.mluppi.prometheus.exporter.supportyoursport.model.Club;
import io.github.mluppi.prometheus.exporter.supportyoursport.model.GroupEnum;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SupportYourSportScraper implements Scraper {

    private static final Logger logger = LoggerFactory.getLogger(SupportYourSportScraper.class);

    private static final String API_URL = decode("aHR0cHM6Ly9zdXBwb3J0eW91cnNwb3J0Lm1pZ3Jvcy5jaC9hcGkvdjEvZnJvbnRlbmQvbGVhZGVyYm9hcmQvP2dyb3VwPSVzJmxpbWl0PTk5OTk5Jm9mZnNldD0w"); // prevent indexing

    private final HttpClient httpClient;

    public SupportYourSportScraper(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<Club> scrape(final GroupEnum group) {
        final long start = System.currentTimeMillis();
        final List<Club> result = new ArrayList<>();
        try {
            final HttpGet request = new HttpGet(String.format(API_URL, group.getSize()));
            final HttpResponse response = this.httpClient.execute(request);
            final JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            json.getJSONArray("results").forEach(entry -> {
                JSONObject club = (JSONObject) entry;
                final JSONObject organisation = club.getJSONObject("organisation");
                result.add(new Club(organisation.getString("publicId"),
                        organisation.getString("name"),
                        club.getInt("rank"),
                        organisation.getInt("totalVoucherCount")));
            });
        } catch (Exception e) {
            logger.error("Scrape failed: group={} time={}ms", group, System.currentTimeMillis() - start, e);
        }
        logger.info("Scrape successful: group={} clubs={} time={}ms", group, result.size(), System.currentTimeMillis() - start);
        return result;
    }

    private static String decode(final String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }
}
