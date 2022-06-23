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
package io.github.secwrk.addr;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class Downloader {

    private static final int BUFFER_SIZE = 16_384;
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public static byte[] downloadAsBytes(String url) throws IOException, InterruptedException {
        HttpResponse<InputStream> httpResponse = HTTP_CLIENT.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build(), HttpResponse.BodyHandlers.ofInputStream());

        return decompress(httpResponse.body());
    }

    public static void downloadCompressedFile(String url, Path path) throws IOException, InterruptedException {
        HttpResponse<InputStream> httpResponse = HTTP_CLIENT.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build(), HttpResponse.BodyHandlers.ofInputStream());

        decompress(httpResponse.body(), path);
    }

    public static Stream<String> downloadAsStream(String url) throws IOException, InterruptedException {
        HttpResponse<Stream<String>> httpResponse = HTTP_CLIENT.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build(), HttpResponse.BodyHandlers.ofLines());

        return httpResponse.body();
    }

    public static Path downloadAsString(String url, Path path) throws IOException, InterruptedException {
        return HTTP_CLIENT.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build(), HttpResponse.BodyHandlers.ofFile(path))
                .body();
    }

    private static byte[] decompress(InputStream in) {
        try (GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(in);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int count;
            byte[] data = new byte[BUFFER_SIZE];

            while ((count = gzipIn.read(data, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(data, 0, count);
            }

            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Caught error while processing archive", ex);
        }
    }

    private static void decompress(InputStream is, Path path) {
        try (GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(is);
             FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            int count;
            byte[] data = new byte[BUFFER_SIZE];

            while ((count = gzipIn.read(data, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(data, 0, count);
            }

            outputStream.flush();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Caught error while processing archive", ex);
        }
    }

    private Downloader() {
        // Prevent outside initialization
    }
}
