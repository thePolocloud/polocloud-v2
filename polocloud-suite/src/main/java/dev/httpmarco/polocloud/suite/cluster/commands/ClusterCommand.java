package dev.httpmarco.polocloud.suite.cluster.commands;

import dev.httpmarco.polocloud.grpc.ClusterService;
import dev.httpmarco.polocloud.suite.PolocloudSuite;
import dev.httpmarco.polocloud.suite.cluster.ClusterInitializer;
import dev.httpmarco.polocloud.suite.cluster.configuration.redis.RedisConfig;
import dev.httpmarco.polocloud.suite.cluster.global.ClusterSuiteData;
import dev.httpmarco.polocloud.suite.cluster.global.GlobalCluster;
import dev.httpmarco.polocloud.suite.cluster.global.suites.ExternalSuite;
import dev.httpmarco.polocloud.suite.cluster.local.LocalCluster;
import dev.httpmarco.polocloud.suite.commands.Command;
import dev.httpmarco.polocloud.suite.commands.type.IntArgument;
import dev.httpmarco.polocloud.suite.commands.type.KeywordArgument;
import dev.httpmarco.polocloud.suite.commands.type.TextArgument;
import dev.httpmarco.polocloud.suite.utils.redis.RedisClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClusterCommand extends Command {

    private static final Logger log = LogManager.getLogger(ClusterCommand.class);

    public ClusterCommand() {
        super("cluster", PolocloudSuite.instance().translation().get("cluster.command.commandDescription"));

        var cluster = PolocloudSuite.instance().cluster();
        var translation = PolocloudSuite.instance().translation();

        syntax(commandContext -> {

            log.info(translation.get("cluster.command.info", cluster instanceof LocalCluster ? "Local" : "Global"));
            log.info(" ");

            if (cluster instanceof GlobalCluster globalCluster) {
                log.info(translation.get("cluster.command.localSuite"));
                log.info(translation.get("cluster.command.localSuiteId", globalCluster.localSuite().id()));
                log.info(translation.get("cluster.command.localSuiteStatus", globalCluster.state()));
                log.info(translation.get("cluster.command.localSuiteAddress", globalCluster.localSuite().data().address()));
                log.info(translation.get("cluster.command.localSuitePrivateKey", "*".repeat(8) + globalCluster.localSuite().data().privateKey().substring(8)));
                log.info(" ");
                log.info(translation.get("cluster.command.externalSuites", globalCluster.suites().size()));

                for (int i = 0; i < globalCluster.suites().size(); i++) {
                    var suite = globalCluster.suites().get(i);
                    String prefix = "├";

                    // the last element of suites
                    if (i == globalCluster.suites().size() - 1) {
                        prefix = "└";
                    }

                    log.info(translation.get("cluster.command.suiteInfo", prefix, suiteStateColorCode(suite) + suite.data().id(), suite.data().address()));
                }
            }
        }, new KeywordArgument("info"));


        if (cluster instanceof LocalCluster) {

            var redisHostname = new TextArgument("redis-hostname");
            var redisPort = new IntArgument("redis-port");
            var redisUsername = new TextArgument("redis-username");
            var redisPassword = new TextArgument("redis-password");
            var redisDatabase = new IntArgument("redis-database");

            // create new cluster
            syntax(it -> {
                        var token = UUID.randomUUID().toString().substring(0, 16);
                        var redisClient = new RedisClient(new RedisConfig(it.arg(redisHostname), it.arg(redisPort), it.arg(redisUsername), it.arg(redisPassword), it.arg(redisDatabase)));

                        if (!checkRedisAvailable(redisClient)) {
                            return;
                        }

                        ClusterInitializer.switchToGlobalCluster(redisClient, token);
                        log.info(translation.get("cluster.command.successfulCreation"));
                    }
                    , translation.get("cluster.command.openDescription")
                    , new KeywordArgument("open")
                    , redisHostname
                    , redisPort
                    , redisUsername
                    , redisPassword
                    , redisDatabase
            );

            var id = new TextArgument("suiteId");
            var hostname = new TextArgument("hostname");
            var port = new IntArgument("port");
            var privateKey = new TextArgument("privateKey");

            syntax(it -> {
                var data = new ClusterSuiteData(it.arg(id), it.arg(hostname), it.arg(privateKey), it.arg(port));
                var externalSuite = new ExternalSuite(data);

                if (!externalSuite.available()) {
                    log.warn(translation.get("cluster.command.warnNotAvailable"));
                    return;
                }

                var result = externalSuite.clusterStub().attachSuite(ClusterService.ClusterSuiteAttachRequest.newBuilder().setSuitePrivateKey(it.arg(privateKey)).setSuiteId(data.id()).build());

                if (!result.getSuccess()) {
                    log.warn(translation.get("cluster.command.warnFailedToEnter", result.getMessage()));
                    return;
                }

                externalSuite.close();
                var redisClient = new RedisClient(new RedisConfig(it.arg(redisHostname), it.arg(redisPort), it.arg(redisUsername), it.arg(redisPassword), it.arg(redisDatabase)));

                // we only can work if the redis client is connected to the same redis server
                if (!checkRedisAvailable(redisClient)) {
                    return;
                }

                if (!redisClient.has("polocloud-cluster-" + result.getToken())) {
                    log.warn(translation.get("cluster.command.redisDatabaseMismatch"));
                    return;
                }

                var globalCluster = ClusterInitializer.switchToGlobalCluster(redisClient, result.getToken());
                // scan all existing suites
                globalCluster.initializeExternals();

                // append this new suite to the cluster suites cache
                for (var suite : globalCluster.suites()) {
                    if (suite.available()) {
                        suite.clusterStub().runtimeHandshake(ClusterService.SuiteRuntimeHandShakeRequest.newBuilder()
                                .setId(globalCluster.localSuite().id())
                                .setHostname(globalCluster.localSuite().data().hostname())
                                .setPort(globalCluster.localSuite().data().port())
                                .setPrivateKey(globalCluster.localSuite().data().privateKey())
                                .build());
                    }
                }

                log.info(translation.get("cluster.command.successfulJoin"));
            }, translation.get("cluster.command.enterDescription"), new KeywordArgument("enter"), id, hostname, port, privateKey, redisHostname, redisPort, redisUsername, redisPassword, redisDatabase);
        }

        if (cluster instanceof GlobalCluster globalCluster) {
            syntax(commandContext -> {

                if (!globalCluster.syncStorage().available()) {
                    log.warn(translation.get("cluster.command.warnRedisUnavailable"));
                    return;
                }

                LocalCluster localCluster = ClusterInitializer.switchToLocalCluster();
                log.info(translation.get("cluster.command.successfulDisconnect"));
            }, translation.get("cluster.command.drainDescription"), new KeywordArgument("drain"));
        }
    }

    private boolean checkRedisAvailable(@NotNull RedisClient client) {
        if (!client.available()) {
            log.warn(PolocloudSuite.instance().translation().get("cluster.command.redisUnavailable"));
            return false;
        }
        return true;
    }

    private String suiteStateColorCode(ExternalSuite suite) {
        return switch (suite.state()) {
            case AVAILABLE -> "&b";
            case OFFLINE -> "&8";
            case INITIALIZING -> "&e";
            case INVALID -> "&c";
            case UNRECOGNIZED -> "&7";
        };
    }
}
