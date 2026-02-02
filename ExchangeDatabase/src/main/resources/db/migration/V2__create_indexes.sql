SET XACT_ABORT ON
GO
CREATE NONCLUSTERED INDEX resident_id_index
ON [dbo].[ResidentProcedure] ([resident_id])
INCLUDE ([id],[legacy_id],[database_id])
GO
CREATE NONCLUSTERED INDEX procedure_activity_id_index
ON [dbo].[ProcedureActivitySite] ([procedure_activity_id])
INCLUDE ([body_site_code])
GO