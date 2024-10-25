package dev.httpmarco.polocloud.node.modules;

import java.net.URLClassLoader;

public record LoadedModule(CloudModule cloudModule, URLClassLoader moduleClassLoader, ModuleMetadata metadata) {
}