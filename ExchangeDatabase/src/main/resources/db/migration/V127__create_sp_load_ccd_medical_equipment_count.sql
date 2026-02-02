/****** Object:  StoredProcedure [dbo].[load_ccd_medical_equipment_count]    Script Date: 05/06/2015 17:58:38 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_medical_equipment_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		medical_equipment_id bigint,
		medical_equipment_device varchar(max),
		medical_equipment_time_heigh datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT me.id, cc.display_name, me.effective_time_high
		FROM MedicalEquipment me
		LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id 
		LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
		WHERE me.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.medical_equipment_id, 
		t.medical_equipment_device, 
		t.medical_equipment_time_heigh
		FROM @T AS t
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable
END
GO