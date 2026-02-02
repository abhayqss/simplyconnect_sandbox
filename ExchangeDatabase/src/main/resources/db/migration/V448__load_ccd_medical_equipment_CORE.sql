ALTER PROCEDURE [dbo].[load_ccd_medical_equipment_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId;

		-- select data without duplicates
		INSERT INTO #Tmp_Medical_Equipment
			SELECT DISTINCT me.id, cc.display_name, me.effective_time_high, me.resident_id
			FROM MedicalEquipment me
				LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id
				LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
				INNER JOIN (
					SELECT min(me.id) id FROM MedicalEquipment me
					WHERE me.resident_id IN (select resident_id from @found_residents)
					GROUP BY
					me.product_instance_id, 
					me.effective_time_high
					) sub on sub.id = me.id
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					WHERE cc.display_name is not null AND cc.display_name<>'';
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Medical_Equipment
			SELECT DISTINCT me.id, cc.display_name, me.effective_time_high, me.resident_id
			FROM MedicalEquipment me
				LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id
				LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
			WHERE me.resident_id IN (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			And cc.display_name is not null AND cc.display_name<>'';
			--END : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

END