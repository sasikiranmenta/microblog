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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
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

        int next = Integer.parseInt(data.getStartingId());
        int remaining = data.getCount();
        while (remaining > 0) {
            ListRequest listRequest = ListRequest.builder().limit(Math.min(remaining, 20)).next(next).build();
            ResponseEntity<List<ListResponse>> response = restTemplate.exchange(blogHost + "/messages/list", HttpMethod.POST, new HttpEntity<>(listRequest), new ParameterizedTypeReference<>() {
            });
            List<ListResponse> msgList = response.getBody();
            msgList.forEach(this::printMessageAndSaveAttachment);
            remaining -= 20;

            if (msgList.isEmpty()) {
                break;
            }
            next = msgList.get(msgList.size() - 1).getMessageId() - 1;
        }

    }

    @SneakyThrows
    private void printMessageAndSaveAttachment(ListResponse response) {
        saveAttachment(response.getAttachment(), "message-" + response.getMessageId() + ".out");
        printMessage(response);
    }

    private void saveAttachment(String encodedData, String fileName) throws IOException {
        byte[] data = Base64.getDecoder().decode(encodedData);
        File file = new File(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
        }
    }

    private void printMessage(ListResponse response) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX");
        String strDate = formatter.format(response.date);
        String message = String.format("%s: %s %s says \"%s\"", response.getMessageId(), strDate, response.getAuthor(), response.getMessage());
        if (response.getAttachment() != null) {
            message += " \uD83D\uDCCE";
        }
        log.info(message);
    }

    private Wini readIniFile() throws IOException {
        return new Wini(new File("mb.ini"));
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ListResponse {
        @JsonAlias("message-id")
        int messageId;
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
