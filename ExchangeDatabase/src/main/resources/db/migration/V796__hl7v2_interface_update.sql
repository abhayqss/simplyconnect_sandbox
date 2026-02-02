if object_id('HL7MessageLogInsert') is not null
    drop trigger HL7MessageLogInsert
GO

if object_id('HL7MessageLogUpdate') is not null
    drop trigger HL7MessageLogUpdate
GO

if object_id('HL7MessageLog') is not null
    drop view HL7MessageLog
GO

if object_id('HL7MessageLog_enc') is not null
    drop table HL7MessageLog_enc
GO

create table HL7MessageLog_enc
(
    id                        bigint       not null identity,
    constraint PK_HL7MessageLog primary key (id),

    raw_message               varbinary(max) not null,
    received_datetime         datetime2    not null,
    channel                   varchar(10)  not null,
    source_address            varchar(45),
    source_port               int,
    resolved_integration      varchar(30),
    success                   bit          not null,
    processed_datetime        datetime2,
    error_message             varchar(max),
    openxds_api_success       bit          not null,
    openxds_api_error_message varchar(max),
    affected_client1_id       bigint,
    constraint FK_HL7MessageLog_resident_affected_client1_id FOREIGN KEY (affected_client1_id)
        references resident_enc (id),

    --some messages assume multiple clients
    affected_client2_id       bigint,
    constraint FK_HL7MessageLog_resident_affected_client2_id FOREIGN KEY (affected_client2_id)
        references resident_enc (id),

    adt_message_id            bigint
)
GO

create view HL7MessageLog as
select id,
       CONVERT(VARCHAR(max), DecryptByKey([raw_message])) [raw_message],
       received_datetime,
       channel,
       source_address,
       source_port,
       resolved_integration,
       success,
       processed_datetime,
       error_message,
       openxds_api_success,
       openxds_api_error_message,
       affected_client1_id,
       affected_client2_id,
       adt_message_id
from HL7MessageLog_enc
GO

create trigger HL7MessageLogInsert
    on HL7MessageLog
    INSTEAD OF INSERT
    AS
BEGIN
    if NOT exists(select 1 FROM sys.openkeys where [key_name] = 'SymmetricKey1')
        begin
            RAISERROR ('SymmetricKey1 is not opened!', 15, 1);
            RETURN;
        end
    INSERT INTO HL7MessageLog_enc
    (raw_message,
     received_datetime,
     channel,
     source_address,
     source_port,
     resolved_integration,
     success,
     processed_datetime,
     error_message,
     openxds_api_success,
     openxds_api_error_message,
     affected_client1_id,
     affected_client2_id,
     adt_message_id)
    select EncryptByKey(Key_GUID('SymmetricKey1'), [raw_message]) [raw_message],
           received_datetime,
           channel,
           source_address,
           source_port,
           resolved_integration,
           success,
           processed_datetime,
           error_message,
           openxds_api_success,
           openxds_api_error_message,
           affected_client1_id,
           affected_client2_id,
           adt_message_id
    FROM inserted
    select @@IDENTITY;
END;
GO

create trigger HL7MessageLogUpdate
    on HL7MessageLog
    INSTEAD OF UPDATE
    AS
BEGIN
    if NOT exists(select 1 FROM sys.openkeys where [key_name] = 'SymmetricKey1')
        begin
            RAISERROR ('SymmetricKey1 is not opened!', 15, 1);
            RETURN;
        end
    update HL7MessageLog_enc
    set raw_message               = EncryptByKey(Key_GUID('SymmetricKey1'), i.raw_message),
        received_datetime         = i.received_datetime,
        channel                   = i.channel,
        source_address            = i.source_address,
        source_port               = i.source_port,
        resolved_integration      = i.resolved_integration,
        success                   = i.success,
        processed_datetime        = i.processed_datetime,
        error_message             = i.error_message,
        openxds_api_success       = i.openxds_api_success,
        openxds_api_error_message = i.openxds_api_error_message,
        affected_client1_id       = i.affected_client1_id,
        affected_client2_id       = i.affected_client2_id,
        adt_message_id            = i.adt_message_id
    FROM inserted i
    where HL7MessageLog_enc.id = i.id;
END;
