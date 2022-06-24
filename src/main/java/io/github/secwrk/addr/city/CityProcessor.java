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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import io.github.secwrk.addr.Downloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.opencsv.ICSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;
import static com.opencsv.ICSVWriter.DEFAULT_QUOTE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;

/**
 * {@link CityProcessor} fetches DB-IP Lite City file, ISO-3316 and Geonames Country
 * and processes them to generate City file which contains Continent name and Country name.
 */
public final class CityProcessor {

    private static final String YEAR;
    private static final String MONTH;

    private static final String ISO3166_URL = "https://raw.githubusercontent.com/lukes/ISO-3166-Countries-with-Regional-Codes/master/all/all.json";
    private static final String GEONAME_URL = "http://download.geonames.org/export/dump/countryInfo.txt";

    private static final List<Iso3166Entry> ISO_LIST = new ArrayList<>();
    private static final List<GeoNameCountryEntry> GEONAME_LIST = new ArrayList<>();
    private static final Queue<CityEntry> CITY_QUEUE = new ConcurrentLinkedQueue<>();

    private static final Map<String, String> CONTINENT_MAPPING = new HashMap<>();

    private static int fileCount = 0;
    private static int index = 0;

    static {
        ZoneId z = ZoneId.of("UTC");
        ZonedDateTime zdt = ZonedDateTime.now(z);
        String month = String.valueOf(zdt.get(ChronoField.MONTH_OF_YEAR));
        if (month.length() == 1) {
            MONTH = "0" + month;
        } else {
            MONTH = month;
        }
        YEAR = String.valueOf(zdt.get(ChronoField.YEAR));

        CONTINENT_MAPPING.put("AS", "Asia");
        CONTINENT_MAPPING.put("AF", "Africa");
        CONTINENT_MAPPING.put("EU", "Europe");
        CONTINENT_MAPPING.put("NA", "North America");
        CONTINENT_MAPPING.put("SA", "South America");
        CONTINENT_MAPPING.put("OC", "Oceania");
        CONTINENT_MAPPING.put("AN", "Antarctica");
    }

    public static void main(String[] args) throws Exception {
        // Download GeoName Database
        Path geoNamePath = Downloader.downloadAsString(GEONAME_URL, Path.of("GeoName.txt"));
        readGeoNameEntry(geoNamePath);

        // Download ISO Database
        Path isoPath = Downloader.downloadAsString(ISO3166_URL, Path.of("Iso3316.json"));
        try (FileReader fileReader = new FileReader(isoPath.toFile())) {
            for (JsonElement element : JsonParser.parseReader(fileReader).getAsJsonArray()) {
                ISO_LIST.add(Iso3166Entry.from((JsonObject) element));
            }
        }

        // Download City File
        Path cityPath = Path.of("City.csv");
        Downloader.downloadCompressedFile("https://download.db-ip.com/free/dbip-city-lite-" + YEAR + "-" + MONTH + ".csv.gz", cityPath);
        processCityEntries(cityPath);

        // Write CSV files
        writeFiles();

        // Write File names of all CSV file in AllCity.txt
        try (FileWriter writer = new FileWriter("generated" + File.separator + "AllCity.txt")) {
            for (int i = 0; i <= fileCount; i++) {
                writer.write("City-" + i + ".csv");
                writer.write("\r\n");
            }
        }
    }

