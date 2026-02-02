/****** Object:  StoredProcedure [dbo].[load_ccd_social_history]    Script Date: 05/06/2015 17:59:57 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_social_history]
	@ResidentId bigint,
	@SortBy varchar(50),
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
		SET @SortBy = ''
		SET @SortDir = ''
	END
	
	
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
		t.s_history_observation_value,
		ROW_NUMBER() OVER(ORDER BY t.s_history_id) AS RowNum
		FROM @T AS t
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, s_history_free_text, s_history_observation_value
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END
GO


