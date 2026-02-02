CREATE TABLE [dbo].[RawXdsMessage] (
  [id]          [BIGINT]              NOT NULL IDENTITY PRIMARY KEY,
  [message]     [NVARCHAR](MAX)       NOT NULL,
  [date]        [datetime2]           NOT NULL
);
GO
