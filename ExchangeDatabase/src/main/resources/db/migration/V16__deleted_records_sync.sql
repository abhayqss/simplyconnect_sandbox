SET XACT_ABORT ON
GO

-- A table for storing all records from DeleteKeys_Exch_Sync 4D table (including ignored records) - used for support purposes
CREATE TABLE [dbo].[RecordDeletionHistory] (
  [id] [bigint] IDENTITY(1,1) PRIMARY KEY,
  [database_id] [bigint] NOT NULL,
  [sequence_num] [bigint] NOT NULL,
  [uuid] [varchar](255) NOT NULL,
  [table_name] [varchar](255) NOT NULL,
  [key_name] [varchar](255) NOT NULL,
  [key_value] [varchar](255) NOT NULL,
  [date_time] [datetime2](7) NULL,
  [recycle_bin_rec_num] [bigint] NULL
);

INSERT INTO [dbo].[DataSyncObjectStatus] ([id], [name]) VALUES (3, 'DELETED');