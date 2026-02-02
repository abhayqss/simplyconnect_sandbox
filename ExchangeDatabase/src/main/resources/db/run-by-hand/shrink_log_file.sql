USE [eldermark_demo];
GO

ALTER DATABASE [eldermark_demo] SET RECOVERY SIMPLE;
GO

DBCC SHRINKFILE (eldermark_demo_Log, 10); --10MB
GO

ALTER DATABASE [eldermark_demo] SET RECOVERY FULL;
GO