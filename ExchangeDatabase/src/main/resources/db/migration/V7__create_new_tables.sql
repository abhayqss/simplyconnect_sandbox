SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[ReactionObservation_Medication]
(
  [reaction_observation_id] [bigint] NOT NULL,
  [medication_id] [bigint] NOT NULL UNIQUE,
  FOREIGN KEY ([reaction_observation_id]) REFERENCES [dbo].[ReactionObservation] ([id]),
  FOREIGN KEY ([medication_id]) REFERENCES [dbo].[Medication] ([id])
);

CREATE TABLE [dbo].[ReactionObservation_ProcedureActivity]
(
  [reaction_observation_id] [bigint] NOT NULL,
  [procedure_activity_id] [bigint] NOT NULL UNIQUE,
  FOREIGN KEY ([reaction_observation_id]) REFERENCES [dbo].[ReactionObservation] ([id]),
  FOREIGN KEY ([procedure_activity_id]) REFERENCES [dbo].[ProcedureActivity] ([id])
);