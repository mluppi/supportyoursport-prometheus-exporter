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
import io.prometheus.client.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SupportYourSportMetrics {

    private static final Logger logger = LoggerFactory.getLogger(SupportYourSportMetrics.class);

    private static final double TOTAL_BALANCE_PER_GROUP = 1_000_000;
    private static final double SEED_BALANCE_PER_ACTIVE_CLUB = 100;
    private static final int VOUCHER_COUNT_TO_ACTIVATE_CLUB = 20;

    private static final String CLUB_RANK_METRIC_NAME = "supportyoursport_club_rank";
    private static final String[] CLUB_RANK_LABEL_NAMES = {"id", "name", "group"};
    private static final Gauge CLUB_RANK_GAUGE = Gauge.build()
            .name(CLUB_RANK_METRIC_NAME)
            .labelNames(CLUB_RANK_LABEL_NAMES)
            .help("Rank of a club").register();

    private static final String CLUB_VOUCHER_COUNT_METRIC_NAME = "supportyoursport_club_voucher_count";
    private static final String[] CLUB_VOUCHER_COUNT_LABEL_NAMES = {"id", "name", "group"};
    private static final Gauge CLUB_VOUCHER_COUNT_GAUGE = Gauge.build()
            .name(CLUB_VOUCHER_COUNT_METRIC_NAME)
            .labelNames(CLUB_VOUCHER_COUNT_LABEL_NAMES)
            .help("Voucher count of a club").register();

    private static final String CLUB_BALANCE_METRIC_NAME = "supportyoursport_club_balance";
    private static final String[] CLUB_BALANCE_LABEL_NAMES = {"id", "name", "group"};
    private static final Gauge CLUB_BALANCE_GAUGE = Gauge.build()
            .name(CLUB_BALANCE_METRIC_NAME)
            .labelNames(CLUB_BALANCE_LABEL_NAMES)
            .help("Balance of a club").register();

    private static final String ACTIVE_VOUCHER_COUNT_METRIC_NAME = "supportyoursport_active_voucher_count";
    private static final String[] ACTIVE_VOUCHER_COUNT_LABEL_NAMES = {"group"};
    private static final Gauge ACTIVE_VOUCHER_COUNT_GAUGE = Gauge.build()
            .name(ACTIVE_VOUCHER_COUNT_METRIC_NAME)
            .labelNames(ACTIVE_VOUCHER_COUNT_LABEL_NAMES)
            .help("Active voucher count of a group").register();

    private static final String ACTIVE_CLUBS_COUNT_METRIC_NAME = "supportyoursport_active_club_count";
    private static final String[] ACTIVE_CLUBS_COUNT_LABEL_NAMES = {"group"};
    private static final Gauge ACTIVE_CLUBS_COUNT_GAUGE = Gauge.build()
            .name(ACTIVE_CLUBS_COUNT_METRIC_NAME)
            .labelNames(ACTIVE_CLUBS_COUNT_LABEL_NAMES)
            .help("Amount of active clubs of a group").register();

    private static final String TOTAL_SHARED_BALANCE_METRIC_NAME = "supportyoursport_total_shared_balance";
    private static final String[] TOTAL_SHARED_BALANCE_LABEL_NAMES = {"group"};
    private static final Gauge TOTAL_SHARED_BALANCE_GAUGE = Gauge.build()
            .name(TOTAL_SHARED_BALANCE_METRIC_NAME)
            .labelNames(TOTAL_SHARED_BALANCE_LABEL_NAMES)
            .help("Total shared balance of a group").register();

    private static final String BON_VALUE_METRIC_NAME = "supportyoursport_bon_value";
    private static final String[] BON_VALUE_LABEL_NAMES = {"group"};
    private static final Gauge BON_VALUE_GAUGE = Gauge.build()
            .name(BON_VALUE_METRIC_NAME)
            .labelNames(BON_VALUE_LABEL_NAMES)
            .help("Value of a bon within a group").register();

    public static void refresh(final Scraper scraper, final List<String> clubIdList) {
        logger.info("Refresh started");
        final long start = System.currentTimeMillis();
        for (GroupEnum group : GroupEnum.values()) {
            final List<Club> allClubs = scraper.scrape(group);
            int activeClubs = 0;
            int activeVouchers = 0;
            for (final Club club : allClubs) {
                if (isClubActive(club)) {
                    activeClubs++;
                    activeVouchers += club.getVoucherCount();
                }
            }

            final double sharedBalance = TOTAL_BALANCE_PER_GROUP - (SEED_BALANCE_PER_ACTIVE_CLUB * activeClubs);
            final double bonValue = (activeVouchers != 0) ? sharedBalance / activeVouchers : 0;
            for (final Club club : allClubs) {
                if (clubIdList.isEmpty() || clubIdList.contains(club.getId())) {
                    if (isClubActive(club)) {
                        final double clubShare = club.getVoucherCount() * bonValue;
                        club.setBalance(SEED_BALANCE_PER_ACTIVE_CLUB + clubShare);
                    }
                    CLUB_RANK_GAUGE.labels(club.getId(), club.getName(), group.name()).set(club.getRank());
                    CLUB_VOUCHER_COUNT_GAUGE.labels(club.getId(), club.getName(), group.name()).set(club.getVoucherCount());
                    CLUB_BALANCE_GAUGE.labels(club.getId(), club.getName(), group.name()).set(club.getBalance());
                }
            }

            ACTIVE_VOUCHER_COUNT_GAUGE.labels(group.name()).set(activeVouchers);
            ACTIVE_CLUBS_COUNT_GAUGE.labels(group.name()).set(activeClubs);
            TOTAL_SHARED_BALANCE_GAUGE.labels(group.name()).set(sharedBalance);
            BON_VALUE_GAUGE.labels(group.name()).set(bonValue);

            if (logger.isDebugEnabled()) {
                logger.debug("Sum of all balances is CHF {}", allClubs.stream().mapToDouble(Club::getBalance).sum());
            }
        }
        logger.info("Refresh completed in {}ms", System.currentTimeMillis() - start);
    }

    private static boolean isClubActive(final Club club) {
        return (club.getVoucherCount() >= VOUCHER_COUNT_TO_ACTIVATE_CLUB);
    }

    private SupportYourSportMetrics() {
    }
}
