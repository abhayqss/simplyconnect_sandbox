if col_length('HL7MessageLog_enc', 'file_name') is not null
    begin
        alter table HL7MessageLog_enc
            drop column file_name
    end
go

alter table HL7MessageLog_enc
    add file_name varchar(200)
GO

alter table HL7MessageLog_enc alter column source_address varchar(100)
GO

if object_id('HL7MessageLogInsert') is not null
    drop trigger HL7MessageLogInsert
GO

if object_id('HL7MessageLogUpdate') is not null
    drop trigger HL7MessageLogUpdate
GO

if object_id('HL7MessageLog') is not null
    drop view HL7MessageLog
GO


create view HL7MessageLog as
select id,
       CONVERT(VARCHAR(max), DecryptByKey([raw_message])) [raw_message],
       received_datetime,
       channel,
       source_address,
       source_port,
       resolved_integration,
       file_name,
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
     file_name,
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
           file_name,
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
        file_name                 = i.file_name,
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
