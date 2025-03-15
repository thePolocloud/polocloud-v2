package dev.httpmarco.polocloud.suite.cluster.tasks;

import dev.httpmarco.polocloud.grpc.ClusterService;
import dev.httpmarco.polocloud.suite.PolocloudSuite;
import dev.httpmarco.polocloud.suite.cluster.global.GlobalCluster;
import dev.httpmarco.polocloud.suite.utils.tasks.TickTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClusterStatusTask extends TickTask {

    private static final Logger log = LogManager.getLogger(ClusterStatusTask.class);
    private final GlobalCluster cluster;

    public ClusterStatusTask(GlobalCluster cluster) {
        // every 3 seconds
        super(3);

        this.cluster = cluster;
    }

    @Override
    public void start() {
        this.requestState();
        super.start();
    }

    @Override
    public void onTick() {
        this.requestState();
    }

    public void requestState() {
        for (var suite : this.cluster.suites()) {

            var currentState = suite.state();
            var state = suite.available() ? suite.clusterStub().requestState(ClusterService.EmptyCall.newBuilder().build()).getState() : ClusterService.State.OFFLINE;

            if (state != currentState) {
                var translation = PolocloudSuite.instance().translation();
                // we must update the new state
                if ((currentState == ClusterService.State.OFFLINE || currentState == ClusterService.State.INITIALIZING) && state == ClusterService.State.AVAILABLE) {
                    log.info(translation.get("cluster.notification.suiteOnline", suite.id()));
                }

                if((currentState == ClusterService.State.AVAILABLE || currentState == ClusterService.State.INITIALIZING) && state == ClusterService.State.OFFLINE) {
                    log.info(translation.get("cluster.notification.suiteOffline", suite.id()));
                }

                suite.state(state);
            }
        }
    }
}
