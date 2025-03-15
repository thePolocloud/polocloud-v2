package dev.httpmarco.polocloud.suite;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

@UtilityClass
public class PolocloudShutdownHandler {

    private static final Logger log = LogManager.getLogger(PolocloudShutdownHandler.class);

    private final String SHUTDOWN_HOOK = "polocloud-shutdown-hook";
    private boolean idleShutdown = false;

    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(PolocloudShutdownHandler::shutdown, SHUTDOWN_HOOK));
    }

    public void shutdown() {
        if(idleShutdown) {
            return;
        }
        idleShutdown = true;

        var cluster = PolocloudSuite.instance().cluster();
        var translation = PolocloudSuite.instance().translation();

        if(cluster != null) {
            log.info(translation.get("shutdown.cluster"));
            cluster.close();
            log.info(translation.get("shutdown.clusterSuccessfully"));
        }

        AnsiConsole.systemUninstall();
        log.info(translation.get("shutdown.suite"));

        if(!Thread.currentThread().getName().equals(SHUTDOWN_HOOK)) {
            System.exit(-1);
        }
    }
}
