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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCommandImpl implements ClientCommand<CommandData.Post> {

    private final RestTemplate restTemplate;

    @Value("${blog.host}")
    String blogHost;

    @Override
    public CommandType getCommandType() {
        return CommandType.POST;
    }

    @Override
    @SneakyThrows
    public void executeCommand(CommandData.Post data) {
        Wini ini = readIniFile();

        String id = ini.get("blog", "id", String.class);
        String privateKey = ini.get("blog", "private_key", String.class);
        CreateRequest request = CreateRequest.builder()
                .date(new Date())
                .author(id)
                .message(data.getMessage())
                .attachment(getEncodedData(data.getFileName()))
                .build();

        request.setSignature(calculateSignature(request, privateKey));

        ResponseEntity<CreatedResponse> response = restTemplate.postForEntity(blogHost + "/messages/create", request, CreatedResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully published messages, message id: {}", response.getBody().getMessageId());
        } else {
            log.info("Publishing message failed, ReceivedStatus code: {}", response.getStatusCode());
        }
    }

    private String getEncodedData(String fileName) throws IOException {
        if(!fileName.equals("null")) {
            return encodeFileToBase64(new File(fileName));
        }
        return null;
    }

    private static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toByteArray();
        } finally {
            inputStream.close();
        }
    }

    public static String encodeFileToBase64(File file) throws IOException {
        byte[] fileContent = readFileToByteArray(file);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    @SneakyThrows
    private String calculateSignature(CreateRequest request, String privateKeyStr) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        String payload = generateSignaturePayload(request);
        byte[] shaDigest = md.digest(payload.getBytes(StandardCharsets.UTF_8));

        PrivateKey privateKey = extractPrivateKey(privateKeyStr);
        byte[] signature = calculateSignature(privateKey, shaDigest);
        return Base64.getEncoder().encodeToString(signature);
    }

    private String generateSignaturePayload(CreateRequest request) {
        String payload = "";
        payload += request.getDate().toString();
        payload += request.getAuthor();
        payload += request.getMessage();
        if(request.getAttachment() != null) {
            payload += request.getAttachment();
        }
        return payload.replaceAll(" ", "");
    }

    @SneakyThrows
    private PrivateKey extractPrivateKey(String encodedPrivateKey) {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedPrivateKey));
        return keyFactory.generatePrivate(privateKeySpec);
    }

    @SneakyThrows
    private byte[] calculateSignature(PrivateKey privateKey, byte[] shaDigest) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(shaDigest);
        return signature.sign();
    }

    private Wini readIniFile() throws IOException {
        return new Wini(new File("mb.ini"));
    }

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateRequest {
        Date date;
        String author;
        String message;
        String attachment;
        String signature;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreatedResponse {
        @JsonAlias("message-id")
        int messageId;
    }
}
