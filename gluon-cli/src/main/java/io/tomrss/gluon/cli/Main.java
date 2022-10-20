package io.tomrss.gluon.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import io.tomrss.gluon.core.GluonBuilder;
import io.tomrss.gluon.core.persistence.DatabaseVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;

@Command(name = "gluon", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    public static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Option(names = {"-L", "--log-level"}, defaultValue = "info")
    private String logLevel;

    @Option(names = "--projectGroupId")
    private String projectGroupId;

    @Option(names = "--projectArtifactId")
    private String projectArtifactId;

    @Option(names = "--projectVersion")
    private String projectVersion;

    @Option(names = "--projectFriendlyName")
    private String projectFriendlyName;

    @Option(names = "--projectDescription")
    private String projectDescription;

    @Option(names = "--basePackage")
    private String basePackage;

    @Option(names = "--customTemplates")
    private String customTemplates;

    @Option(names = "--projectDirectory")
    private String projectDirectory;

    @Option(names = "--imageRegistry")
    private String imageRegistry;

    @Option(names = "--databaseVendor")
    private String databaseVendor;

    @Option(names = "--templateExtension")
    private String templateExtension;

    @Option(names = "--entities")
    private String entities;

    @Option(names = "--archetype")
    private String archetype;

    @Override
    public Integer call() throws Exception {
        configureLogger();
        try {
            final GluonBuilder gluonBuilder = new GluonBuilder();

            // all this optional hassle means: let gluon builder set the defaults, don't bother here
            Optional.ofNullable(customTemplates).map(Paths::get).ifPresent(gluonBuilder::customTemplatesDirectory);
            Optional.ofNullable(projectDirectory).map(Paths::get).ifPresent(gluonBuilder::projectDirectory);
            Optional.ofNullable(projectGroupId).ifPresent(gluonBuilder::groupId);
            Optional.ofNullable(projectArtifactId).ifPresent(gluonBuilder::artifactId);
            Optional.ofNullable(projectVersion).ifPresent(gluonBuilder::version);
            Optional.ofNullable(projectFriendlyName).ifPresent(gluonBuilder::friendlyName);
            Optional.ofNullable(projectDescription).ifPresent(gluonBuilder::description);
            Optional.ofNullable(basePackage).ifPresent(gluonBuilder::basePackage);
            Optional.ofNullable(imageRegistry).ifPresent(gluonBuilder::imageRegistry);
            Optional.ofNullable(databaseVendor).map(DatabaseVendor::valueOf).ifPresent(gluonBuilder::databaseVendor);
            Optional.ofNullable(templateExtension).ifPresent(gluonBuilder::templateExtension);
            Optional.ofNullable(entities).map(Paths::get).ifPresent(gluonBuilder::entityDirectory);
            Optional.ofNullable(archetype).ifPresent(gluonBuilder::archetype);

            gluonBuilder.createGluon().generateProject();
            return 0;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Unable to generate project", e);
            } else {
                // do not print stack trace
                LOG.error("Unable to generate project: " + e.getMessage());
            }
            return 1;
        }
    }

    private void configureLogger() {
        final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        final LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();

        final PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(logCtx);
        logEncoder.setPattern("[%-5level] %msg%n");
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

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
