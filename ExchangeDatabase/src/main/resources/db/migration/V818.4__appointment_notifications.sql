IF (OBJECT_ID('Event') IS NOT NULL)
    DROP VIEW [dbo].[Event]
GO

IF COL_LENGTH('Event_enc', 'appointment_chain_id') IS NOT NULL
    BEGIN
        alter table Event_enc
            drop column appointment_chain_id;
    END
GO

IF (OBJECT_ID('DeferredAppointmentNotification') IS NOT NULL)
    drop table DeferredAppointmentNotification
GO

IF (OBJECT_ID('ClientAppointmentNotification') IS NOT NULL)
    drop table ClientAppointmentNotification
GO

alter table Event_enc
    add appointment_chain_id bigint
go

CREATE VIEW [dbo].[Event]
AS
SELECT [id],
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
       CONVERT(varchar(MAX), DecryptByKey([event_content])) event_content,
       CONVERT(varchar(MAX), DecryptByKey([situation]))     situation,
       CONVERT(varchar(MAX), DecryptByKey([assessment]))    assessment,
       CONVERT(varchar(MAX), DecryptByKey([followup]))      followup,
       [appointment_chain_id]
from Event_enc
go

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
     [map_document_id], [appointment_chain_id])
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
           [appointment_chain_id]
    FROM inserted
    SELECT @@IDENTITY
END
go

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
        [appointment_chain_id]        = i.[appointment_chain_id]
    FROM inserted i
    WHERE Event_enc.id = i.id
END
go

create table ClientAppointmentNotification
(
    id                  bigint identity (1, 1) not null,
    appointment_id      bigint                 not null,
    notification_method varchar(10)            not null,
    notification_type   varchar(30)            not null,
    created_datetime    datetime2(7)           not null,
    sent_datetime       datetime2(7)           null,
    constraint PK_ClientAppointmentNotification primary key (id),
    constraint FK_ClientAppointmentNotification_Appointment foreign key (appointment_id) references Appointment (id)
)
go

create table DeferredAppointmentNotification
(
    id                bigint identity (1, 1) not null,
    appointment_id    bigint                 not null,
    notification_type varchar(30)            not null,
    dispatch_datetime datetime2(7)           not null,
    constraint PK_DeferredAppointmentNotification primary key (id),
    constraint FK_DeferredAppointmentNotification_Appointment foreign key (appointment_id) references Appointment (id)
)
go
