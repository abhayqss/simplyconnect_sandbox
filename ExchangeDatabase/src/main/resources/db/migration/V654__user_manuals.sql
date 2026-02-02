CREATE TABLE dbo.UserManual
(
    id BIGINT IDENTITY(1,1) NOT NULL,
    constraint PK_UserManual primary key ([id]),
    file_name varchar(512) NOT NULL,
    title varchar(256) NOT NULL,
    mime_type varchar(128) NOT NULL,
    created datetime2(7) NOT NULL
)