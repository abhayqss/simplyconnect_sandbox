/****** Object:  StoredProcedure [dbo].[load_datasync_log_report]   ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER TABLE [dbo].[Document] ADD [deletion_time] datetime2(7) NULL;
GO

update [dbo].[Document]
set [deletion_time] = CURRENT_TIMESTAMP
where exists_in_file_store = 0

EXEC sp_RENAME 'dbo.Document.exists_in_file_store', 'visible', 'COLUMN'
GO
