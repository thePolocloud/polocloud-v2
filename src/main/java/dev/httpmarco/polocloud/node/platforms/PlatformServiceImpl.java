package dev.httpmarco.polocloud.node.platforms;

import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Singleton
public final class PlatformServiceImpl implements PlatformService {

    @Override
    public void initialize() {
        // loading versions
        log.info("Loading 0 cluster platforms&8.");
    }
}
