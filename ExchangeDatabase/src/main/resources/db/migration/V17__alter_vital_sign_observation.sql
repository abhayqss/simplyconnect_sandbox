SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[VitalSignObservation] ADD [legacy_id] [bigint] NULL;
GO

UPDATE [dbo].[VitalSignObservation] SET legacy_id = [VitalSign].legacy_id
FROM [dbo].[VitalSignObservation] INNER JOIN [dbo].[VitalSign]
ON [VitalSignObservation].[vital_sign_id] = [VitalSign].[id];
GO

ALTER TABLE [dbo].[VitalSignObservation] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[VitalSignObservation]
ADD CONSTRAINT UQ_VitalSignObservation_legacy UNIQUE ([database_id], [legacy_id]);
GO