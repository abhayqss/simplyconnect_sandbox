DROP TABLE [dbo].[SystemSetup]

/****** Object:  Table [dbo].[SystemSetup]    Script Date: 07/08/2015 17:51:28 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[SystemSetup](
	[database_id] [bigint] NOT NULL,
	[login_company_id] [varchar](10) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UQ_SystemSetup_LoginCompanyId] UNIQUE NONCLUSTERED
(
	[login_company_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[SystemSetup]  WITH CHECK ADD  CONSTRAINT [FK_SystemSetup_DatabaseId] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[SystemSetup] CHECK CONSTRAINT [FK_SystemSetup_DatabaseId]
GO


/* Insert initial values to SystemSetup. Those would be updated by DataSync as soon as login_company_id will be added to all 4D databases. */
insert into [dbo].[SystemSetup]
	  (database_id, login_company_id)
    select id, id
	  from [dbo].[SourceDatabase] db
        where not exists (
            select 1
            from [dbo].[SystemSetup]
                where database_id = db.id
        )