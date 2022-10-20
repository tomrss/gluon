package io.tomrss.gluon.cli;

import io.tomrss.gluon.cli.command.CreateCommand;
import io.tomrss.gluon.cli.logging.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(
        name = "gluon",
        version = "gluon command line interface v0.1.0",
        subcommands = {CreateCommand.class, HelpCommand.class},
        mixinStandardHelpOptions = true
)
public class GluonCommandLine {

    private static final Logger LOG = LoggerFactory.getLogger(GluonCommandLine.class);

    @Option(names = {"-L", "--log-level"}, defaultValue = "info", description = "Log level")
    private String logLevel;

    private int executionStrategy(ParseResult parseResult) {
        try {
            init();
        } catch (Exception e) {
            System.err.println("Unable to initialize command line interface");
            e.printStackTrace();
            return 2;
        }
        return new RunLast().execute(parseResult);
    }

    private void init() {
        Logging.configureLogger(logLevel);;
    }

    public static void main(String[] args) {
        final GluonCommandLine gluonCli = new GluonCommandLine();
        int exitCode = new CommandLine(gluonCli)
                .setExecutionStrategy(gluonCli::executionStrategy)
                .execute(args);
        System.exit(exitCode);
    }
}
