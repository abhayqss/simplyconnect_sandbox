package com.scnsoft.eldermark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional(readOnly = true)
public class DocumentEncryptionServiceImpl implements DocumentEncryptionService {

    @Autowired
    private EncryptionKeyService encryptionKeyService;

    private SecretKey secretKey;

    @PostConstruct
    private void loadSecretKey() {
        secretKey = encryptionKeyService.find();
    }

    @Override
    public byte[] decrypt(byte[] encrypted) {
        try {
            Cipher cipher = initiateCipherForDecrypt(secretKey);
            return cipherProcess(encrypted, cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error while decrypting file");
        }
    }

    @Override
    public byte[] encrypt(byte[] decrypted) {
        try {
            Cipher cipher = initiateCipherForEncrypt(secretKey);
            return cipherProcess(decrypted, cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error while encrypting file");
        }
    }

    private Cipher initiateCipherForEncrypt(SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    private Cipher initiateCipherForDecrypt(SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher;
    }

    private byte[] cipherProcess(byte[] bytes, Cipher cipher) {
        try {
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Error while encrypting/decrypting file");
        }
    }

}
