
CREATE TABLE [dbo].[MPI_merged_residents](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[surviving_resident_id] [bigint] NOT NULL,
	[merged_resident_id] [bigint] NOT NULL,
	[merged] [bit] NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[MPI_merged_residents]  WITH CHECK ADD  CONSTRAINT [FK_MPI_merged_residents_resident_enc] FOREIGN KEY([merged_resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[MPI_merged_residents] CHECK CONSTRAINT [FK_MPI_merged_residents_resident_enc]
GO

ALTER TABLE [dbo].[MPI_merged_residents]  WITH CHECK ADD  CONSTRAINT [FK_MPI_merged_residents_resident_enc1] FOREIGN KEY([surviving_resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[MPI_merged_residents] CHECK CONSTRAINT [FK_MPI_merged_residents_resident_enc1]
GO

INSERT INTO MPI_merged_residents (merged_resident_id, surviving_resident_id)
Select m1.resident_id, m2.resident_id
FROM MPI m1 INNER JOIN MPI m2 ON (m1.surviving_patient_id=m2.patient_id  AND m1.assigning_authority=m2.assigning_authority)
WHERE m1.merged='Y'


