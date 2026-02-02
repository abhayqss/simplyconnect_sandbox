
/****** Object:  StoredProcedure [dbo].[load_ccd_immunizations_count]    Script Date: 05/06/2015 17:38:36 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_immunizations_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	DECLARE @T TABLE (
		imm_id bigint,
		imm_text varchar(MAX),
		imm_started datetime2(7),	
		imm_stopped datetime2(7),	
		imm_status varchar(50)
	)

	-- select data
	INSERT INTO @T 
		SELECT i.id, imi.text, i.immunization_stopped, i.immunization_started,  i.status_code FROM Immunization i 
		LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
	WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.imm_id, 
			t.imm_text,				
			t.imm_started,					
			t.imm_stopped,							
			t.imm_status
		FROM @T AS t
	)
		
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable
END
GO


