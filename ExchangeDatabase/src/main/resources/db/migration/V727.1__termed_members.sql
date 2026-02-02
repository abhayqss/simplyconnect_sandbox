IF OBJECT_ID('HealthPartnersTermedMember') IS NOT NULL
    DROP TABLE HealthPartnersTermedMember;
GO

CREATE TABLE HealthPartnersTermedMember
(
    [id]                 [bigint] IDENTITY (1,1) NOT NULL,
    [hp_file_log_id]     [bigint]                NOT NULL,
    [received_datetime]  [datetime2](7)          NOT NULL,
    [is_success]         [bit]                   NOT NULL,
    [error_msg]          [varchar](MAX)          NULL,
    [member_identifier]  [varchar](30)           NULL,
    [member_first_name]  [varchar](70)           NULL,
    [member_middle_name] [varchar](70)           NULL,
    [member_last_name]   [varchar](70)           NULL,
    [birth_date]         [datetime2](7)          NULL,
    [resident_id]        [bigint]                NULL,
    [resident_is_new]    [bit]                   NULL,
    CONSTRAINT [PK_HealthPartnersTermedMember] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[HealthPartnersTermedMember]
    WITH CHECK ADD CONSTRAINT [FK_HealthPartnersTermedMember_HealthPartnersFileLog] FOREIGN KEY ([hp_file_log_id])
        REFERENCES [dbo].[HealthPartnersFileLog] ([id])
GO

ALTER TABLE [dbo].[HealthPartnersTermedMember]
    WITH CHECK ADD CONSTRAINT [FK_HealthPartnersTermedMember_Resident] FOREIGN KEY ([resident_id])
        REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE HealthPartnersFileLog
    ALTER COLUMN file_type varchar(40)
