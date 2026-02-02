/****** Object:  StoredProcedure [dbo].[load_ccd_plan_of_care_count]    Script Date: 05/06/2015 18:02:14 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_plan_of_care_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
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
	  t.pl_of_care_date
		FROM @T AS t		
	)
	
		-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END
GO


