/****** Object:  StoredProcedure [dbo].[load_ccd_procedures]    Script Date: 05/06/2015 17:04:49 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [dbo].[load_ccd_procedures] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['procedure_type_text'|'procedure_started'|'procedure_stopped']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int
AS
BEGIN

	SET NOCOUNT ON;
	
	-- validate input params
	IF @Offset IS NULL OR @Offset < 0 
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN 
		SET @SortBy = 'procedure_started'
		SET @SortDir = 'DESC'
	END
	
	
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
		
	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.procedure_type_text, 
			t.procedure_started,
			t.procedure_stopped,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'procedure_type_text' AND @SortDir = 'ASC'  THEN t.procedure_type_text END ASC,
					CASE WHEN @SortBy = 'procedure_type_text' AND @SortDir = 'DESC' THEN t.procedure_type_text END DESC,
					CASE WHEN @SortBy = 'procedure_started' AND @SortDir = 'ASC'  THEN t.procedure_started END ASC,
					CASE WHEN @SortBy = 'procedure_started' AND @SortDir = 'DESC' THEN t.procedure_started END DESC,					
					CASE WHEN @SortBy = 'procedure_stopped' AND @SortDir = 'ASC' THEN t.procedure_stopped END ASC,
					CASE WHEN @SortBy = 'procedure_stopped' AND @SortDir = 'DESC' THEN t.procedure_stopped END DESC
			) AS RowNum
		FROM @T AS t
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, procedure_type_text, procedure_started, procedure_stopped 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 
END

GO