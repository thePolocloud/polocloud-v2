package dev.httpmarco.polocloud.suite.cluster.suits;

import dev.httpmarco.polocloud.suite.cluster.ClusterSuite;
import dev.httpmarco.polocloud.suite.cluster.data.LocalSuiteData;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class LocalSuite implements ClusterSuite {

    private static final Logger log = LogManager.getLogger(LocalSuite.class);
    private final Server server;

    public LocalSuite(LocalSuiteData data) {
        this.server = ServerBuilder.forPort(data.port()).build();

        try {
            this.server.start();

            log.info("Server started on port 9091");
        } catch (IOException e) {
            e.printStackTrace(System.err);
            // todo call shutdown methode
        }
    }
}
