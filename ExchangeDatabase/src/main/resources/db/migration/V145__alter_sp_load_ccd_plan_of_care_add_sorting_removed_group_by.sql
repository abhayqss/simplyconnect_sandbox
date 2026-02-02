/****** Object:  StoredProcedure [dbo].[load_ccd_plan_of_care]    Script Date: 05/29/2015 12:26:52 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


ALTER PROCEDURE [dbo].[load_ccd_plan_of_care]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['pl_of_care_date'|'pl_of_care_activity']
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
		SET @SortBy = 'pl_of_care_date'
		SET @SortDir = 'DESC'
	END
	
	
	DECLARE @T TABLE (	
		pl_of_care_id bigint,
		pl_of_care_activity varchar(max),
		pl_of_care_date datetime2(7)
	)

	-- select data
	INSERT INTO @T 
	SELECT poc.id, cc.display_name, poc_act.effective_time
	FROM  PlanOfCare poc
		LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id 
		LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
		LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
	WHERE resident_id = @ResidentId
	UNION 
	SELECT poc.id, cc.display_name, poc_act.effective_time
	FROM  PlanOfCare poc
		LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id 
		LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id 
		LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
	WHERE resident_id = @ResidentId
	UNION
	SELECT poc.id, cc.display_name, poc_act.effective_time
	FROM  PlanOfCare poc
		LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id                    
		LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
		LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
	WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
      t.pl_of_care_id,
	  t.pl_of_care_activity, 
	  t.pl_of_care_date,  
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'pl_of_care_activity' AND @SortDir = 'ASC'  THEN t.pl_of_care_date END ASC,
					CASE WHEN @SortBy = 'pl_of_care_activity' AND @SortDir = 'DESC' THEN t.pl_of_care_date END DESC,
					CASE WHEN @SortBy = 'pl_of_care_date' AND @SortDir = 'ASC'  THEN t.pl_of_care_date END ASC,
					CASE WHEN @SortBy = 'pl_of_care_date' AND @SortDir = 'DESC' THEN t.pl_of_care_date END DESC
			) AS RowNum
		FROM @T AS t		
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, pl_of_care_date, pl_of_care_activity
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END
GO
