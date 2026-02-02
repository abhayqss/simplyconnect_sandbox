/****** Object:  StoredProcedure [dbo].[load_ccd_social_history_count]    Script Date: 05/06/2015 18:00:25 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_social_history_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		s_history_id bigint,
		s_history_free_text varchar(255),
		s_history_observation_value varchar(max)
	)

	-- select data
	INSERT INTO @T 
		SELECT sh.id, sho.free_text, cc.display_name  
		FROM SocialHistory sh
		LEFT OUTER JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id 
		LEFT OUTER JOIN CcdCode cc ON sho.value_code_id = cc.id
		WHERE sh.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.s_history_id, 
		t.s_history_free_text, 
		t.s_history_observation_value
		FROM @T AS t
	)
	
		-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END
GO


