IF OBJECT_ID('LabResearchResultsEmployeeNotification') IS NOT NULL
DROP TABLE [dbo].[LabResearchResultsEmployeeNotification];
GO

CREATE TABLE [dbo].[LabResearchResultsEmployeeNotification](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[notification_type] [varchar](50) NOT NULL,
	[created_datetime] [datetime2](7) NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[sent_datetime] [datetime2](7) NULL,
	[destination] [varchar](256) NULL,
 CONSTRAINT [PK_LabResearchResultsEmployeeNotification] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[LabResearchResultsEmployeeNotification]  WITH CHECK ADD  CONSTRAINT [FK_LabResearchResultsEmployeeNotification_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[LabResearchResultsEmployeeNotification] CHECK CONSTRAINT [FK_LabResearchResultsEmployeeNotification_Employee_enc]
GO