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

import java.io.FileWriter;
import java.io.IOException;

public final class SplitFileWriter extends FileWriter {

    private final int maxWrites;
    private final String fileName;
    private int filesCount = 0;
    private int currentWriteIndex = 0;

    private FileWriter writer;

    public SplitFileWriter(String fileName, int maxWrites) throws IOException {
        super(fileName);
        this.fileName = fileName;
        this.maxWrites = maxWrites;
        this.writer = newWriter();
    }

    @Override
    public void write(String str) throws IOException {
        writer.write(str);
        if (currentWriteIndex == maxWrites) {
            currentWriteIndex = 0;
            writer.close();
            writer = newWriter();
        } else {
            currentWriteIndex++;
        }
    }

    private FileWriter newWriter() throws IOException {
        return new FileWriter(fileName + "-" + filesCount++);
    }

    public int filesCount() {
        return filesCount;
    }

    @Override
    public void close() throws IOException {
        writer.close();
        super.close();
    }
}
