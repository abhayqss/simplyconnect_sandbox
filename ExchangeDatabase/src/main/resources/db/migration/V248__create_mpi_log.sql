CREATE TABLE [dbo].[MPI_log](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[last_matched] [datetime2],
	[last_index_updated] [datetime2]
)
GO
INSERT INTO [dbo].[MPI_log]
           ([last_matched]
           ,[last_index_updated])
     VALUES
           (null
           ,null)
GO