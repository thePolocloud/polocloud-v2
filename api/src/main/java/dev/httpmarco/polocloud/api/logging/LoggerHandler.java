package dev.httpmarco.polocloud.api.logging;

import java.util.logging.Level;

public interface LoggerHandler {

    void print(Level level, String message, Throwable throwable, Object... objects);

    void close();

}