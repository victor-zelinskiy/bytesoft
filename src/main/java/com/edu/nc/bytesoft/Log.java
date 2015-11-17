package com.edu.nc.bytesoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private Logger logger;

    public Log(Logger logger) {
        this.logger = logger;
    }

    public static Log get(Class aClass) {
        return new Log(LoggerFactory.getLogger(aClass));
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void info(String msg, Object... arguments) {
        logger.info(msg, arguments);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

}