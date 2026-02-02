package com.scnsoft.simplyconnect.filedecrypt.service;

import com.scnsoft.simplyconnect.filedecrypt.dao.EncryptionKeyDao;
import com.scnsoft.simplyconnect.filedecrypt.entity.EncryptionKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EncryptionKeyServiceImpl implements EncryptionKeyService {

    @Autowired
    private EncryptionKeyDao encryptionKeyDao;

    @Autowired
    private SymmetricKeySqlServerService symmetricKeySqlServerService;

    @Override
    public SecretKey find() {
        symmetricKeySqlServerService.open();
        List<EncryptionKey> keyList = encryptionKeyDao.findAll();
        if (CollectionUtils.isEmpty(keyList)) {
            throw new RuntimeException("No encryption key found");
        }
        if (keyList.size() > 1) {
            throw new RuntimeException("Multiple encryption keys found");
        }
        var data =  keyList.get(0).getSecretKey();
        return new SecretKeySpec(data, 0, data.length, "AES");
    }

}
