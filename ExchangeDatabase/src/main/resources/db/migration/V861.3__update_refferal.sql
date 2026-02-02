IF COL_LENGTH('ServiceType', 'is_business_related') IS NOT NULL
    BEGIN
        alter table ServiceType
            drop column is_business_related;
    END
GO

alter table ServiceType
    add is_business_related bit null
go

update ServiceType
set is_business_related = 1
go

alter table ServiceType
    alter column is_business_related bit not null
go

IF COL_LENGTH('Referral', 'requesting_organization_id') IS NOT NULL
    BEGIN
        alter table Referral
            drop column requesting_organization_id;
    END
GO

alter table Referral
    add requesting_organization_id bigint null
go

update ref
set ref.requesting_organization_id = res.facility_id
from Referral ref
         left join resident_enc res on ref.resident_id = res.id

alter table Referral
    alter column requesting_organization_id bigint not null
go

alter table Referral
    alter column resident_id bigint
go

IF (OBJECT_ID('Referral_ServiceType') IS NOT NULL)
    DROP TABLE Referral_ServiceType
GO

create table Referral_ServiceType
(
    referral_id     bigint not null,
    service_type_id bigint not null,
    constraint PK_Referral_ServiceType primary key (referral_id, service_type_id),
    constraint FK_Referral_ServiceType_referral_id foreign key (referral_id) references Referral (id),
    constraint FK_Referral_ServiceType_service_type_id foreign key (service_type_id) references ServiceType (id),
)
