package edu.sjsu.cmpe272.simpleblog.client.command;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCommandImpl implements ClientCommand<CommandData.Create> {

    private final KeyPairGenerator rsaKeyPairGenerator;
    private final RestTemplate restTemplate;

    @Value("${blog.host}")
    String blogHost;

    @Override
    public CommandType getCommandType() {
        return CommandType.CREATE;
    }

    @Override
    @SneakyThrows
    public void executeCommand(CommandData.Create data) {
        KeyPair rsaPair = rsaKeyPairGenerator.generateKeyPair();

        CreateCommandImpl.CreateRequest createRequest = CreateRequest.builder()
                .user(data.getId())
                .publicKey(Base64.getEncoder().encodeToString(rsaPair.getPublic().getEncoded()))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(blogHost + "/user/create", createRequest, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            storeKey(data.getId(), Base64.getEncoder().encodeToString(rsaPair.getPrivate().getEncoded()));
        }
        log.info("Successfully created user: {}", data.getId());
    }

    private void storeKey(String id, String privateKey) throws IOException {
        File file = new File("mb.ini");
        if (file.createNewFile()) {
            Wini ini = new Wini(file);
            ini.put("blog", "private_key", privateKey);
            ini.put("blog", "id", id);
            ini.store();
        }
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateResponse {
        String message;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class CreateRequest {
        String user;
        @JsonAlias("public-key")
        String publicKey;
    }
}
