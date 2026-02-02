SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MPI_merged_residents] ADD [merged_manually] [bit] NULL;
ALTER TABLE [dbo].[MPI_merged_residents] ADD [duke_confidence] [float] NULL;
GO

CREATE TABLE [dbo].[MPI_unmerged_residents] (
  [first_resident_id] [bigint] NOT NULL,
  [second_resident_id] [bigint] NOT NULL,

  FOREIGN KEY ([first_resident_id]) REFERENCES [dbo].[resident_enc] ([id]),
  FOREIGN KEY ([second_resident_id]) REFERENCES [dbo].[resident_enc] ([id]),
  CONSTRAINT [UK_MPI_unmerged_residents__first_second_resident_id] UNIQUE (first_resident_id, second_resident_id)
);
GO
