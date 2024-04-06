package edu.sjsu.cmpe272.simpleblog.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class RSAService {

    private final KeyPairGenerator rsaGenerator;

    KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        return rsaGenerator.generateKeyPair();
    }
}
