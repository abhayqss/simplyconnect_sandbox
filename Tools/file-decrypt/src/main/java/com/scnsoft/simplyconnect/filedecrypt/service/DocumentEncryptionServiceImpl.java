package com.scnsoft.simplyconnect.filedecrypt.service;

import com.scnsoft.simplyconnect.filedecrypt.CipherMode;
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
    public void processAllFiles(CipherMode mode, String source, String target) {
        try {
            Cipher cipher = initiateCipher(mode, getSecretKey());
            Path sourcePath = Paths.get(source);
            if (!Files.isDirectory(Paths.get(target))) {
                throw new RuntimeException("Target folder should be folder");
            }
            if (Files.isRegularFile(sourcePath)) {
                readProcessSave(target, cipher, sourcePath);
            } else {
                Files.walk(sourcePath).filter(Files::isRegularFile).forEach(subPath -> {
                    readProcessSave(target, cipher, subPath);
                });
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error while decrypting file");
        } catch (IOException e) {
            throw new RuntimeException("I/O error");
        }
    }

    private void readProcessSave(String target, Cipher cipher, Path sourcePath) {
        var bytes = readFileBytesFromPath(sourcePath);
        var filename = sourcePath.getFileName().toString();
        var decrypted = cipherProcess(bytes, cipher);
        saveToFilePath(decrypted, Paths.get(target, filename));
    }

    private Cipher initiateCipher(CipherMode mode, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (mode == null) {
            throw new RuntimeException("Cipher mode should be ENCRYPT or DECRYPT");
        }
        Cipher cipher = Cipher.getInstance("AES");
        if (mode == CipherMode.ENCRYPT) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
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
            throw new RuntimeException("Error while decrypting file");
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
