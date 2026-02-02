if not exists(select 1
              from OneTimeUpdate
              where update_name = 'update-hie-consent-policy')
    insert into OneTimeUpdate (update_name)
    values ('update-hie-consent-policy')
go

update resident_enc
set hie_consent_policy_type = 'OPT_OUT'
where hie_consent_policy_type is null
go

alter table resident_enc
    alter column hie_consent_policy_type varchar(20) not null
go

alter table resident_enc
    add constraint DF_resident_enc_hie_consent_policy_type default 'OPT_OUT' for hie_consent_policy_type
go
