SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResLeaveOfAbsence](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[bed_hold_letter_sent] [bit] NULL,
	[from_date] [datetime2](7) NULL,
	[from_when] [bigint] NULL,
	[hospital_discharge_diagnosis] [varchar](255) NULL,
	[hospital_visit_location] [varchar](255) NULL,
	[hospital_visit_outcome] [varchar](255) NULL,
	[hospital_visit_reason] [varchar](max) NULL,
	[last_updated] [date] NULL,
	[meds_on_hold] [bit] NULL,
	[on_leave] [bit] NULL,
	[pre_pour_meds] [bit] NULL,
	[reason] [varchar](40) NULL,
	[service_on_hold] [bit] NULL,
	[to_date] [datetime2](7) NULL,
	[to_when_future] [bigint] NULL,
	[who_requested] [varchar](30) NULL,
	[database_id] [bigint] NOT NULL,
	[last_updated_employee_id] [bigint] NULL,
	[loa_reason_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence]  WITH CHECK ADD  CONSTRAINT [FK_2f088scawegle6te5ogs83hwl] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence] CHECK CONSTRAINT [FK_2f088scawegle6te5ogs83hwl]
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence]  WITH CHECK ADD  CONSTRAINT [FK_5eh668gsnd1niie4fhbiwqms3] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence] CHECK CONSTRAINT [FK_5eh668gsnd1niie4fhbiwqms3]
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence]  WITH CHECK ADD  CONSTRAINT [FK_em53ecjv8uqw69adam4ecg1ys] FOREIGN KEY([loa_reason_id])
REFERENCES [dbo].[LoaReason] ([id])
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence] CHECK CONSTRAINT [FK_em53ecjv8uqw69adam4ecg1ys]
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence]  WITH CHECK ADD  CONSTRAINT [FK_i9um4knhvwia74odpdcddke55] FOREIGN KEY([last_updated_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence] CHECK CONSTRAINT [FK_i9um4knhvwia74odpdcddke55]
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence]  WITH CHECK ADD  CONSTRAINT [FK_lohfa158s4saq2brfqa22pfbe] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResLeaveOfAbsence] CHECK CONSTRAINT [FK_lohfa158s4saq2brfqa22pfbe]
GO

