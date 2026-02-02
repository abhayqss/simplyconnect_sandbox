IF COL_LENGTH('Event_enc', 'pcc_sc_adt_record_id') IS NOT NULL
    BEGIN
        alter table Event_enc
            drop constraint FK_Event_PccAdtRecord_pcc_sc_adt_record_id;
        alter table Event_enc
            drop column pcc_sc_adt_record_id;
    END
GO

if OBJECT_ID('PccAdtRecord') is not null
    drop table PccAdtRecord
GO

create table PccAdtRecord
(
    id                          bigint identity
        constraint PK_PccAdtRecord primary key,

    saved_at                    datetime2(7) not null,

    --pcc fields
    accessing_entity_id         varchar(100),
    action_code                 varchar(10),
    action_type                 varchar(200),
    additional_bed_desc         varchar(500),
    additional_bed_id           bigint,
    additional_floor_desc       varchar(500),
    additional_floor_id         bigint,
    additional_room_desc        varchar(500),
    additional_room_id          bigint,
    additional_unit_desc        varchar(500),
    additional_unit_id          bigint,
    admission_source            varchar(500),
    admission_source_code       varchar(100),
    admission_type              varchar(200),
    admission_type_code         varchar(100),
    adt_record_id               bigint,
    bed_desc                    varchar(500),
    bed_id                      bigint,
    destination                 varchar(500),
    destination_type            varchar(100),
    discharge_status            varchar(200),
    discharge_status_code       varchar(100),
    effective_date_time         datetime2(3),
    entered_by                  varchar(200),
    entered_by_position_id      bigint,
    entered_date                datetime2(3),
    floor_desc                  varchar(500),
    floor_id                    bigint,
    modified_date_time          datetime2(3),
    origin                      varchar(500),
    origin_type                 varchar(200),
    outpatient                  bit,
    outpatient_status           varchar(20),
    patient_id                  bigint,
    payer_code                  varchar(5),
    payer_name                  varchar(100),
    payer_type                  varchar(100),
    qhs_waiver                  bit,
    room_desc                   varchar(500),
    room_id                     bigint,
    skilled_care                bit,
    skilled_effective_from_date varchar(15),
    skilled_effective_to_date   varchar(15),
    standard_action_type        varchar(20),
    stop_billing_date           datetime2(3),
    unit_desc                   varchar(500),
    unit_id                     bigint,
    is_cancelled_record         bit
)
GO

alter table Event_enc
    add pcc_sc_adt_record_id bigint
        constraint FK_Event_PccAdtRecord_pcc_sc_adt_record_id foreign key references PccAdtRecord
GO

if OBJECT_ID('Event') is not null
    drop view Event
GO

create view [dbo].[Event]
as
select [id],
       [resident_id],
       [event_type_id],
       [event_datetime],
       [is_injury],
       [location],
       [background],
       [is_followup],
       [is_manual],
       [event_manager_id],
       [event_author_id],
       [event_rn_id],
       [event_treating_physician_id],
       [event_treating_hospital_id],
       [is_er_visit],
       [is_overnight_in],
       [organization],
       [community],
       [adt_msg_id],
       [auxiliary_info],
       [device_id],
       [person_responsible],
       [entered_by],
       [death_indicator],
       [death_date],
       [map_document_id],
       [pcc_sc_adt_record_id],
       CONVERT(varchar(MAX), DecryptByKey([event_content])) event_content,
       CONVERT(varchar(MAX), DecryptByKey([situation]))     situation,
       CONVERT(varchar(MAX), DecryptByKey([assessment]))    assessment,
       CONVERT(varchar(MAX), DecryptByKey([followup]))      followup
from Event_enc
GO

IF OBJECT_ID('EventInsert') IS NOT NULL
    DROP TRIGGER EventInsert;
GO

CREATE TRIGGER [dbo].[EventInsert]
    on [dbo].[Event]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO Event_enc
    ([resident_id], [event_type_id], [event_datetime], [is_injury], [location], [background], [is_followup],
     [is_manual],
     [event_manager_id], [event_author_id], [event_rn_id], [event_treating_physician_id], [event_treating_hospital_id],
     [is_er_visit], [is_overnight_in], [event_content], [situation], [assessment], [followup], [organization],
     [community],
     [adt_msg_id], [auxiliary_info], [device_id], [person_responsible], [entered_by], [death_indicator], [death_date],
     [map_document_id], [pcc_sc_adt_record_id])
    SELECT [resident_id],
           [event_type_id],
           [event_datetime],
           ISNULL([is_injury], 0),
           [location],
           [background],
           ISNULL([is_followup], 0),
           ISNULL([is_manual], 0),
           [event_manager_id],
           [event_author_id],
           [event_rn_id],
           [event_treating_physician_id],
           [event_treating_hospital_id],
           ISNULL([is_er_visit], 0),
           ISNULL([is_overnight_in], 0),

           EncryptByKey(Key_GUID('SymmetricKey1'), [event_content]) event_content,
           EncryptByKey(Key_GUID('SymmetricKey1'), [situation])     situation,
           EncryptByKey(Key_GUID('SymmetricKey1'), [assessment])    assessment,
           EncryptByKey(Key_GUID('SymmetricKey1'), [followup])      followup,
           [organization],
           [community],
           [adt_msg_id],
           [auxiliary_info],
           [device_id],
           [person_responsible],
           [entered_by],
           [death_indicator],
           [death_date],
           [map_document_id],
           [pcc_sc_adt_record_id]
    FROM inserted
    SELECT @@IDENTITY
END
GO

IF OBJECT_ID('EventUpdate') IS NOT NULL
    DROP TRIGGER EventUpdate;
GO

CREATE TRIGGER [dbo].[EventUpdate]
    on [dbo].[Event]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE Event_enc
    SET [resident_id]                 = i.[resident_id],
        [event_type_id]               = i.[event_type_id],
        [event_datetime]              = i.[event_datetime],
        [is_injury]                   = ISNULL(i.[is_injury], 0),
        [location]                    = i.[location],
        [background]                  = i.[background],
        [is_followup]                 = ISNULL(i.[is_followup], 0),
        [is_manual]                   = ISNULL(i.[is_manual], 0),
        [event_manager_id]            = i.[event_manager_id],
        [event_author_id]             = i.[event_author_id],
        [event_rn_id]                 = i.[event_rn_id],
        [event_treating_physician_id] = i.[event_treating_physician_id],
        [event_treating_hospital_id]  = i.[event_treating_hospital_id],
        [is_er_visit]                 = ISNULL(i.[is_er_visit], 0),
        [is_overnight_in]             = ISNULL(i.[is_overnight_in], 0),
        [event_content]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.event_content),
        [situation]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.situation),
        [assessment]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.assessment),
        [followup]                    = EncryptByKey(Key_GUID('SymmetricKey1'), i.followup),
        [organization]                = i.[organization],
        [community]                   = i.[community],
        [adt_msg_id]                  = i.[adt_msg_id],
        [auxiliary_info]              = i.[auxiliary_info],
        [device_id]                   = i.[device_id],
        [person_responsible]          = i.[person_responsible],
        [entered_by]                  = i.[entered_by],
        [death_indicator]             = i.[death_indicator],
        [death_date]                  = i.[death_date],
        [map_document_id]             = i.[map_document_id],
        [pcc_sc_adt_record_id]        = i.[pcc_sc_adt_record_id]
    FROM inserted i
    WHERE Event_enc.id = i.id
END
GO
