ALTER TABLE [dbo].[resident_enc]
  ADD [genacross_id] BIGINT NULL;

exec update_resident_view
go

exec update_resident_history_view
go
