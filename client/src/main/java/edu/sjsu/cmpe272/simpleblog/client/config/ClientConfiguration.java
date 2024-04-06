package edu.sjsu.cmpe272.simpleblog.client.config;

import edu.sjsu.cmpe272.simpleblog.client.command.ClientCommand;
import edu.sjsu.cmpe272.simpleblog.client.command.CommandData;
import edu.sjsu.cmpe272.simpleblog.client.command.CommandType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ClientConfiguration {

    @Bean("rsaGenerator")
    public KeyPairGenerator rsaKeyPairGenerator() throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance("RSA");
    }

    @Bean("clientCommandFactory")
    public Map<CommandType, ClientCommand<? extends CommandData.Data>> clientCommandFactory(List<ClientCommand<? extends CommandData.Data>> commands) {
        return commands.stream()
                .collect(Collectors.toMap(ClientCommand::getCommandType, Function.identity()));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
