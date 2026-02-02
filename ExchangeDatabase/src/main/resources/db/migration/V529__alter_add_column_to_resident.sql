ALTER TABLE resident_enc ADD risk_score varchar(512) DEFAULT NULL
exec update_resident_view @toEncrypt = default