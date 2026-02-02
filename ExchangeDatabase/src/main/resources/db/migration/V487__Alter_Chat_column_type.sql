

ALTER TABLE [dbo].[thread_messages] DROP COLUMN [text]

ALTER TABLE [dbo].[thread_messages] ADD [text] varbinary(max) 