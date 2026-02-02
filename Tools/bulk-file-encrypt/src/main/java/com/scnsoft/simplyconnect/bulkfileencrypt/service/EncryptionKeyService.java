package com.scnsoft.simplyconnect.bulkfileencrypt.service;

import javax.crypto.SecretKey;

public interface EncryptionKeyService {
    SecretKey find();
}
