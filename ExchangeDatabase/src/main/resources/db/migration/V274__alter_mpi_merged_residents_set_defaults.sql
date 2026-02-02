SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MPI_merged_residents] ADD DEFAULT 0 FOR [merged_manually];
ALTER TABLE [dbo].[MPI_merged_residents] ADD DEFAULT 0 FOR [merged];
ALTER TABLE [dbo].[MPI_merged_residents] ADD DEFAULT 0 FOR [probably_matched];
ALTER TABLE [dbo].[MPI_merged_residents] ADD DEFAULT 0 FOR [merged_automatically];
GO