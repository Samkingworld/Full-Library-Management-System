package com.groupproject.libraryManagementSystem.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
@Data
public class RSAKeyProperties {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSAKeyProperties() {
        KeyPair pair = KeyGeneratorUtility.generateRsaKey();
        publicKey = (RSAPublicKey) pair.getPublic();
        privateKey = (RSAPrivateKey) pair.getPrivate();
    }

}
