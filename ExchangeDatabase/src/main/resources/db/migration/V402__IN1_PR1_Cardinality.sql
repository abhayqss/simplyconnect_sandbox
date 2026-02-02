SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO


-- ==================================== Step 1. create connection tables. ===============================================

-- 1.1 create connection tables for A01
CREATE TABLE [dbo].[ADT_MSG2SGMNT_A01_TO_PR1] (
  [message_id] [bigint] NOT NULL,
  [pr1_id]     [bigint] NOT NULL,
  PRIMARY KEY (pr1_id, message_id),
  CONSTRAINT [FK_A01_TO_PR1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A01] ([id]),
  CONSTRAINT [FK_A01_TO_PR1_SGMNT] FOREIGN KEY ([pr1_id]) REFERENCES [dbo].[PR1_Procedures] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A01_TO_IN1] (
  [message_id] [bigint] NOT NULL,
  [in1_id]     [bigint] NOT NULL,
  PRIMARY KEY (in1_id, message_id),
  CONSTRAINT [FK_A01_TO_IN1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A01] ([id]),
  CONSTRAINT [FK_A01_TO_IN1_SGMNT] FOREIGN KEY ([in1_id]) REFERENCES [dbo].[IN1_Insurance] ([id])
)
GO

-- 1.2 create connection table for A03
CREATE TABLE [dbo].[ADT_MSG2SGMNT_A03_TO_PR1] (
  [message_id] [bigint] NOT NULL,
  [pr1_id]     [bigint] NOT NULL,
  PRIMARY KEY (pr1_id, message_id),
  CONSTRAINT [FK_A03_TO_PR1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A03] ([id]),
  CONSTRAINT [FK_A03_TO_PR1_SGMNT] FOREIGN KEY ([pr1_id]) REFERENCES [dbo].[PR1_Procedures] ([id])
)
GO

-- 1.3 create connection table for A04
CREATE TABLE [dbo].[ADT_MSG2SGMNT_A04_TO_PR1] (
  [message_id] [bigint] NOT NULL,
  [pr1_id]     [bigint] NOT NULL,
  PRIMARY KEY (pr1_id, message_id),
  CONSTRAINT [FK_A04_TO_PR1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A04] ([id]),
  CONSTRAINT [FK_A04_TO_PR1_SGMNT] FOREIGN KEY ([pr1_id]) REFERENCES [dbo].[PR1_Procedures] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A04_TO_IN1] (
  [message_id] [bigint] NOT NULL,
  [in1_id]     [bigint] NOT NULL,
  PRIMARY KEY (in1_id, message_id),
  CONSTRAINT [FK_A04_TO_IN1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A04] ([id]),
  CONSTRAINT [FK_A04_TO_IN1_SGMNT] FOREIGN KEY ([in1_id]) REFERENCES [dbo].[IN1_Insurance] ([id])
)
GO

-- 1.4 create connection table for A08
CREATE TABLE [dbo].[ADT_MSG2SGMNT_A08_TO_PR1] (
  [message_id] [bigint] NOT NULL,
  [pr1_id]     [bigint] NOT NULL,
  PRIMARY KEY (pr1_id, message_id),
  CONSTRAINT [FK_A08_TO_PR1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A08] ([id]),
  CONSTRAINT [FK_A08_TO_PR1_SGMNT] FOREIGN KEY ([pr1_id]) REFERENCES [dbo].[PR1_Procedures] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A08_TO_IN1] (
  [message_id] [bigint] NOT NULL,
  [in1_id]     [bigint] NOT NULL,
  PRIMARY KEY (in1_id, message_id),
  CONSTRAINT [FK_A08_TO_IN1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A08] ([id]),
  CONSTRAINT [FK_A08_TO_IN1_SGMNT] FOREIGN KEY ([in1_id]) REFERENCES [dbo].[IN1_Insurance] ([id])
)
GO


-- ======================================= Step 2 - copy data ==========================================================

-- 2.1 copy A01 PR1 and IN1 data
INSERT INTO [dbo].[ADT_MSG2SGMNT_A01_TO_PR1] (message_id, pr1_id)
  SELECT
    id,
    pr1_id
  from [dbo].[ADT_A01]
  where pr1_id IS NOT NULL;

INSERT INTO [dbo].[ADT_MSG2SGMNT_A01_TO_IN1] (message_id, in1_id)
  SELECT
    id,
    in1_id
  from [dbo].[ADT_A01]
  where in1_id IS NOT NULL;


-- 2.2 copy A03 PR1 data
INSERT INTO [dbo].[ADT_MSG2SGMNT_A03_TO_PR1] (message_id, pr1_id)
  SELECT
    id,
    pr1_id
  from [dbo].[ADT_A03]
  where pr1_id IS NOT NULL;


-- 2.3 copy A04 PR1 and IN1 data
INSERT INTO [dbo].[ADT_MSG2SGMNT_A04_TO_PR1] (message_id, pr1_id)
  SELECT
    id,
    pr1_id
  from [dbo].[ADT_A04]
  where pr1_id IS NOT NULL;

INSERT INTO [dbo].[ADT_MSG2SGMNT_A04_TO_IN1] (message_id, in1_id)
  SELECT
    id,
    in1_id
  from [dbo].[ADT_A04]
  where in1_id IS NOT NULL;


-- 2.4 copy A08 PR1 and IN1 data
INSERT INTO [dbo].[ADT_MSG2SGMNT_A08_TO_PR1] (message_id, pr1_id)
  SELECT
    id,
    pr1_id
  from [dbo].[ADT_A08]
  where pr1_id IS NOT NULL;

INSERT INTO [dbo].[ADT_MSG2SGMNT_A08_TO_IN1] (message_id, in1_id)
  SELECT
    id,
    in1_id
  from [dbo].[ADT_A08]
  where in1_id IS NOT NULL;
GO

-- ======================================= Step 3 - drop old columns ===================================================

-- 3.1 drop A01 PR1 and IN1 old column
ALTER TABLE [dbo].[ADT_A01] DROP CONSTRAINT FK_adta01_pr1,
  COLUMN pr1_id;

ALTER TABLE [dbo].[ADT_A01] DROP CONSTRAINT FK_adta01_in1,
  COLUMN in1_id;
GO

-- 3.2 drop A03 PR1 old column
ALTER TABLE [dbo].[ADT_A03] DROP CONSTRAINT FK_adta03_pr1,
  COLUMN pr1_id;
GO

-- 3.3 drop A04 PR1 and IN1 old column
ALTER TABLE [dbo].[ADT_A04] DROP CONSTRAINT FK_adta04_pr1,
  COLUMN pr1_id;

ALTER TABLE [dbo].[ADT_A04] DROP CONSTRAINT FK_adta04_in1,
  COLUMN in1_id;
GO

-- 3.4 drop A08 PR1 and IN1 old column
ALTER TABLE [dbo].[ADT_A08] DROP CONSTRAINT FK_adta08_pr1,
  COLUMN pr1_id;

ALTER TABLE [dbo].[ADT_A08] DROP CONSTRAINT FK_adta08_in1,
  COLUMN in1_id;
GO