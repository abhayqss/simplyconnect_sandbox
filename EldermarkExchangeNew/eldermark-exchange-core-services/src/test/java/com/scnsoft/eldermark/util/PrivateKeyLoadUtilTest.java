package com.scnsoft.eldermark.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateKeyLoadUtilTest {

    //private keys generated specifically for this test

    @Test
    void loadKey_rsaPkcs1PemPrivateKey() throws IOException {
        test("src/test/resources/key/rsa-pkcs1-pem.key");
    }

    @Test
    void loadKey_rsaPkcs8PemPrivateKey() throws IOException {
        test("src/test/resources/key/rsa-pkcs8-pem.key");
    }

    @Test
    void loadKey_rsaPkcs8DerPrivateKey() throws IOException {
        test("src/test/resources/key/rsa-pkcs8-der.key");
    }

    private void test(String file) throws IOException {
        var key = PrivateKeyLoadUtil.loadKeyFromFile(file);
        assertThat(key).isNotNull();
    }
}