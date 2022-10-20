package io.tomrss.gluon.cli.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import picocli.CommandLine;

public class AnsiColorLevelConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        final String levelStr = event.getLevel().levelStr;
        final String color = switch (levelStr) {
            case "TRACE" -> "purple";
            case "DEBUG" -> "blue";
            case "INFO" -> "green";
            case "WARN" -> "orange";
            case "ERROR" -> "red";
            default -> "yellow";
        };
        return CommandLine.Help.Ansi.AUTO.string("@|bold," + color + " " + levelStr + "|@");
    }
}
