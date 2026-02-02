package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@MappedSuperclass
public class BasePhrExternalAuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expiration_time")
    private Instant expirationTime;

    @Column(name="token_encoded", unique = true, nullable = false)
    private String tokenEncoded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getTokenEncoded() {
        return tokenEncoded;
    }

    public void setTokenEncoded(String tokenEncoded) {
        this.tokenEncoded = tokenEncoded;
    }
}
