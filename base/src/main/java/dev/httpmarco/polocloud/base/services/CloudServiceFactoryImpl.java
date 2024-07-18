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

import dev.httpmarco.osgan.files.OsganFile;
import dev.httpmarco.osgan.files.OsganFileCreateOption;
import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.events.service.CloudServiceShutdownEvent;
import dev.httpmarco.polocloud.api.events.service.CloudServiceStartEvent;
import dev.httpmarco.polocloud.api.groups.CloudGroup;
import dev.httpmarco.polocloud.api.groups.GroupProperties;
import dev.httpmarco.polocloud.api.services.CloudService;
import dev.httpmarco.polocloud.api.services.CloudServiceFactory;
import dev.httpmarco.polocloud.api.services.ServiceState;
import dev.httpmarco.polocloud.base.CloudBase;
import dev.httpmarco.polocloud.base.groups.CloudGroupPlatformService;
import dev.httpmarco.polocloud.base.groups.platforms.PaperPlatform;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class CloudServiceFactoryImpl implements CloudServiceFactory {

    // todo not used
    private static final Path RUNNING_FOLDER = OsganFile.define("running", OsganFileCreateOption.CREATION).path();

    @SneakyThrows
    public CloudServiceFactoryImpl() {
        if (Files.exists(RUNNING_FOLDER)) {
            FileUtils.forceDelete(RUNNING_FOLDER.toFile());
            Files.createDirectory(RUNNING_FOLDER);
        }
    }

    @Override
    @SneakyThrows
    public void start(CloudGroup cloudGroup) {
        var service = new LocalCloudService(cloudGroup, this.nextServiceId(cloudGroup), UUID.randomUUID(), ServicePortDetector.detectServicePort(cloudGroup), ServiceState.PREPARED);
        ((CloudServiceProviderImpl) CloudAPI.instance().serviceProvider()).registerService(service);

        CloudAPI.instance().logger().info("The Service &2'&4" + service.name() + "&2' &1is starting now on node &2'&4" + CloudAPI.instance().nodeService().localNode().name() + "&2'");

        CloudBase.instance().templatesService().cloneTemplate(service);

        // download and/or copy platform file to service
        CloudGroupPlatformService platformService = CloudBase.instance().groupProvider().platformService();
        platformService.preparePlatform(service);

        var args = new LinkedList<>();

        args.add("java");

        // default commands
        args.add("-Djline.terminal=jline.UnsupportedTerminal");

        args.addAll(List.of("-XX:+UseG1GC", "-XX:+ParallelRefProcEnabled", "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions", "-XX:+DisableExplicitGC", "-XX:+AlwaysPreTouch", "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40", "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20", "-XX:G1HeapWastePercent=5", "-XX:G1MixedGCCountTarget=4", "-XX:InitiatingHeapOccupancyPercent=15", "-XX:G1MixedGCLiveThresholdPercent=90", "-XX:G1RSetUpdatingPauseTimePercent=5", "-XX:SurvivorRatio=32", "-XX:+PerfDisableSharedMem", "-XX:MaxTenuringThreshold=1", "-Dusing.aikars.flags=https://mcflags.emc.gs", "-Daikars.new.flags=true", "-XX:-UseAdaptiveSizePolicy", "-XX:CompileThreshold=100", "-Dio.netty.recycler.maxCapacity=0", "-Dio.netty.recycler.maxCapacity.default=0", "-Djline.terminal=jline.UnsupportedTerminal", "-Dfile.encoding=UTF-8", "-Dclient.encoding.override=UTF-8", "-DIReallyKnowWhatIAmDoingISwear=true"));

        args.add("-Xms" + service.memory() + "M");
        args.add("-Xmx" + service.memory() + "M");

        args.addAll(Arrays.stream(platformService.find(cloudGroup.platform().version()).platformsEnvironment()).toList());
        args.add("-javaagent:../../polocloud.jar");
        args.add("-jar");
        args.add("../../polocloud.jar");
        args.add("--instance");
        args.addAll(Arrays.stream(platformService.find(cloudGroup.platform().version()).platformsArguments()).toList());

        var processBuilder = new ProcessBuilder().directory(service.runningFolder().toFile()).command(args.toArray(String[]::new));

        if (cloudGroup.properties().has(GroupProperties.DEBUG_MODE)) {
            processBuilder.redirectError(new File(service.name() + service.id() + ".error-log"));
        }

        processBuilder.environment().put("hostname", service.hostname());
        processBuilder.environment().put("port", String.valueOf(service.port()));
        processBuilder.environment().put("appendSearchClasspath", String.valueOf(!(platformService.find(service.group().platform().version()) instanceof PaperPlatform)));
        processBuilder.environment().put("bootstrapFile", service.group().platform().version());
        processBuilder.environment().put("serviceId", service.id().toString());
        processBuilder.environment().put("proxySecret", CloudGroupPlatformService.PROXY_SECRET);

        var pluginDirectory = service.runningFolder().resolve("plugins");

        OsganFile.create(pluginDirectory.toString());

        // copy polocloud plugin
        //todo a better way for minestom
        java.nio.file.Files.copy(Path.of("local/polocloud-plugin.jar"), pluginDirectory.resolve("polocloud-plugin.jar"), StandardCopyOption.REPLACE_EXISTING);

        service.state(ServiceState.STARTING);
        CloudAPI.instance().globalEventNode().call(new CloudServiceStartEvent(service));

        service.process(processBuilder.start());

        new Thread(() -> {
            try {
                service.process().waitFor();

                if (CloudBase.instance().running()) {
                    this.closeProcess(service);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    @SneakyThrows
    public void stop(CloudService service) {
        if (!(service instanceof LocalCloudService localCloudService)) {
            return;
        }

        localCloudService.state(ServiceState.STOPPING);

        if (localCloudService.process() != null) {
            if (localCloudService.group().platform().proxy()) {
                localCloudService.execute("end");
            } else {
                localCloudService.execute("stop");
            }
            localCloudService.process().waitFor(5, TimeUnit.SECONDS);
            this.shutdownProcess(localCloudService);
        }

        if (!CloudBase.instance().running()) {
            this.closeProcess(localCloudService);
        }
    }

    @SneakyThrows
    private void closeProcess(LocalCloudService service) {
        CloudAPI.instance().globalEventNode().call(new CloudServiceShutdownEvent(service));

        if (!service.group().properties().has(GroupProperties.STATIC)) {
            synchronized (this) {
                FileUtils.deleteDirectory(service.runningFolder().toFile());
                Files.deleteIfExists(service.runningFolder());
            }
        }

        ((CloudServiceProviderImpl) CloudAPI.instance().serviceProvider()).unregisterService(service);
        CloudAPI.instance().logger().info("The Service &2'&4" + service.name() + "&2' &1was successfully stopped");
    }


    @SneakyThrows
    private void shutdownProcess(@NotNull LocalCloudService service) {
        service.process().toHandle().destroyForcibly();
        service.process().waitFor();
        service.process(null);
    }

    private int nextServiceId(CloudGroup cloudGroup) {
        int id = 1;
        while (this.isIdPresent(cloudGroup, id)) {
            id++;
        }
        return id;
    }

    private boolean isIdPresent(CloudGroup group, int id) {
        return CloudAPI.instance().serviceProvider().services(group).stream().anyMatch(it -> it.orderedId() == id);
    }
}

