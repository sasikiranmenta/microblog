package edu.sjsu.cmpe272.simpleblog.client;

import edu.sjsu.cmpe272.simpleblog.client.command.*;
import edu.sjsu.cmpe272.simpleblog.client.mapper.CommandDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Map;

@SpringBootApplication
@Command
@RequiredArgsConstructor
public class ClientApplication implements CommandLineRunner, ExitCodeGenerator {


    private final CommandLine.IFactory iFactory;
    private final ConfigurableApplicationContext context;
    private final Map<CommandType, ClientCommand<? extends CommandData.Data>> clientCommandFactory;
    private final CommandDataMapper commandDataMapper;

    @Command
    public int post(@Parameters String message, @Parameters(paramLabel = "file-to-attach", defaultValue = "null") String attachment) {
        System.out.println("post");
        CommandData.Post post = commandDataMapper.mapToPostCommandData(message, attachment);
        ((PostCommandImpl)clientCommandFactory.get(CommandType.POST)).executeCommand(post);
        return 2;
    }

    @Command
    int create(@Parameters String id) {
        System.out.println("create");
        CommandData.Create create = commandDataMapper.mapToCreateCommandData(id);
        ((CreateCommandImpl) clientCommandFactory.get(CommandType.CREATE)).executeCommand(create);
        return 2;
    }

    @Command
    int list(@Parameters String starting, @Parameters int count, @Parameters(paramLabel = "save-attachment") boolean saveAttachment) {
        System.out.println("list");
        CommandData.List list = commandDataMapper.mapToListCommandData(starting, count, saveAttachment);
        ((ListCommandImpl) clientCommandFactory.get(CommandType.LIST)).executeCommand(list);
        return 2;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(ClientApplication.class).web(WebApplicationType.NONE)
                .run(args);
    }

    int exitCode;

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(this, iFactory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
