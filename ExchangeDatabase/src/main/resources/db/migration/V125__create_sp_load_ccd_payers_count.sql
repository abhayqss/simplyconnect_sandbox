/****** Object:  StoredProcedure [dbo].[load_ccd_payer_providers_count]    Script Date: 05/06/2015 17:57:10 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_payer_providers_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	DECLARE @T TABLE (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_type varchar(MAX),
		payer_providers_time_low datetime2(7),			
		payer_providers_time_heigh datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT pr.id, o.name, cc.display_name, prt.effective_time_high, prt.effective_time_low
	FROM Payer pr
	LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id 
	LEFT OUTER JOIN Organization o ON pa.guarantor_organization_id = o.id                       
	LEFT OUTER JOIN CcdCode cc ON pa.health_insurance_type_code_id = cc.id 
	LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
		WHERE pr.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.payer_providers_id, 
		t.payer_providers_insurance_info, 
		t.payer_providers_insurance_type, 
		t.payer_providers_time_low, 
		t.payer_providers_time_heigh
		FROM @T AS t
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable		
END
GO