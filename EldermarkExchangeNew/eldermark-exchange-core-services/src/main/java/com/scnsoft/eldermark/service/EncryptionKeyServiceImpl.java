package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.EncryptionKeyDao;
import com.scnsoft.eldermark.entity.EncryptionKey;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Transactional
public class EncryptionKeyServiceImpl implements EncryptionKeyService {

    @Autowired
    private EncryptionKeyDao encryptionKeyDao;

    @Override
    public Long create() {
        var keyCount = encryptionKeyDao.count();
        if (keyCount > 0L) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_ENCRYPTION_KEY_ALREADY_EXISTS);
        }
        var keyBytes = generateAesKeyBytes();
        EncryptionKey entity = new EncryptionKey();
        entity.setSecretKey(keyBytes);
        return encryptionKeyDao.save(entity).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public SecretKey find() {
        List<EncryptionKey> keyList = encryptionKeyDao.findAll();
        if (CollectionUtils.isEmpty(keyList)) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_NO_ENCRYPTION_KEY);
        }
        if (keyList.size() > 1) {
            throw new BusinessException(BusinessExceptionType.DOCUMENT_MULTIPLE_ENCRYPTION_KEYS);
        }
        var data =  keyList.get(0).getSecretKey();
        return new SecretKeySpec(data, 0, data.length, "AES");
    }

    private byte[] generateAesKeyBytes() {
        try {
            var secretKey = KeyGenerator.getInstance("AES").generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerException(InternalServerExceptionType.ENCRYPTION_KEY_GENERATION_ERROR);
        }
    }
}
