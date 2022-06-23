/*
 * Copyright 2022, SecWrk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.secwrk.addr.city;

import java.util.Objects;

/**
 * Geonames Country Entry
 */
public record GeoNameCountryEntry(String countryCode, String countryCode3, String countryName, String continentCode) {

    public static GeoNameCountryEntry from(String countryCode, String countryCode3, String countryName, String continentCode) {
        return new GeoNameCountryEntry(Objects.requireNonNull(countryCode, "CountryCode"),
                Objects.requireNonNull(countryCode3, "CountryCode3"),
                Objects.requireNonNull(countryName, "CountryName"),
                Objects.requireNonNull(continentCode, "ContinentCode"));
    }

    @Override
    public String toString() {
        return "GeoNameCountryEntry{" +
                "countryCode='" + countryCode + '\'' +
                ", countryCode3='" + countryCode3 + '\'' +
                ", countryName='" + countryName + '\'' +
                ", continentCode='" + continentCode + '\'' +
                '}';
    }
}
