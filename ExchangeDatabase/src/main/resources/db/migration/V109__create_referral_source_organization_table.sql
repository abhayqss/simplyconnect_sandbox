SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ReferralSource_Organization](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[ref_source_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ReferralSource_Organization]  WITH CHECK ADD  CONSTRAINT [FK_2mfl67xyk9wn6g4s00tpfc43y] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ReferralSource_Organization] CHECK CONSTRAINT [FK_2mfl67xyk9wn6g4s00tpfc43y]
GO

ALTER TABLE [dbo].[ReferralSource_Organization]  WITH CHECK ADD  CONSTRAINT [FK_kcm7fjhsn90ed3g7kw1gggxd9] FOREIGN KEY([ref_source_id])
REFERENCES [dbo].[ReferralSource] ([id])
GO

ALTER TABLE [dbo].[ReferralSource_Organization] CHECK CONSTRAINT [FK_kcm7fjhsn90ed3g7kw1gggxd9]
GO

ALTER TABLE [dbo].[ReferralSource_Organization]  WITH CHECK ADD  CONSTRAINT [FK_lwfkv90ta48wgfiqk1lxu1xk8] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ReferralSource_Organization] CHECK CONSTRAINT [FK_lwfkv90ta48wgfiqk1lxu1xk8]
GO
