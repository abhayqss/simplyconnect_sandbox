if col_length('resident_enc', 'avatar_id') is not null
    begin
        alter table resident_enc
            drop constraint FK_Resident_Avatar_id;
        alter table resident_enc
            drop column avatar_id;
    end
go

alter table resident_enc
    add avatar_id bigint null
go

alter table resident_enc
    add constraint FK_Resident_Avatar_id foreign key (avatar_id) references Avatar (id)

update r
set r.avatar_id = a.id
from resident_enc r
         inner join Avatar a on r.id = a.resident_id
go

exec resident_table_modified

if col_length('Employee_enc', 'avatar_id') is not null
    begin
        alter table Employee_enc
            drop constraint FK_Employee_Avatar_id;
        alter table Employee_enc
            drop column avatar_id;
    end
go

alter table Employee_enc
    add avatar_id bigint null
go

alter table Employee_enc
    add constraint FK_Employee_Avatar_id foreign key (avatar_id) references Avatar (id)

update e
set e.avatar_id = a.id
from Employee_enc e
         inner join Avatar a on e.id = a.employee_id
go

if object_id('EmployeeBasic') is not null
    drop view EmployeeBasic
go

CREATE VIEW [dbo].[EmployeeBasic]
AS
SELECT [id],
       [inactive],
       [legacy_id],
       [database_id],
       [person_id],
       [care_team_role_id],
       [ccn_community_id],
       CONVERT(NVARCHAR(256), DecryptByKey([first_name]))  [first_name],
       CONVERT(NVARCHAR(256), DecryptByKey([last_name]))   [last_name],
       CONVERT(NVARCHAR(256), DecryptByKey([login]))       [login],
       [creator_id],
       [last_session_datetime],
       [avatar_id]
FROM Employee_enc
GO

exec update_employee_view

if col_length('Prospect_enc', 'avatar_id') is not null
    begin
        alter table Prospect_enc
            drop constraint FK_Prospect_enc_Avatar_id;
        alter table Prospect_enc
            drop column avatar_id;
    end
go

alter table Prospect_enc
    add avatar_id bigint null
go

alter table Prospect_enc
    add constraint FK_Prospect_enc_Avatar_id foreign key (avatar_id) references Avatar (id)

update p
set p.avatar_id = a.id
from Prospect_enc p
         inner join Avatar a on p.id = a.prospect_id
go

IF (OBJECT_ID('Prospect') IS NOT NULL)
    DROP VIEW [dbo].[Prospect]
GO

CREATE VIEW [dbo].[Prospect]
AS
SELECT [id]
     , CONVERT(VARCHAR(256), DecryptByKey([first_name]))               [first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([last_name]))                [last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([middle_name]))              [middle_name]
     , [database_id]
     , [person_id]
     , [in_network_insurance_id]
     , CONVERT(VARCHAR(256), DecryptByKey([insurance_plan]))           [insurance_plan]
     , [gender_id]
     , CONVERT(varchar(11), DecryptByKey([ssn]))                       [ssn]
     , [marital_status_id]
     , [race_id]
     , CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0)  [birth_date]
     , [veteran]
     , [move_in_date]
     , [rental_agreement_date]
     , [assessment_date]
     , CONVERT(VARCHAR(256), DecryptByKey([referral_source]))          [referral_source]
     , CONVERT(VARCHAR(256), DecryptByKey([notes]))                    [notes]
     , [second_occupant_id]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_first_name])) [related_party_first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_last_name]))  [related_party_last_name]
     , [related_party_person_id]
     , [related_party_relationship]
     , [organization_id]
     , [external_id]
     , [prospect_status]
     , [created_date]
     , [last_modified_date]
     , [primary_contact_id]
     , [active]
     , [activation_date]
     , [deactivation_date]
     , [activation_comment]
     , [deactivation_comment]
     , [deactivation_reason]
     , [created_by_id]
     , [updated_by_id]
     , [avatar_id]
FROM [dbo].[Prospect_enc]
GO