    private static void writeFiles() throws IOException {
        FileWriter writer = newWriter(fileCount);
        CityEntry cityEntry = CITY_QUEUE.poll();

        StringBuilder line = new StringBuilder();
        while (cityEntry != null) {

            line.append(cityEntry.ipStart()).append(',')
                    .append(cityEntry.ipStart()).append(',')
                    .append(cityEntry.ipEnd()).append(',')
                    .append(cityEntry.continentCode()).append(',')
                    .append(cityEntry.countryCode()).append(',');

            // Continent Name Check
            if (doesStringContainsSpecialCharacters(cityEntry.continentName())) {
                line.append("\"").append(cityEntry.continentName()).append("\"").append(',');
            } else {
                line.append(cityEntry.continentName()).append("\"").append(',');
            }

            // Country Name Check
            if (doesStringContainsSpecialCharacters(cityEntry.countryName())) {
                line.append("\"").append(cityEntry.countryName()).append("\"").append(',');
            } else {
                line.append(cityEntry.countryName()).append(',');
            }

            // State Check
            if (doesStringContainsSpecialCharacters(cityEntry.stateProvince())) {
                line.append("\"").append(cityEntry.stateProvince()).append("\"").append(',');
            } else {
                line.append(cityEntry.stateProvince()).append(',');
            }

            // City Name Check
            if (doesStringContainsSpecialCharacters(cityEntry.city())) {
                line.append("\"").append(cityEntry.city()).append("\"").append(',');
            } else {
                line.append(cityEntry.city()).append(',');
            }

            // Add Latitude and Longitude
            line.append(cityEntry.latitude()).append(',')
                    .append(cityEntry.longitude()).append("\r\n");

            writer.write(line.toString());
            line.setLength(0); // Reset the StringBuilder

            index++;
            // When we hit 500000 lines of file, close it and reset index and increment file count.
            if (index == 500_000) {
                writer.flush();
                writer.close();
                index = 0;
                fileCount++;
                writer = newWriter(fileCount);
            }

            // Pull a City Entry for next iteration
            cityEntry = CITY_QUEUE.poll();
        }
        writer.close();
    }

    private static FileWriter newWriter(int fileCount) throws IOException {
        return new FileWriter("generated" + File.separator + "City-" + fileCount + ".csv");
    }

    private static boolean doesStringContainsSpecialCharacters(String line) {
        return line.indexOf(DEFAULT_QUOTE_CHARACTER) != -1
                || line.indexOf(DEFAULT_ESCAPE_CHARACTER) != -1
                || line.indexOf(DEFAULT_SEPARATOR) != -1
                || line.contains(DEFAULT_LINE_END)
                || line.contains("\r");
    }

    private static void processCityEntries(Path path) throws Exception {
        try (CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(new FileReader(path.toFile()))).build()) {
            for (String[] line : csvReader.readAll()) {
                String ipStart = line[0];
                String ipEnd = line[1];
                String continentCode = line[2];
                String countryCode = line[3];
                String stateProvince = line[4];
                String city = line[5];
                float latitude = Float.parseFloat(line[6]);
                float longitude = Float.parseFloat(line[7]);

                if (countryCode.equalsIgnoreCase("ZZ")) {
                    System.out.println("Skipping ZZ");
                    continue;
                }

                Iso3166Entry iso3166Entry = getIso(countryCode);
                String countryName;
                String continentName;

                if (iso3166Entry == null) {
                    System.out.println("Skipping Iso3166Entry for CountryCode: " + countryCode);
                    System.out.println("Trying GeoNameEntry for CountryCode: " + countryCode);

                    GeoNameCountryEntry geoNameEntry = getGeoName(countryCode);
                    if (geoNameEntry == null) {
                        System.out.println("Skipping GeoNameCountryEntry for CountryCode: " + countryCode);
                        continue;
                    } else {
                        System.out.println("Successfully Retrieved CountryCode: " + countryCode);
                        countryName = geoNameEntry.countryName();
                        continentName = CONTINENT_MAPPING.get(geoNameEntry.continentCode().toUpperCase());
                    }
                } else {
                    countryName = iso3166Entry.name();
                    continentName = iso3166Entry.region();
                }

                CITY_QUEUE.add(CityEntry.from(ipStart, ipEnd, continentCode, countryCode, stateProvince, city, latitude, longitude,
                        countryName, continentName));
            }
        }
    }

    private static Iso3166Entry getIso(String countryCode) {
        for (Iso3166Entry iso3166Entry : ISO_LIST) {
            if (iso3166Entry.alpha2().equalsIgnoreCase(countryCode)) {
                return iso3166Entry;
            }
        }
        return null;
    }

    private static GeoNameCountryEntry getGeoName(String countryCode) {
        for (GeoNameCountryEntry geonameEntry : GEONAME_LIST) {
            if (geonameEntry.countryCode().equalsIgnoreCase(countryCode)) {
                return geonameEntry;
            }
        }
        return null;
    }

    private static void readGeoNameEntry(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) == '#') {
                    continue; // Skip Comment
                }
                String[] items = line.split("\t");
                GEONAME_LIST.add(GeoNameCountryEntry.from(items[0], items[1], items[4], items[8]));
            }
        }
    }
}
