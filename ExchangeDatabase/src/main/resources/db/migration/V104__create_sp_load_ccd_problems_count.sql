/****** Object:  StoredProcedure [dbo].[load_ccd_problems_count]    Script Date: 04/27/2015 16:12:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[load_ccd_problems_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	SELECT COUNT (*) as [count] 
	FROM Problem p
		LEFT JOIN ProblemObservation o ON o.problem_id = p.id
	WHERE p.resident_id = @ResidentId

END
GO