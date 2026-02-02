if col_length('DocumentFolder', 'temporary_deleted_by_id"') is not null
    begin
        alter table DocumentFolder
            drop constraint FK_DocumentFolder_TemporaryDeletedBy
        alter table DocumentFolder
            drop column temporary_deleted_by_id
    end
go


if col_length('DocumentFolder', 'deleted_by_id"') is not null
    begin
        alter table DocumentFolder
            drop constraint FK_DocumentFolder_DeletedBy
        alter table DocumentFolder
            drop column deleted_by_id
    end
go

if col_length('DocumentFolder', 'deletion_time') is not null
    begin
        alter table DocumentFolder
            drop column deletion_time
    end
go

if col_length('DocumentFolder', 'restored_by_id"') is not null
    begin
        alter table DocumentFolder
            drop constraint FK_DocumentFolder_RestoredBy
        alter table DocumentFolder
            drop column restored_by_id
    end
go

if col_length('DocumentFolder', 'restoration_time') is not null
    begin
        alter table DocumentFolder
            drop column restoration_time
    end
go


alter table DocumentFolder
    add temporary_deleted_by_id bigint null
go

alter table DocumentFolder
    add constraint FK_DocumentFolder_TemporaryDeletedBy foreign key (temporary_deleted_by_id) references Employee_enc (id)
go

alter table DocumentFolder
    add deleted_by_id bigint null
go

alter table DocumentFolder
    add constraint FK_DocumentFolder_DeletedBy foreign key (deleted_by_id) references Employee_enc (id)
go

alter table DocumentFolder
    add deletion_time datetime2(7) null
go

alter table DocumentFolder
    add restored_by_id bigint null
go

alter table DocumentFolder
    add constraint FK_DocumentFolder_RestoredBy foreign key (restored_by_id) references Employee_enc (id)
go

alter table DocumentFolder
    add restoration_time datetime2(7) null
go
