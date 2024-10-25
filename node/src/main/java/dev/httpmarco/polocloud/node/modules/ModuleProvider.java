package dev.httpmarco.polocloud.node.modules;

import dev.httpmarco.polocloud.api.Reloadable;
import dev.httpmarco.polocloud.launcher.PoloCloudLauncher;
import dev.httpmarco.polocloud.node.util.JsonUtils;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;

@Log4j2
@Accessors(fluent = true)
public class ModuleProvider implements Reloadable {

    private static final Path MODULE_PATH = Path.of("./local/modules/");
    private final List<LoadedModule> loadedModules = new CopyOnWriteArrayList<>();

    @SneakyThrows
    public ModuleProvider() {
        if (!Files.exists(MODULE_PATH)) {
            Files.createDirectory(MODULE_PATH);
        }
    }

    @SneakyThrows
    public void loadAllUnloadedModules() {
        var moduleFiles = this.getAllModuleJarFiles();
        var loadedModules = new ArrayList<String>();
        var unloadedModules = new ArrayList<String>();

        for (var file : moduleFiles) {
            try (var jarFile = new JarFile(file)) {
                var json = jarFile.getJarEntry("module.json");
                if (json == null) {
                    log.error("File &b\"{}\" &7is in the modules path but does not contain a module.json", file.getName());
                    continue;
                }

                var reader = new InputStreamReader(jarFile.getInputStream(json));
                var moduleMetadata = JsonUtils.GSON.fromJson(reader, ModuleMetadata.class);

                if (moduleMetadata == null || !moduleMetadata.isValid()) {
                    log.error("Module &b\"{}\" &7has an invalid module.json", file.getName());
                    continue;
                }

                try {
                    this.loadModuleFileContent(file, moduleMetadata);
                    loadedModules.add(moduleMetadata.name());
                } catch (Exception e) {
                    unloadedModules.add(moduleMetadata.name());
                    log.error("Failed to load module: {}", moduleMetadata.name());
                    e.printStackTrace();
                }
            }
        }

        final var modules = new ArrayList<>(loadedModules.stream().map(it -> "&7" + it).toList());
        modules.addAll(unloadedModules.stream().map(it -> "&6" + it).toList());

        if (!modules.isEmpty()) {
            log.info("Loaded modules&8: {}", String.join("&8, ", modules));
            this.loadedModules().forEach(it -> it.cloudModule().onEnable());
        }
    }

    public void unloadAllModules() {
        this.loadedModules.forEach(this::unloadModule);
    }

    public List<LoadedModule> loadedModules() {
        return new ArrayList<>(this.loadedModules);
    }

    @SneakyThrows
    private void loadModuleFileContent(File file, ModuleMetadata metadata) {
        var cloudModule = this.loadModule(file, metadata.main());
        var classLoader = (URLClassLoader) cloudModule.getClass().getClassLoader();
        var loadedModule = new LoadedModule(cloudModule, classLoader, metadata);

        this.loadedModules.add(loadedModule);
    }

    @SneakyThrows
    private CloudModule loadModule(File file, String mainClass) {
        var classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()},  PoloCloudLauncher.CLASS_LOADER);

        var clazz = classLoader.loadClass(mainClass);
        if (!CloudModule.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + mainClass + " does not implement CloudModule");
        }

        return (CloudModule) clazz.getDeclaredConstructor().newInstance();
    }

    @SneakyThrows
    private void unloadModule(LoadedModule loadedModule) {
        loadedModule.cloudModule().onDisable();
        loadedModule.moduleClassLoader().close();
        this.loadedModules.remove(loadedModule);
    }

    private List<File> getAllModuleJarFiles() {
        var files = MODULE_PATH.toFile().listFiles((dir, name) -> name.endsWith(".jar"));
        return files != null ? List.of(files) : List.of();
    }

    @Override
    public void reload() {
        this.unloadAllModules();
        this.loadAllUnloadedModules();
    }
}