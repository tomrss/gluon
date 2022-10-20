package io.tomrss.gluon.cli.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Logging {
    private Logging() {
    }

    public static void configureLogger(String logLevel) {
        final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        final LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();

        @SuppressWarnings("unchecked")
        Map<String, String> ruleRegistry = (Map<String, String>) logCtx.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        if (ruleRegistry == null) {
            ruleRegistry = new HashMap<>();
            logCtx.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
        }
        ruleRegistry.put("logLevel", AnsiColorLevelConverter.class.getName());

        final PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(logCtx);
        logEncoder.setPattern("%logLevel %msg%n");
        logEncoder.start();

        final ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(logCtx);
        consoleAppender.setName("console");
        consoleAppender.setEncoder(logEncoder);
        consoleAppender.start();

        rootLogger.setLevel(Level.toLevel(logLevel));
        rootLogger.detachAndStopAllAppenders();
        rootLogger.addAppender(consoleAppender);
    }
}
