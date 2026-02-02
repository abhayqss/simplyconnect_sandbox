if object_id('ResidentInsuranceAuthorization') is not null
    drop view ResidentInsuranceAuthorization
go

if object_id('ResidentInsuranceAuthorization_enc') is not null
    drop table ResidentInsuranceAuthorization_enc
go

create table ResidentInsuranceAuthorization_enc
(
    id            bigint identity (1, 1) not null,
    constraint PK_ResidentInsuranceAuthorization primary key (id),
    start_date    datetime2(7)           not null,
    end_date      datetime2(7)           not null,
    number        varbinary(max)         not null,
    resident_id   bigint                 not null,
    constraint FK_ResidentInsuranceAuthorization_Resident foreign key (resident_id) references resident_enc (id),
    created_date  datetime2(7)           not null,
    created_by_id bigint                 not null,
    constraint FK_ResidentInsuranceAuthorization_CreatedById foreign key (created_by_id) references Employee_enc (id),
)
go

create view ResidentInsuranceAuthorization as
select id,
       start_date,
       end_date,
       convert(varchar(128), DecryptByKey(number)) number,
       resident_id,
       created_date,
       created_by_id
from ResidentInsuranceAuthorization_enc
go

create trigger ResidentInsuranceAuthorizationInsert
    on ResidentInsuranceAuthorization
    instead of insert
    as
begin
    insert into ResidentInsuranceAuthorization_enc(start_date,
                                                   end_date,
                                                   number,
                                                   resident_id,
                                                   created_date,
                                                   created_by_id)
    select start_date,
           end_date,
           EncryptByKey(Key_GUID('SymmetricKey1'), number) number,
           resident_id,
           created_date,
           created_by_id
    from inserted select @@IDENTITY
end
go

create trigger ResidentInsuranceAuthorizationUpdate
    on ResidentInsuranceAuthorization
    instead of update
    as
begin
    update ResidentInsuranceAuthorization_enc
    set start_date    = i.start_date,
        end_date      = i.end_date,
        number        = EncryptByKey(Key_GUID('SymmetricKey1'), i.number),
        resident_id   = i.resident_id,
        created_date  = i.created_date,
        created_by_id = i.created_by_id
    from inserted i
    where i.id = ResidentInsuranceAuthorization_enc.id
end
go

create trigger ResidentInsuranceAuthorizationDelete
    on ResidentInsuranceAuthorization
    instead of delete
    as
begin
    delete from ResidentInsuranceAuthorization_enc
    where ResidentInsuranceAuthorization_enc.id in (select d.id from deleted d)
end
go
