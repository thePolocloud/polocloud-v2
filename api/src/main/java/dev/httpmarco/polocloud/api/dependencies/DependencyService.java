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

package dev.httpmarco.polocloud.api.dependencies;

import dev.httpmarco.polocloud.runner.RunnerBootstrap;
import lombok.SneakyThrows;

import java.nio.file.Files;

public final class DependencyService {

    @SneakyThrows
    public DependencyService() {
        var dependenciesPath = RunnerBootstrap.RUNNER.dependencyFolder();

        if (!Files.exists(dependenciesPath)) {
            Files.createDirectory(dependenciesPath);
        }

        // todo generate default dependency definitions
        // load default dependencies of base and instance
        //todo better subversion
        Dependency.load("dev.httpmarco", "osgan-utils", "1.2.12-SNAPSHOT", "1.2.12-20240606.202406-1", Dependency.MAVEN_CENTRAL_SNAPSHOT_REPO);;
        Dependency.load("dev.httpmarco", "osgan-reflections", "1.2.12-SNAPSHOT", "1.2.12-20240606.202406-1", Dependency.MAVEN_CENTRAL_SNAPSHOT_REPO);
        Dependency.load("dev.httpmarco", "osgan-files", "1.2.12-SNAPSHOT", "1.2.12-20240606.202406-1", Dependency.MAVEN_CENTRAL_SNAPSHOT_REPO);
        Dependency.load("dev.httpmarco", "osgan-netty", "1.2.12-SNAPSHOT", "1.2.12-20240606.202406-1", Dependency.MAVEN_CENTRAL_SNAPSHOT_REPO);
        Dependency.load("io.netty", "netty5-common", "5.0.0.Alpha5");
        Dependency.load("io.netty", "netty5-transport", "5.0.0.Alpha5");
        Dependency.load("io.netty", "netty5-codec", "5.0.0.Alpha5");
        Dependency.load("io.netty", "netty5-resolver", "5.0.0.Alpha5");
        Dependency.load("io.netty", "netty5-buffer", "5.0.0.Alpha5");
        Dependency.load("io.netty", "netty5-transport-classes-epoll", "5.0.0.Alpha5");
        Dependency.load("com.google.code.gson", "gson", "2.10.1");
        Dependency.load("net.kyori", "adventure-text-minimessage", "4.17.0");
        Dependency.load("net.kyori", "adventure-platform-bungeecord", "4.3.2");
    }
}
