/****** Object:  StoredProcedure [dbo].[load_ccd_family_history_count]    Script Date: 05/06/2015 17:35:30 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_family_history_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		fh_id bigint,
		fh_deceased bit,
		fh_problem_name varchar(MAX),
		fh_age_observation_val int
	)

	-- select data
	INSERT INTO @T 
		SELECT 
			fh.id, 
			fho.deceased, 			
			CcdCode.display_name, 
			fho.age_observation_value 
			FROM FamilyHistory  fh 
			LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id  
			LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
		WHERE fh.resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
		t.fh_id,
		t.fh_problem_name + case when t.fh_deceased = 1 then ' (cause of death)' else '' end fh_problem_name,
		t.fh_age_observation_val	
		FROM @T t
		GROUP BY t.fh_id, t.fh_deceased, t.fh_problem_name, fh_age_observation_val
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END

GO