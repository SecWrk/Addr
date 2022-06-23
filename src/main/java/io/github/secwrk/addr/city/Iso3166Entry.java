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

import com.google.gson.JsonObject;

/**
 * ISO 3316 Entry
 */
public record Iso3166Entry(String name, String alpha2, String alpha3, String countryCode, String iso_3166_2,
                           String region, String subRegion, String intermediateRegion, String regionCode,
                           String subRegionCode, String intermediateRegionCode) {

    public static Iso3166Entry from(JsonObject jsonObject) {
/*
        // Might be used in future.
        return new Iso3166Entry(jsonObject.get("name").getAsString(),
                jsonObject.get("alpha-2").getAsString(),
                jsonObject.get("alpha-3").getAsString(),
                jsonObject.get("country-code").getAsString(),
                jsonObject.get("iso_3166-2").getAsString(),
                jsonObject.get("region").getAsString(),
                jsonObject.get("sub-region").getAsString(),
                jsonObject.get("intermediate-region").getAsString(),
                jsonObject.get("region-code").getAsString(),
                jsonObject.get("sub-region-code").getAsString(),
                jsonObject.get("intermediate-region-code").getAsString());
*/

        return new Iso3166Entry(jsonObject.get("name").getAsString(),
                jsonObject.get("alpha-2").getAsString(),
                null,
                null,
                null,
                jsonObject.get("region").getAsString(),
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public String toString() {
        return "Iso3166Entry{" +
                "name='" + name + '\'' +
                ", alpha2='" + alpha2 + '\'' +
                ", alpha3='" + alpha3 + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", iso_3166_2='" + iso_3166_2 + '\'' +
                ", region='" + region + '\'' +
                ", subRegion='" + subRegion + '\'' +
                ", intermediateRegion='" + intermediateRegion + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", subRegionCode='" + subRegionCode + '\'' +
                ", intermediateRegionCode='" + intermediateRegionCode + '\'' +
                '}';
    }
}
