alter table SourceDatabase
    ADD CONSTRAINT DF_SourceDatabase_is_signature_enabled_0 default 0 for is_signature_enabled
GO
