/****** Object:  StoredProcedure [dbo].[load_ccd_medical_equipment]    Script Date: 05/06/2015 17:58:25 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_medical_equipment]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['medical_equipment_device'|'medical_equipment_time_heigh']
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
		SET @SortBy = 'medical_equipment_time_heigh'
		SET @SortDir = 'DESC'
	END
	
	
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
		t.medical_equipment_time_heigh,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'medical_equipment_device' AND @SortDir = 'ASC'  THEN t.medical_equipment_device END ASC,
					CASE WHEN @SortBy = 'medical_equipment_device' AND @SortDir = 'DESC' THEN t.medical_equipment_device END DESC,
					CASE WHEN @SortBy = 'medical_equipment_time_heigh' AND @SortDir = 'ASC'  THEN t.medical_equipment_time_heigh END ASC,
					CASE WHEN @SortBy = 'medical_equipment_time_heigh' AND @SortDir = 'DESC' THEN t.medical_equipment_time_heigh END DESC
			) AS RowNum
		FROM @T AS t
		GROUP BY  t.medical_equipment_id, 
		t.medical_equipment_device, 
		t.medical_equipment_time_heigh
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, medical_equipment_device, medical_equipment_time_heigh
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END
GO