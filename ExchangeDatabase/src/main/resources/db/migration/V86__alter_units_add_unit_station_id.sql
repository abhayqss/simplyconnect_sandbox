SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Unit] ADD [unit_station_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[Unit]  WITH CHECK ADD  CONSTRAINT [FK_k1rvwopnhw4t3q64jrdylffky] FOREIGN KEY([unit_station_id])
REFERENCES [dbo].[UnitStation] ([id])
GO

ALTER TABLE [dbo].[Unit] CHECK CONSTRAINT [FK_k1rvwopnhw4t3q64jrdylffky]
GO
