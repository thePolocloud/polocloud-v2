package dev.httpmarco.polocloud.modules.rest;

import dev.httpmarco.polocloud.modules.rest.auth.JwtProvider;
import dev.httpmarco.polocloud.modules.rest.configuration.Config;
import dev.httpmarco.polocloud.modules.rest.controller.ControllerService;
import dev.httpmarco.polocloud.modules.rest.auth.user.UserService;
import dev.httpmarco.polocloud.modules.rest.socket.web.WebSocketService;
import dev.httpmarco.polocloud.api.modules.CloudModule;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Accessors(fluent = true)
public class RestModule implements CloudModule {

    public static RestModule instance;
    private Config config;
    private Javalin app;
    private WebSocketService webSocketService;
    private JwtProvider jwtProvider;
    private UserService userService;
    private ControllerService controllerService;

    @Override
    public void onEnable() {
        /*
        DependencyDownloader.download(
                new Dependency("org.jetbrains.kotlin", "kotlin-stdlib", "1.9.0"),//TODO funktionierend machen
                new Dependency("io.javalin", "javalin", "6.3.0", true),
                new Dependency("com.auth0", "java-jwt", "4.4.0"));*/

        instance = this;

        this.config = new Config();

        startUpJavalin();
        this.webSocketService = new WebSocketService(this);

        this.jwtProvider = new JwtProvider(this.config.usersConfiguration().secret());
        this.userService = new UserService(this);
        this.controllerService = new ControllerService(this);
    }

    @Override
    public void onDisable() {
        if (this.app != null) {
            new Thread(() -> {
                try {
                    this.app.stop();
                    log.info("Javalin server stopped.");
                } catch (Exception e) {
                    log.error("Error while stopping Javalin: ", e);
                }
            }).start();
        }
    }

    private void startUpJavalin() {
        this.app = Javalin.create(config -> {
            config.jetty.defaultHost = this.config.javalinConfiguration().hostname();
            config.jetty.defaultPort = this.config.javalinConfiguration().port();
            config.showJavalinBanner = false;

            config.jetty.modifyServer((server) -> {
                server.setStopTimeout(5000);
                server.setStopAtShutdown(true);
            });

            config.pvt.javaLangErrorHandler(((httpServletResponse, error) -> {
                httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
                error.printStackTrace();
            }));
        }).start();

        setupExceptionHandling();
    }

    private void setupExceptionHandling() {
        this.app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            log.error("An error occurred: {}", e.getMessage());
        });
    }
}
