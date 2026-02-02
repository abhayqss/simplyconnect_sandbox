package com.scnsoft.eldermark.api.shared.web.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.api.shared.web.security.SymmetricKeyPasswordEncoder;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.UUID;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 12/27/2016.
 */
public class Token {
    private Long userId;
    private String uuid;

    private static ObjectMapper mapper = new ObjectMapper();


/*
    private static final String PASSWORD_ENCODE_KEY = "kjh@89_dn=!@GSn9";
    private static SymmetricKeyPasswordEncoder passwordEncoder = new SymmetricKeyPasswordEncoder(PASSWORD_ENCODE_KEY);
*/


    public static String base64encode(Token token) {
        return base64encode(token.toJsonString());
    }

    public static String base64encode(String token) {
        return Base64.encodeBase64String(token.getBytes());
    }

    public static String base64decode(String tokenEncoded) {
        return new String(Base64.decodeBase64(tokenEncoded));
    }

    public static Token base64decodeAsToken(String tokenEncoded) {
        String json =  base64decode(tokenEncoded);
        return fromJsonString(json);
    }

    public static String encode(String token, SymmetricKeyPasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(token);
    }

    public static String encode(Token token, SymmetricKeyPasswordEncoder passwordEncoder) {
        return encode(token.toJsonString(), passwordEncoder);
    }

    public static Token decode(String encodedValue, SymmetricKeyPasswordEncoder passwordEncoder) {
        return Token.fromJsonString(passwordEncoder.decode(encodedValue));
    }

    // used by Jackson deserializer
    public Token() {}

    public Token(Long userId, String uuid) {
        this.userId = userId;
        this.uuid = uuid;
    }

    public boolean matches(String encodedValue, SymmetricKeyPasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(this.toJsonString(), encodedValue);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String toJsonString() {
        return toJsonString(this);
    }

    public static String toJsonString(Token token) {
        try {
            return mapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Token fromEncodedJsonString(String encodedJsonString) {
        return Token.fromJsonString(Token.base64decode(encodedJsonString));
    }


    public static Token fromJsonString(String jsonString) {
        try {
            return mapper.readValue(jsonString, Token.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Token generateToken(Long userId) {
        Token token = new Token(userId, UUID.randomUUID().toString());
        return token;
    }
}
