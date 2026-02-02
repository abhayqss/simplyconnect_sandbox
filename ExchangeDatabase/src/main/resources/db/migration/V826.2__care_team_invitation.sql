IF OBJECT_ID('ResidentCareTeamInvitation_enc') IS NOT NULL
    DROP TABLE ResidentCareTeamInvitation_enc
go

create table ResidentCareTeamInvitation_enc
(
    id                        bigint identity
        constraint PK_ResidentCareTeamInvitation primary key,

    created_at                datetime2(7)   not null,

    created_by_employee_id    bigint         not null
        constraint FK_ResidentCareTeamInvitation_Employee_created_by_employee_id references Employee_enc,

    [status]                  varchar(10),

    first_name                varbinary(max) not null,
    last_name                 varbinary(max) not null,
    birth_date                varbinary(max) not null,
    email                     varbinary(max) not null,

    target_employee_id        bigint         not null
        constraint FK_ResidentCareTeamInvitation_Employee_target_employee_id references Employee_enc,

    resident_id               bigint         not null
        constraint FK_ResidentCareTeamInvitation_resident_resident_id references resident_enc,

    family_app_resident_id    bigint
        constraint FK_ResidentCareTeamInvitation_resident_family_app_resident_id references resident_enc,

    token                     varbinary(max),

    is_hidden                 bit            not null
        constraint DF_ResidentCareTeamInvitation_is_hidden default 0,


    accepted_at               datetime2(7),
    declined_at               datetime2(7),
    cancelled_at              datetime2(7),
    expired_at                datetime2(7),

    resent_from_invitation_id bigint
        constraint FK_ResidentCareTeamInvitation_resent_from_invitation_id references ResidentCareTeamMember
)
go

IF OBJECT_ID('ResidentCareTeamInvitation') IS NOT NULL
    DROP VIEW ResidentCareTeamInvitation
go


create view ResidentCareTeamInvitation as
select id,
       created_at,
       created_by_employee_id,
       [status],
       CONVERT(varchar(256), DecryptByKey([first_name]))           [first_name],
       CONVERT(varchar(256), DecryptByKey([last_name]))            [last_name],
       CONVERT(date, CONVERT(varchar, DecryptByKey([birth_date]))) [birth_date],
       CONVERT(varchar(256), DecryptByKey([email]))                [email],
       target_employee_id,
       resident_id,
       family_app_resident_id,
       CONVERT(varchar(255), DecryptByKey([token]))                [token],
       is_hidden,
       accepted_at,
       declined_at,
       cancelled_at,
       expired_at,
       resent_from_invitation_id
from ResidentCareTeamInvitation_enc
GO

create trigger ResidentCareTeamInvitationInsert
    on ResidentCareTeamInvitation
    instead of INSERT
    as
    insert into ResidentCareTeamInvitation_enc (created_at,
                                                created_by_employee_id,
                                                [status],
                                                [first_name],
                                                [last_name],
                                                [birth_date],
                                                [email],
                                                target_employee_id,
                                                resident_id,
                                                family_app_resident_id,
                                                [token],
                                                is_hidden,
                                                accepted_at,
                                                declined_at,
                                                cancelled_at,
                                                expired_at,
                                                resent_from_invitation_id)
    select created_at,
           created_by_employee_id,
           [status],
           EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])                 first_name,
           EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                  last_name,
           EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, birth_date)) birth_date,
           EncryptByKey(Key_GUID('SymmetricKey1'), [email])                      email,
           target_employee_id,
           resident_id,
           family_app_resident_id,
           EncryptByKey(Key_GUID('SymmetricKey1'), [token])                      token,
           is_hidden,
           accepted_at,
           declined_at,
           cancelled_at,
           expired_at,
           resent_from_invitation_id
    FROM inserted
    SELECT @@IDENTITY;
GO

create trigger ResidentCareTeamInvitationUpdate
    on ResidentCareTeamInvitation
    instead of UPDATE
    as
    update ResidentCareTeamInvitation_enc
    set created_at                = i.created_at,
        created_by_employee_id    = i.created_by_employee_id,
        [status]                  = i.[status],
        [first_name]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
        [last_name]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
        [birth_date]              = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date])),
        [email]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[email]),
        target_employee_id        = i.[target_employee_id],
        resident_id               = i.[resident_id],
        family_app_resident_id    = i.[family_app_resident_id],
        [token]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[token]),
        is_hidden                 = i.[is_hidden],
        accepted_at               = i.[accepted_at],
        declined_at               = i.[declined_at],
        cancelled_at              = i.[cancelled_at],
        expired_at                = i.[expired_at],
        resent_from_invitation_id = i.[resent_from_invitation_id]
    FROM inserted i
    WHERE ResidentCareTeamInvitation_enc.id = i.id;
GO
