declare @db_alt_id varchar(255)
set @db_alt_id = 'LSS Nor Ca'

declare @default_updated_by_login varchar(256)
set @default_updated_by_login = 'MGonzalez@lssnorcal.org'

if exists(select 1
          from SourceDatabase
          where alternative_id = @db_alt_id)
    begin
        declare @db_id bigint
        select @db_id = id from SourceDatabase where alternative_id = @db_alt_id

        --1. set community policies as opt in so that one time update works properly. Will be changed back manuallt after release
        update OrganizationHieConsentPolicy
        set type = 'OPT_IN'
        where organization_id in (select id from Organization where database_id = @db_id)

        --2. set clients are opt in
        OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

        declare @default_updated_by_employee_id bigint
        select @default_updated_by_employee_id = id from employee where login = @default_updated_by_login and database_id = @db_id

        update resident
        set hie_consent_policy_update_datetime        = iif(intake_date is not null, intake_date, date_created),
            hie_consent_policy_obtained_from          = CONCAT_WS(' ', first_name, last_name),
            hie_consent_policy_source                 = 'WEB',
            hie_consent_policy_obtained_by            = 'CLIENT',
            hie_consent_policy_updated_by_employee_id = iif(created_by_id is not null, created_by_id, @default_updated_by_employee_id),
            hie_consent_policy_type                   = 'OPT_IN'

        where database_id = @db_id

        CLOSE SYMMETRIC KEY SymmetricKey1;
    end