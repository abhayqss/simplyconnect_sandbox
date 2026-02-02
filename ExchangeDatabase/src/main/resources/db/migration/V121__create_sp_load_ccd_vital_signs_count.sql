/****** Object:  StoredProcedure [dbo].[load_ccd_vital_signs_count]    Script Date: 05/06/2015 17:37:25 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_vital_signs_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		vs_id bigint,
		vs_date datetime2(7),	
		vs_value varchar(50),
		vs_unit varchar(50),	
		vs_res_type varchar(max)
	)

	-- select data
	INSERT INTO @T 
	SELECT 
		vs.id, 
		vso.effective_time, 
		CAST(vso.value AS VARCHAR), 
		vso.unit, 
		cc.display_name
	FROM  VitalSign vs
		LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id 
		LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
	WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.vs_id, 
			t.vs_date,
			CAST(t.vs_value AS VARCHAR) + case when t.vs_unit IS NOT NULL then ' ' + REPLACE(REPLACE(t.vs_unit, '[', ''), ']', '') else '' end vs_value,
			t.vs_res_type
		FROM @T AS t
		GROUP BY t.vs_id, 
			t.vs_date,
			t.vs_value,
			t.vs_unit,
			t.vs_res_type
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END
GO