package com.scnsoft.eldermark.service;

import javax.crypto.SecretKey;

public interface EncryptionKeyService {
    Long create();
    SecretKey find();
}