CREATE TRIGGER [dbo].[ProspectInsert]
    ON [dbo].[Prospect]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[Prospect_enc]
    ( [first_name]
    , [last_name]
    , [middle_name]
    , [database_id]
    , [person_id]
    , [in_network_insurance_id]
    , [insurance_plan]
    , [gender_id]
    , [ssn]
    , [marital_status_id]
    , [race_id]
    , [birth_date]
    , [veteran]
    , [move_in_date]
    , [rental_agreement_date]
    , [assessment_date]
    , [referral_source]
    , [notes]
    , [second_occupant_id]
    , [related_party_first_name]
    , [related_party_last_name]
    , [related_party_person_id]
    , [related_party_relationship]
    , [organization_id]
    , [external_id]
    , [prospect_status]
    , [created_date]
    , [last_modified_date]
    , [primary_contact_id]
    , [active]
    , [activation_date]
    , [deactivation_date]
    , [activation_comment]
    , [deactivation_comment]
    , [deactivation_reason]
    , [created_by_id]
    , [updated_by_id]
    , [avatar_id])
    SELECT EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])               [first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                [last_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])              [middle_name]
         , [database_id]
         , [person_id]
         , [in_network_insurance_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [insurance_plan])           [insurance_plan]
         , [gender_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])                      [ssn]
         , [marital_status_id]
         , [race_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [birth_date], 0))
         , [veteran]
         , [move_in_date]
         , [rental_agreement_date]
         , [assessment_date]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [referral_source])          [referral_source]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [notes])                    [notes]
         , [second_occupant_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_first_name]) [related_party_first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_last_name])  [related_party_last_name]
         , [related_party_person_id]
         , [related_party_relationship]
         , [organization_id]
         , [external_id]
         , [prospect_status]
         , GETDATE()
         , GETDATE()
         , [primary_contact_id]
         , [active]
         , [activation_date]
         , [deactivation_date]
         , [activation_comment]
         , [deactivation_comment]
         , [deactivation_reason]
         , [created_by_id]
         , [updated_by_id]
         , [avatar_id]
    FROM inserted
    SELECT @@IDENTITY;
END
GO

CREATE TRIGGER [dbo].[ProspectUpdate]
    ON [dbo].[Prospect]
    INSTEAD OF
        UPDATE
    AS
BEGIN
    UPDATE Prospect_enc
    SET [first_name]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name])
      , [last_name]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name])
      , [middle_name]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name])
      , [database_id]                = i.[database_id]
      , [person_id]                  = i.[person_id]
      , [in_network_insurance_id]    = i.[in_network_insurance_id]
      , [insurance_plan]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[insurance_plan])
      , [gender_id]                  = i.[gender_id]
      , [ssn]                        = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn])
      , [marital_status_id]          = i.[marital_status_id]
      , [race_id]                    = i.[race_id]
      , [birth_date]                 = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0))
      , [veteran]                    = i.[veteran]
      , [move_in_date]               = i.[move_in_date]
      , [rental_agreement_date]      = i.[rental_agreement_date]
      , [assessment_date]            = i.[assessment_date]
      , [referral_source]            = EncryptByKey(Key_GUID('SymmetricKey1'), i.[referral_source])
      , [notes]                      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[notes])
      , [second_occupant_id]         = i.[second_occupant_id]
      , [related_party_first_name]   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_first_name])
      , [related_party_last_name]    = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_first_name])
      , [related_party_person_id]    = i.[related_party_person_id]
      , [related_party_relationship] = i.[related_party_relationship]
      , [organization_id]            = i.[organization_id]
      , [external_id]                = i.[external_id]
      , [prospect_status]            = i.[prospect_status]
      , [last_modified_date]         = GETDATE()
      , [primary_contact_id]         = i.[primary_contact_id]
      , [active]                     = i.[active]
      , [deactivation_date]          = i.[deactivation_date]
      , [activation_date]            = i.[activation_date]
      , [deactivation_reason]        = i.[deactivation_reason]
      , [activation_comment]         = i.[activation_comment]
      , [deactivation_comment]       = i.[deactivation_comment]
      , [created_by_id]              = i.[created_by_id]
      , [updated_by_id]              = i.[updated_by_id]
      , [avatar_id]                  = i.[avatar_id]
    FROM inserted i
    WHERE Prospect_enc.id = i.id;
END
GO

