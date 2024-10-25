package dev.httpmarco.polocloud.modules.example;

import dev.httpmarco.polocloud.api.modules.CloudModule;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ExampleModule implements CloudModule {

    @Override
    public void onEnable() {
        log.info("Example Module enabled");
    }

    @Override
    public void onDisable() {
        log.info("Example Module disabled");
    }
}
