package dev.httpmarco.polocloud.api.modules;

import java.net.URLClassLoader;

public record LoadedModule(CloudModule cloudModule, URLClassLoader moduleClassLoader, ModuleMetadata metadata) {
}