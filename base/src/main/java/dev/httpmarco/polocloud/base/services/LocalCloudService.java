/*
 * Copyright 2024 Mirco Lindenau | HttpMarco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.httpmarco.polocloud.base.services;

import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.osgan.networking.ChannelTransmit;
import dev.httpmarco.polocloud.api.groups.CloudGroup;
import dev.httpmarco.polocloud.api.services.ServiceState;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
public final class LocalCloudService extends CloudServiceImpl {

    private final Path runningFolder;

    @Setter
    private Process process;
    @Setter
    private ChannelTransmit channelTransmit;
    private boolean subscribed;

    public LocalCloudService(CloudGroup group, int orderedId, UUID id, int port, ServiceState state) {
        super(group, orderedId, id, port, group.platform().proxy() ? "0.0.0.0" : CloudAPI.instance().nodeService().localNode().hostname(), state);

        this.runningFolder = Path.of("running/" + name() + "-" + id());
        this.subscribed = false;
    }

    @SneakyThrows
    public void execute(String execute) {
        var writer = new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream()));

        writer.write(execute);
        writer.newLine();
        writer.flush();
    }

    public void subscribeLog() {
        if (this.subscribed) {
            this.subscribed = false;
            return;
        }
        this.subscribed = true;

        log().forEach(System.out::println);
        new Thread(() -> {
            var in = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    if (!this.subscribed) {
                        break;
                    }
                    CloudAPI.instance().logger().info(line);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }).start();
    }

    @Override
    @SneakyThrows
    public List<String> log() {
        var logs = new ArrayList<String>();
        var inputStream = this.process.getInputStream();
        var bytes = new byte[2048];
        int length;
        while (inputStream.available() > 0 && (length = inputStream.read(bytes, 0, bytes.length)) != -1) {
            logs.addAll(Arrays.stream(new String(bytes, 0, length, StandardCharsets.UTF_8).split("\n")).toList());
        }
        return logs;
    }

}