if col_length('SecondOccupant_enc', 'avatar_id') is not null
    begin
        alter table SecondOccupant_enc
            drop constraint FK_SecondOccupant_enc_Avatar_id;
        alter table SecondOccupant_enc
            drop column avatar_id;
    end
go

alter table SecondOccupant_enc
    add avatar_id bigint null
go

alter table SecondOccupant_enc
    add constraint FK_SecondOccupant_enc_Avatar_id foreign key (avatar_id) references Avatar (id)

update so
set so.avatar_id = a.id
from SecondOccupant_enc so
         inner join Avatar a on so.id = a.second_occupant_id
go

IF (OBJECT_ID('SecondOccupant') IS NOT NULL)
    DROP VIEW [dbo].[SecondOccupant]
GO

CREATE VIEW [dbo].[SecondOccupant]
AS
SELECT [id]
     , CONVERT(VARCHAR(256), DecryptByKey([first_name]))              [first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([last_name]))               [last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([middle_name]))             [middle_name]
     , [person_id]
     , [in_network_insurance_id]
     , CONVERT(VARCHAR(256), DecryptByKey([insurance_plan]))          [insurance_plan]
     , [gender_id]
     , CONVERT(varchar(11), DecryptByKey([ssn]))                      [ssn]
     , [marital_status_id]
     , [race_id]
     , [database_id]
     , CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0) [birth_date]
     , [veteran]
     , [status]
     , [avatar_id]
FROM [dbo].[SecondOccupant_enc]
GO

CREATE TRIGGER [dbo].[SecondOccupantInsert]
    ON [dbo].[SecondOccupant]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[SecondOccupant_enc]
    ( [first_name]
    , [last_name]
    , [middle_name]
    , [person_id]
    , [in_network_insurance_id]
    , [insurance_plan]
    , [gender_id]
    , [ssn]
    , [marital_status_id]
    , [race_id]
    , [database_id]
    , [birth_date]
    , [veteran]
    , [status]
    , [avatar_id])
    SELECT EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])     [first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])      [last_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])    [middle_name]
         , [person_id]
         , [in_network_insurance_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [insurance_plan]) [insurance_plan]
         , [gender_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])            [ssn]
         , [marital_status_id]
         , [race_id]
         , [database_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [birth_date], 0))
         , [veteran]
         , [status]
         , [avatar_id]
    FROM inserted
    SELECT @@IDENTITY;
END
GO

CREATE TRIGGER [dbo].[SecondOccupantUpdate]
    ON [dbo].[SecondOccupant]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE SecondOccupant_enc
    SET [first_name]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name])
      , [last_name]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name])
      , [middle_name]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name])
      , [person_id]               = i.[person_id]
      , [in_network_insurance_id] = i.[in_network_insurance_id]
      , [insurance_plan]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.[insurance_plan])
      , [gender_id]               = i.[gender_id]
      , [ssn]                     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn])
      , [marital_status_id]       = i.[marital_status_id]
      , [race_id]                 = i.[race_id]
      , [database_id]             = i.[database_id]
      , [birth_date]              = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0))
      , [veteran]                 = i.[veteran]
      , [status]                  = i.[status]
      , [avatar_id]               = i.[avatar_id]
    FROM inserted i
    WHERE SecondOccupant_enc.id = i.id;
END
GO

if col_length('Avatar', 'second_occupant_id') is not null
    begin
        alter table Avatar
            drop constraint FK_Avatar_SecondOccupant_enc_id;
        alter table Avatar
            drop column second_occupant_id;
    end
go

if col_length('Avatar', 'prospect_id') is not null
    begin
        alter table Avatar
            drop constraint FK_Avatar_Prospect_enc_id;
        alter table Avatar
            drop column prospect_id;
    end
go

if col_length('Avatar', 'resident_id') is not null
    begin
        alter table Avatar
            drop constraint FK_Resident_ResidentAvatar_id;
        alter table Avatar
            drop column resident_id;
    end
go

if col_length('Avatar', 'employee_id') is not null
    begin
        alter table Avatar
            drop constraint FK_Employee_ResidentAvatar_id;
        alter table Avatar
            drop column employee_id;
    end
go
