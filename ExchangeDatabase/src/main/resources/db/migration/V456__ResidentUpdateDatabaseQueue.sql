SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[ResidentUpdateQueue] (
  [id]          [BIGINT] IDENTITY (-9223372036854775808, 1) NOT NULL,
  [resident_id] [BIGINT]                                    NOT NULL,
  [update_type] varchar(20)                                 NOT NULL,
  [update_time] datetime2(7)                                NOT NULL
);

CREATE TYPE RESIDENT_UPDATE_QUEUE_DATA AS TABLE(
  [resident_id] BIGINT       NOT NULL,
  [update_type] varchar(20)  NOT NULL,
  [update_time] datetime2(7) NOT NULL
);
GO

CREATE PROCEDURE dequeue_resident_update_queue @batchSize bigint = 1
AS
  BEGIN;
    WITH extracted as (
        SELECT TOP (@batchSize)
          [resident_id],
          [update_type],
          [update_time]
        FROM ResidentUpdateQueue with ( rowlock, readpast )
        ORDER BY id
    ) delete from extracted
    output deleted.resident_id, deleted.update_type, deleted.update_time
  END;
GO

CREATE PROCEDURE enqueue_resident_update_queue @residnetUpdateQueueData RESIDENT_UPDATE_QUEUE_DATA READONLY
AS
  BEGIN;
    SET NOCOUNT ON;
    INSERT INTO ResidentUpdateQueue ([resident_id],
                                     [update_type],
                                     [update_time])
      SELECT
        [resident_id],
        [update_type],
        [update_time]
      FROM @residnetUpdateQueueData;
  END;
GO
