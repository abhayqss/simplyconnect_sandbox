package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.EncryptionKeyDao;
import com.scnsoft.eldermark.entity.EncryptionKey;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Service
@Transactional
public class EncryptionKeyServiceImpl implements EncryptionKeyService {

    @Autowired
    private EncryptionKeyDao encryptionKeyDao;

    @Override
    @Transactional(readOnly = true)
    public SecretKey find() {
        List<EncryptionKey> keyList = encryptionKeyDao.findAll();
        if (CollectionUtils.isEmpty(keyList)) {
            throw new BusinessException("No encryption key found.");
        }
        if (keyList.size() > 1) {
            throw new BusinessException("Multiple encryption keys found.");
        }
        byte[] data =  keyList.get(0).getSecretKey();
        return new SecretKeySpec(data, 0, data.length, "AES");
    }

}
