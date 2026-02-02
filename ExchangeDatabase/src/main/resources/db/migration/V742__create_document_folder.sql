if (object_id('DocumentFolder_DocumentCategory') is not null)
    drop table DocumentFolder_DocumentCategory
go

if (object_id('DocumentFolderPermission') is not null)
    drop table DocumentFolderPermission
go

if (object_id('DocumentFolderPermissionLevel') is not null)
    drop table DocumentFolderPermissionLevel
go

if (object_id('DocumentFolder') is not null)
    drop table DocumentFolder
go

create table DocumentFolder
(
    id                  bigint identity primary key,
    parent_id           bigint,
    name                varchar(256) not null,
    is_security_enabled bit          not null,
    community_id        bigint       not null,
    constraint UQ_DocumentFolder_Name unique (community_id, parent_id, name),
    constraint FK_DocumentFolder_Community foreign key (community_id) references Organization (id)
)
go

create table DocumentFolderPermissionLevel
(
    id    bigint identity primary key,
    code  varchar(16),
    title varchar(16),
    constraint UQ_DocumentFolderPermissionLevel_Code unique (code)
)
go

insert into DocumentFolderPermissionLevel(code, title)
values ('ADMIN', 'Admin'),
       ('UPLOADER', 'Uploader'),
       ('VIEWER', 'Viewer')
go

create table DocumentFolderPermission
(
    id                  bigint identity primary key,
    folder_id           bigint not null,
    employee_id         bigint not null,
    permission_level_id bigint not null,
    constraint FK_DocumentFolderPermission_Folder foreign key (folder_id) references DocumentFolder (id),
    constraint FK_DocumentFolderPermission_Level foreign key (permission_level_id) references DocumentFolderPermissionLevel (id),
    constraint FK_DocumentFolderPermission_Employee foreign key (employee_id) references Employee_enc (id),
    constraint UQ_DocumentFolderPermission_Folder_Employee unique (folder_id, employee_id)
)
go

create table DocumentFolder_DocumentCategory
(
    folder_id         bigint not null,
    category_chain_id bigint not null,
    constraint PK_DocumentFolder_DocumentCategory primary key (folder_id, category_chain_id),
    constraint FK_DocumentFolder_DocumentCategory_Folder foreign key (folder_id) references DocumentFolder (id),
)
go
