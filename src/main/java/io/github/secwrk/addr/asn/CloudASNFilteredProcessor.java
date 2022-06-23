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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import io.github.secwrk.addr.Downloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * {@link CloudASNFilteredProcessor} fetches ASN List from iptoasn.com and Cloud Providers IP address list
 * from Firehol and maps IP address to ASN and stores it into "ASN.json" file in Json format.
 * This can be used to filter cloud provider IP addresses.
 */
public final class CloudASNFilteredProcessor {

    private static final Gson GSON = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    public static void main(String[] args) throws Exception {
        echo("Downloading ASN File");

        // Download ASN File
        byte[] bytes = Downloader.downloadAsBytes("https://iptoasn.com/data/ip2asn-combined.tsv.gz");
        List<ASNEntry> entries = readAsnEntries(new String(bytes));

        echo("Downloading Datacenter IP File");
        // Download Datacenter IP File
        List<String> response = Downloader.downloadAsStream("https://raw.githubusercontent.com/firehol/blocklist-ipsets/master/datacenters.netset")
                .filter(s -> s.charAt(0) != '#') // Filter lines which are not commented.
                .toList();

        echo("Processing ASN");
        // Match Datacenter IP against ASN List and Collect
        // matching ASN to Set.
        Set<Long> ASN_SET = response.parallelStream()
                .map(ip -> getAsn(entries, new IPAddressString(ip).getAddress()))
                .filter(asn -> asn > 0)          // Filter ASN which are greater than 0
                .collect(Collectors.toCollection(TreeSet::new));

        echo("Transforming result to Json");
        // Create Json and add ASN List
        JsonArray asnArray = new JsonArray();
        ASN_SET.forEach(asnArray::add);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("asn", asnArray);

        echo("Writing ASN.json File");
        // Write Json to File
        try (FileWriter writer = new FileWriter("generated" + File.separator + "CloudASN.json", false)) {
            writer.write(GSON.toJson(jsonObject));
        }

        echo("Finished...");
    }

    /**
     * Lookup for {@link ASNEntry} for an IP Address
     *
     * @param list    {@link List} of {@link ASNEntry}
     * @param address IP address to search for
     * @return Returns ASN for IP Address. Returns -1 if not found.
     */
    private static long getAsn(List<ASNEntry> list, IPAddress address) {
        for (ASNEntry ASNEntry : list) {
            if (ASNEntry.isInsideRange(address)) {
                return ASNEntry.asn();
            }
        }
        return -1;
    }

    private static List<ASNEntry> readAsnEntries(String data) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(data))) {
            List<ASNEntry> list = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split("\t");
                list.add(ASNEntry.create(items[0], items[1], Long.parseLong(items[2]), items[3], items[4]));
            }
            return list;
        }
    }

    /**
     * Shortcut call to System.out.println(Object)
     */
    private static void echo(Object o) {
        System.out.println(o);
    }
}
