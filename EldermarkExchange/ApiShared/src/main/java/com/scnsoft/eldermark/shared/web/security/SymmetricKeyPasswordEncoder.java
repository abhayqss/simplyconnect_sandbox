package com.scnsoft.eldermark.shared.web.security;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.Key;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 2/13/2017.
 */
@Component
public class SymmetricKeyPasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "AES";
    private final Key key;

    private final Resource passwordKey = new ClassPathResource("/pass.key");

    public SymmetricKeyPasswordEncoder() {
        //Initialize password encoder
        try (InputStream is = passwordKey.getInputStream()) {
            byte[] b = new byte[128/Byte.SIZE];
            is.read(b);
            key = new SecretKeySpec(b, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SymmetricKeyPasswordEncoder(byte[] secKey) {
        key = new SecretKeySpec(secKey, ALGORITHM);

    }

    @Override
    public String encode(CharSequence value) {
        String encryptedVal = null;

        try {
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            final byte[] encValue = c.doFinal(value.toString().getBytes());
            encryptedVal = Base64.encodeBase64String(encValue);
        } catch(Exception ex) {
            System.out.println("The Exception is=" + ex);
            throw new RuntimeException("Invalid decryption");
        }

        return encryptedVal;
    }

    public String decode(final String encryptedValue) {

        String decryptedValue = null;
        try {
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            final byte[] decorVal = Base64.decodeBase64(encryptedValue);
            final byte[] decValue = c.doFinal(decorVal);
            decryptedValue = new String(decValue);
        } catch(Exception ex) {
            System.out.println("The Exception is=" + ex);
            throw new RuntimeException("Invalid decryption");
        }

        return decryptedValue;
    }

    @Override
    public boolean matches(CharSequence raw, String encoded) {
        return decode(encoded).equals(raw);
    }
}
