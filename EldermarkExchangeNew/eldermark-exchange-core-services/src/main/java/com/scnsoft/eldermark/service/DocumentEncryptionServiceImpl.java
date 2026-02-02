package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional(readOnly = true)
public class DocumentEncryptionServiceImpl implements DocumentEncryptionService {

    @Autowired
    private EncryptionKeyService encryptionKeyService;

    private SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = encryptionKeyService.find();
        }
        return secretKey;
    }

    @Override
    public byte[] decrypt(byte[] encrypted) {
        try {
            Cipher cipher = initiateCipherForDecrypt(getSecretKey());
            return cipherProcess(encrypted, cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new InternalServerException(InternalServerExceptionType.ENCRYPTION_ERROR, e);
        }
    }

    @Override
    public byte[] encrypt(byte[] decrypted) {
        try {
            Cipher cipher = initiateCipherForEncrypt(getSecretKey());
            return cipherProcess(decrypted, cipher);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new InternalServerException(InternalServerExceptionType.ENCRYPTION_ERROR, e);
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

    private byte[] readFileBytesFromPath(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    private byte[] cipherProcess(byte[] bytes, Cipher cipher) {
        try {
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new InternalServerException(InternalServerExceptionType.ENCRYPTION_ERROR, e);
        }
    }

    private void saveToFilePath(byte[] bytes, Path path) {
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }

    }
}
