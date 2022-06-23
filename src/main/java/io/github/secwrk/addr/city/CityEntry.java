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
 * DB-IP Lite City Entries
 */
public final class CityEntry {

    private final String ipStart;
    private final String ipEnd;
    private final String continentCode;
    private final String countryCode;
    private final String stateProvince;
    private final String city;
    private final float latitude;
    private final float longitude;
    private final String countryName;
    private final String continentName;

    private CityEntry(String ipStart, String ipEnd, String continentCode, String countryCode,
                      String stateProvince, String city, float latitude, float longitude,
                      String countryName, String continentName) {
        this.ipStart = Objects.requireNonNull(ipStart, "IpStart");
        this.ipEnd = Objects.requireNonNull(ipEnd, "IpEnd");
        this.continentCode = Objects.requireNonNull(continentCode, "ContinentCode");
        this.countryCode = Objects.requireNonNull(countryCode, "CountryCode");
        this.stateProvince = Objects.requireNonNull(stateProvince, "StateProvince");
        this.city = Objects.requireNonNull(city, "City");
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryName = Objects.requireNonNull(countryName, "CountryName");
        this.continentName = Objects.requireNonNull(continentName, "CountryName");
    }

    public static CityEntry from(String ipStart, String ipEnd, String continentCode, String countryCode,
                                 String stateProvince, String city, float latitude, float longitude,
                                 String countryName, String continentName) {
        return new CityEntry(ipStart, ipEnd, continentCode, countryCode, stateProvince, city, latitude, longitude, countryName, continentName);
    }

    public String ipStart() {
        return ipStart;
    }

    public String ipEnd() {
        return ipEnd;
    }

    public String continentCode() {
        return continentCode;
    }

    public String countryCode() {
        return countryCode;
    }

    public String stateProvince() {
        return stateProvince;
    }

    public String city() {
        return city;
    }

    public float latitude() {
        return latitude;
    }

    public float longitude() {
        return longitude;
    }

    public String countryName() {
        return countryName;
    }

    public String continentName() {
        return continentName;
    }

    @Override
    public String toString() {
        return "CityEntry{" +
                "ipStart='" + ipStart + '\'' +
                ", ipEnd='" + ipEnd + '\'' +
                ", continentCode='" + continentCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", stateProvince='" + stateProvince + '\'' +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", countryName='" + countryName + '\'' +
                ", continentName='" + continentName + '\'' +
                '}';
    }
}
