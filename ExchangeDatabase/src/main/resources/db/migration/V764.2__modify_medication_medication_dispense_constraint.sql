IF OBJECT_ID('UK_iq7aapdf84pfddtcqvas2tfia') IS NOT NULL
	ALTER TABLE [dbo].[Medication_MedicationDispense] DROP CONSTRAINT [UK_iq7aapdf84pfddtcqvas2tfia]
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] WITH CHECK ADD  CONSTRAINT [UK_iq7aapdf84pfddtcqvas2tfia] UNIQUE NONCLUSTERED 
(
	[medication_dispense_id] ASC, [medication_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO