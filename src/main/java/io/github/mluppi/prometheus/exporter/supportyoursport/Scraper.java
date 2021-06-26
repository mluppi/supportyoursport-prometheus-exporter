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

import java.util.List;

public interface Scraper {

    List<Club> scrape(final GroupEnum group);

}
