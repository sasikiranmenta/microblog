package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.MessageSignature;
import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.SignatureOrder;
import edu.sjsu.cmpe272.simpleblog.server.exception.NoMessageSignatureFoundException;
import edu.sjsu.cmpe272.simpleblog.server.exception.SignatureMismatchException;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SignatureValidationServiceImpl implements SignatureValidationService {
    @Override
    @SneakyThrows
    public <T> void validateSignature(T request, UserDetails userDetails) {
        PublicKey publicKey = extractPublicKey(userDetails.getPublicKey());

        String payload = constructSignaturePayload(request);
        byte[] shaDigest = generateSHA256Digest(payload);

        Signature signature = generatePublicKeySignature(publicKey, shaDigest);
        String signatureFromSource = extractMessageSignature(request);
        boolean isValid = signature.verify(Base64.getDecoder().decode(signatureFromSource));

        if (!isValid) {
            log.warn("Signature validation failed");
            throw new SignatureMismatchException();
        }
        log.info("Signature validated successfully");
    }

    @SneakyThrows
    private PublicKey extractPublicKey(String publicKey) {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return keyFactory.generatePublic(publicKeySpec);
    }

    @SneakyThrows
    private byte[] generateSHA256Digest(String payload) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(payload.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    private Signature generatePublicKeySignature(PublicKey publicKey, byte[] shaDigest) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(shaDigest);
        return signature;
    }

    private <T> String constructSignaturePayload(T targetObj) {
        Field[] fields = targetObj.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> this.mapToOrderAndPayload(field, targetObj))
                .filter(OrderAndPayload::isInclude)
                .sorted(Comparator.comparingInt(OrderAndPayload::getOrder))
                .map(OrderAndPayload::getPayload)
                .map(payload -> payload.replaceAll(" ", ""))
                .collect(Collectors.joining(""));
    }

    @SneakyThrows
    private <T> OrderAndPayload mapToOrderAndPayload(Field field, T targetObj) {
        SignatureOrder signatureOrder = field.getDeclaredAnnotation(SignatureOrder.class);
        field.setAccessible(true);
        Object payload = field.get(targetObj);
        field.setAccessible(false);

        if (signatureOrder == null || (payload == null && signatureOrder.ignoreIfEmpty())) {
            return OrderAndPayload.builder().include(false).build();
        }
        if (payload == null) {
            return OrderAndPayload.builder().order(signatureOrder.order()).include(true).build();
        }
        return OrderAndPayload.builder().order(signatureOrder.order()).include(true).payload(payload.toString()).build();
    }

    @SneakyThrows
    private <T> String extractMessageSignature(T targetObj) {
        Field[] fields = targetObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            MessageSignature messageSignature = field.getDeclaredAnnotation(MessageSignature.class);
            if (messageSignature != null) {
                field.setAccessible(true);
                String payload = (String) field.get(targetObj);
                field.setAccessible(false);
                return payload;
            }
        }
        throw new NoMessageSignatureFoundException();
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    static class OrderAndPayload {
        int order;
        String payload;
        boolean include;
    }
}
