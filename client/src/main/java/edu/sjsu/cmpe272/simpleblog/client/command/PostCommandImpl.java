package edu.sjsu.cmpe272.simpleblog.client.command;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
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
                .message(data.message)
                .attachment(getEncodedData(data.fileName))
                .build();

        request.setSignature(calculateSignature(request, privateKey));

        ResponseEntity<String> response = restTemplate.postForEntity(blogHost + "/messages/create", request, String.class);
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
        String payload = "";
        payload += request.getDate().toString();
        payload += request.getAuthor();
        payload += request.getMessage();
        if(request.getAttachment() != null) {
           payload+= request.getAttachment();
        }
        payload = payload.replaceAll(" ", "");

        ResponseEntity<String> publicKeyResponse = restTemplate.getForEntity(blogHost + "/user/"+request.getAuthor()+"/public-key", String.class);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        byte[] shaDigest = md.digest(payload.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(shaDigest);
        byte[] signedHash = signature.sign();

//        Cipher encryptCipher = Cipher.getInstance("RSA");
//        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//        byte[] encryptedMessageBytes = encryptCipher.doFinal(shaDigest);
        return Base64.getEncoder().encodeToString(signedHash);
    }

    private Wini readIniFile() throws IOException {
        return new Wini(new File("md.ini"));
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
}
