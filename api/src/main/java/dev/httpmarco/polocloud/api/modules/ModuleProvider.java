package dev.httpmarco.polocloud.api.modules;

import dev.httpmarco.polocloud.api.Reloadable;

import java.util.List;

public abstract class ModuleProvider implements Reloadable {

    public abstract void loadAllUnloadedModules();

    public abstract void unloadAllModules();

    public abstract List<LoadedModule> loadedModules();
}
