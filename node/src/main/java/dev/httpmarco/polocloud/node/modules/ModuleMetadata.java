package dev.httpmarco.polocloud.node.modules;

public record ModuleMetadata(String id, String name, String version, String description, String author, String main) {

    public boolean isValid() {
        return id != null && name != null && version != null && description != null && author != null && main != null;
    }
}