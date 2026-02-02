package com.scnsoft.simplyconnect.filedecrypt.service;

import javax.crypto.SecretKey;

public interface EncryptionKeyService {
    SecretKey find();
}
