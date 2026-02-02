package com.scnsoft.simplyconnect.bulkfileencrypt.service;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
public class DocumentEncryptionServiceImpl implements DocumentEncryptionService {

    @Autowired
    private EncryptionKeyService encryptionKeyService;

    @Autowired
    private EncryptedDocumentService encryptedDocumentService;

    private SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = encryptionKeyService.find();
        }
        return secretKey;
    }

    @Override
    public void encryptAllFiles(String stringPath, Boolean checkIfAlreadyEncrypted) {
        try {
            Cipher cipher = initiateCipherForEncrypt(getSecretKey());
            Files.walk(Paths.get(stringPath)).filter(Files::isRegularFile).forEach(subPath -> {
                if (BooleanUtils.isNotTrue(checkIfAlreadyEncrypted) || !encryptedDocumentService.isEncrypted(subPath.toString())) {
                    var bytes = readFileBytesFromPath(subPath);
                    var encrypted = cipherProcess(bytes, cipher);
                    saveToFilePath(encrypted, subPath);
                    encryptedDocumentService.markEncrypted(subPath.toString());
                    System.out.println("File encrypted: " + subPath.toString());
                }
            });
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error while encrypting file");
        } catch (IOException e) {
            throw new RuntimeException("I/O error");
        }
    }

    private Cipher initiateCipherForEncrypt(SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    private byte[] readFileBytesFromPath(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("I/O error");
        }
    }

    private byte[] cipherProcess(byte[] bytes, Cipher cipher) {
        try {
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Error while encrypting file");
        }
    }

    private void saveToFilePath(byte[] bytes, Path path) {
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException("I/O error");
        }

    }
}
