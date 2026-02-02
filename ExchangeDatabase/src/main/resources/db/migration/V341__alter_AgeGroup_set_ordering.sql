SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[AgeGroup]
  ADD [display_order] [BIGINT] NOT NULL DEFAULT 0;
GO

UPDATE [dbo].[AgeGroup]
SET [display_name] = 'Children/adolescents (0 - 17)'
WHERE [display_name] IN ('Children/adolescents', 'Children/adolescents (0 - 17)');

UPDATE [dbo].[AgeGroup]
SET [display_name] = 'Young adults (18 - 21)', [display_order] = 1
WHERE [display_name] IN ('Young adults', 'Young adults (18 - 21)');

UPDATE [dbo].[AgeGroup]
SET [display_name] = 'Adults (22 - 64)', [display_order] = 2
WHERE [display_name] IN ('Adults', 'Adults (22 - 64)');

UPDATE [dbo].[AgeGroup]
SET [display_order] = 3
WHERE [display_name] IN ('Seniors (65 or older)');

GO