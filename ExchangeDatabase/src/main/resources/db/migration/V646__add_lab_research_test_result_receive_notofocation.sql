IF OBJECT_ID('LabResearchNotification') IS NOT NULL
DROP TABLE [dbo].[LabResearchNotification];
GO

CREATE TABLE LabResearchNotification (
  [id]                          bigint            NOT NULL IDENTITY (1,1),
  CONSTRAINT PK_ResearchNotification PRIMARY KEY ([id]),
  [notification_type]           varchar(50)       NOT NULL,
  [created_datetime]            [datetime2](7)    NOT NULL,
  [lab_research_order_id]       bigint            NOT NULL,
  CONSTRAINT FK_Research_notification_lab_research_order_id FOREIGN KEY ([lab_research_order_id]) REFERENCES LabResearchOrder ([id]),
  [employee_id]                 bigint            NOT NULL,
  CONSTRAINT FK_Research_notification_employee_id FOREIGN KEY ([employee_id]) REFERENCES Employee_enc ([id]),
  [sent_datetime]               [datetime2](7),
  [destination]                 varchar(50),
)
GO