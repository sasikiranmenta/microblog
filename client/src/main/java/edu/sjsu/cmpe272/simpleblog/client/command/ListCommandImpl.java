package edu.sjsu.cmpe272.simpleblog.client.command;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCommandImpl implements ClientCommand<CommandData.List> {

    private final RestTemplate restTemplate;

    @Value("${blog.host}")
    String blogHost;
    @Override
    public CommandType getCommandType() {
        return CommandType.LIST;
    }

    @Override
    @SneakyThrows
    public void executeCommand(CommandData.List data) {
        Wini ini = readIniFile();
        ListRequest listRequest = ListRequest.builder().limit(data.getCount()).next(Integer.parseInt(data.getStartingId())).build();

        ResponseEntity<List<ListResponse>> response = restTemplate.exchange(blogHost + "/messages/list", HttpMethod.POST, new HttpEntity<>(listRequest), new ParameterizedTypeReference<>(){});
        response.toString();
    }

    private Wini readIniFile() throws IOException {
        return new Wini(new File("md.ini"));
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ListResponse  {
        @JsonAlias("message-id")
        String messageId;
        Date date;
        String author;
        String message;
        String attachment;
        String signature;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class ListRequest {
        int limit;
        int next;
    }
}
