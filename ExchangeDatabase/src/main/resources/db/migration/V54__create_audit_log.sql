SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[AuditLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[action] [varchar](255) NOT NULL,
	[date] [datetime2](7) NOT NULL,
	[employee_id] [bigint] NOT NULL,
  PRIMARY KEY ([id]),
  FOREIGN KEY (employee_id) REFERENCES [dbo].[Employee] ([id]),
);

CREATE TABLE [dbo].[AuditLog_Residents](
	[audit_log_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
	FOREIGN KEY([audit_log_id]) REFERENCES [dbo].[AuditLog] ([id]),
	FOREIGN KEY([resident_id]) REFERENCES [dbo].[Resident] ([id])
);

CREATE TABLE [dbo].[AuditLog_Documents](
	[audit_log_id] [bigint] NOT NULL,
	[document_id] [bigint] NOT NULL,
	FOREIGN KEY([audit_log_id]) REFERENCES [dbo].[AuditLog] ([id]),
	FOREIGN KEY([document_id]) REFERENCES [dbo].[Document] ([id])
);