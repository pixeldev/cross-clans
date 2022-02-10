package net.cosmogrp.crclans.log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHandler {

    private final Logger logger;

    public LogHandler(Logger logger) {
        this.logger = logger;
    }

    public void reportError(
            String message, Throwable throwable,
            String... args
    ) {
        logger.log(Level.WARNING, message, args);
        if (throwable != null) {
            // make it thread-safe
            throwable.printStackTrace(System.err);
        }
    }

    public void info(
            String message, String... args
    ) {
        logger.log(Level.INFO, message, args);
    }
}
