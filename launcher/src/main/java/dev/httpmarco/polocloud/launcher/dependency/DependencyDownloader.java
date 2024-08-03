package dev.httpmarco.polocloud.launcher.dependency;

import dev.httpmarco.polocloud.launcher.PoloCloudLauncher;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class DependencyDownloader {

    private static final Path DOWNLOAD_DIR = Path.of("local/dependencies");
    private static final ExecutorService DOWNLOAD_TASK = Executors.newCachedThreadPool();

    static {
        DOWNLOAD_DIR.toFile().mkdirs();

        Runtime.getRuntime().addShutdownHook(new Thread(DOWNLOAD_TASK::shutdown));
    }

    @SneakyThrows
    public void download(Dependency dependency) {
        var file = DOWNLOAD_DIR.resolve(dependency + ".jar").toFile();

        if (file.exists()) {
            PoloCloudLauncher.CLASS_LOADER.addURL(file.toURI().toURL());
            return;
        }

        DependencyHelper.download(dependency.downloadUrl(), file);
        PoloCloudLauncher.CLASS_LOADER.addURL(file.toURI().toURL());

        if (dependency.withSubDependencies()) {
            dependency.loadSubDependencies();
            for (var subDependency : dependency.subDependencies()) {
                download(subDependency);
            }
        }
    }

    public static void download(Dependency... dependencies) {
        downloadDependenciesWithProgress(List.of(dependencies));
    }

    private void downloadDependenciesWithProgress(List<Dependency> dependencies) {
        int totalDependencies = dependencies.size();
        for (int i = 0; i < totalDependencies; i++) {
            var dependency = dependencies.get(i);
            logProgress(totalDependencies, i + 1, dependency.artifactId());
            download(dependency);
        }

        clearTerminal();
    }

    private void logProgress(int total, int current, String name) {
        var time = LocalTime.now();
        var formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
        System.out.printf("\r%s \u001B[90m| \u001B[36mINFO\u001B[90m:\u001B[0m Downloading Dependencies - %s (\u001B[36m%d\u001B[0m/\u001B[36m%d\u001B[0m)", formattedTime, name, current, total);
    }

    private void clearTerminal() {
        System.out.print("\r");
        System.out.print(" ".repeat(80));
        System.out.print("\r");
    }
}
