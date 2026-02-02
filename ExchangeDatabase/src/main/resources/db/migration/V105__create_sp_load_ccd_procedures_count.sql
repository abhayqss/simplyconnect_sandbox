/****** Object:  StoredProcedure [dbo].[load_ccd_procedures_count]    Script Date: 04/27/2015 16:13:41 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[load_ccd_procedures_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	DECLARE @T TABLE (
		procedure_type_text varchar(max),
		procedure_started datetime2(7),
		procedure_stopped datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT a.procedure_type_text, a.procedure_started, a.procedure_stopped
		FROM ResidentProcedure p
			JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
			JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
		WHERE resident_id = @ResidentId
		UNION
		SELECT a.procedure_type_text, a.procedure_started, a.procedure_stopped
		FROM ResidentProcedure p
			JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
			JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
		WHERE resident_id = @ResidentId
		UNION
		SELECT a.procedure_type_text, a.procedure_started, a.procedure_stopped
		FROM ResidentProcedure p
			JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
			JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
		WHERE resident_id = @ResidentId
	
	-- count & output
	SELECT COUNT (*) as [count] 
	FROM @T
	
END
GO