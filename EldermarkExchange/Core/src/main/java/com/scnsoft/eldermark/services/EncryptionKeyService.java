package com.scnsoft.eldermark.services;

import javax.crypto.SecretKey;

public interface EncryptionKeyService {
    SecretKey find();
}
