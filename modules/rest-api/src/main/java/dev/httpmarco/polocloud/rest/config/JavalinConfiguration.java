package dev.httpmarco.polocloud.rest.config;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class JavalinConfiguration {

    private final String hostname;
    private final int port;

    public JavalinConfiguration() {
        this.hostname = "localhost";
        this.port = 8080;
    }
}
