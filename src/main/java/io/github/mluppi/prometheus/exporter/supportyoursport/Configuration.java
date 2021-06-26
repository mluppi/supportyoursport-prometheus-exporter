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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    // options
    static final String OPTION_PORT = "port";
    static final String OPTION_REFRESH_RATE = "refresh-rate";
    static final String OPTION_CLUB_IDS = "club-ids";

    // defaults
    static final int DEFAULT_PORT = 9788;
    static final int DEFAULT_REFRESH_RATE_IN_SECONDS = 600; // 10 minutes

    // attributes
    private final int port;
    private final int refreshRate;
    private final List<String> clubIds;

    public Configuration(final String[] args) throws ParseException {
        final CommandLine commandLine = parseAndGetCommandLine(args);
        this.port = extractIntWithDefault(commandLine, OPTION_PORT, DEFAULT_PORT);
        this.refreshRate = extractIntWithDefault(commandLine, OPTION_REFRESH_RATE, DEFAULT_REFRESH_RATE_IN_SECONDS);
        this.clubIds = extractStringList(commandLine, OPTION_CLUB_IDS);
        logger.info("Loaded configuration: port={}, refreshrate={}s, clubIds={}", this.port, this.refreshRate, this.clubIds);
    }

    public int getPort() {
        return this.port;
    }

    public int getRefreshRateInSeconds() {
        return this.refreshRate;
    }

    public List<String> getClubIds() {
        return Collections.unmodifiableList(clubIds);
    }

    private static CommandLine parseAndGetCommandLine(final String[] args) throws ParseException {
        final Options options = new Options();

        options.addOption(Option.builder()
                .required(false)
                .longOpt(OPTION_PORT)
                .hasArg()
                .build());

        options.addOption(Option.builder()
                .required(false)
                .longOpt(OPTION_REFRESH_RATE)
                .hasArg()
                .build());

        options.addOption(Option.builder()
                .required(false)
                .longOpt(OPTION_CLUB_IDS)
                .hasArgs()
                .build());

        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static List<String> extractStringList(final CommandLine commandLine, final String opt) {
        final String[] values = commandLine.getOptionValues(opt);
        if (values == null) {
            return Collections.emptyList();
        } else {
            return List.of(values);
        }
    }

    private static int extractIntWithDefault(final CommandLine commandLine, final String opt, final int defaultValue) {
        if (commandLine.hasOption(opt)) {
            final String value = commandLine.getOptionValue(opt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Could not parse '" + opt + "' parameter", e);
            }
        } else {
            return defaultValue;
        }
    }
}
