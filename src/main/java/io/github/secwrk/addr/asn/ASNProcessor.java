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
package io.github.secwrk.addr.asn;

import io.github.secwrk.addr.Downloader;

import java.io.File;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

/**
 * {@link ASNProcessor} fetches DB-IP Lite ASN file and converts, decompresses it,
 * and stores it as mirror.
 */
public final class ASNProcessor {

    private static final String YEAR;
    private static final String MONTH;

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
    }

    public static void main(String[] args) throws Exception {
        Path cityPath = Path.of("generated" + File.separator + "ASN.csv");
        Downloader.downloadCompressedFile("https://download.db-ip.com/free/dbip-asn-lite-" + YEAR + "-" + MONTH + ".csv.gz", cityPath);
    }
}
