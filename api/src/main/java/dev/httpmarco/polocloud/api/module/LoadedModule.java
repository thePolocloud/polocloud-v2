package dev.httpmarco.polocloud.api.module;

import java.net.URLClassLoader;

public record LoadedModule(CloudModule cloudModule, URLClassLoader moduleClassLoader, ModuleMetadata metadata) {
}
