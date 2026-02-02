create table DocumentSignatureTemplate
(
    [id]             bigint       not null identity,
    constraint PK_DocumentSignatureTemplate primary key ([id]),
    [name]           varchar(255) not null,
    [title]          varchar(255) not null,
    [form_ui_schema] varchar(max),
    [form_schema]    varchar(max)
)
go

create table DocumentSignatureTemplate_Organization
(
    signature_template_id bigint
        constraint FK_DocumentSignatureTemplate_Organization_signature_template_id
            references DocumentSignatureTemplate,

    organization_id       bigint
        constraint FK_DocumentSignatureTemplate_Organization_organization_id
            references Organization,

    constraint PK_DocumentSignatureTemplate_Organization primary key
        (organization_id, signature_template_id)
)
go


create table DocumentSignatureTemplate_SourceDatabase
(
    signature_template_id bigint
        constraint FK_DocumentSignatureTemplate_SourceDatabase_signature_template_id
            references DocumentSignatureTemplate,
    database_id           bigint
        constraint FK_DocumentSignatureTemplate_SourceDatabase_database_id
            references SourceDatabase,

    constraint PK_DocumentSignatureTemplate_SourceDatabase primary key
        (database_id, signature_template_id)
)
go
