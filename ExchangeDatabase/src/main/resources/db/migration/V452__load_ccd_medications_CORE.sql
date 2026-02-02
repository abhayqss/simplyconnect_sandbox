ALTER PROCEDURE [dbo].[load_ccd_medications_CORE]
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
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Medications
			SELECT DISTINCT
				m.medication_stopped,
				m.medication_started,
				m.free_text_sig,
				m.status_code,
				m.id,
				m_inf.product_name_text,
				cc.display_name,
				ins.text,
				m.resident_id
			FROM Medication m
				LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
				LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
				LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
				LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
				INNER JOIN (
					SELECT min(m.id) id FROM Medication m
						LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
						LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id 
						LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
					WHERE resident_id in (select resident_id from @found_residents)
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND m_inf.product_name_text IS NOT NULL AND m_inf.product_name_text<>''
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					GROUP BY
						m.medication_stopped,
						m.medication_started,
						m.free_text_sig,
						m.status_code,
						m_inf.product_name_text,
						ind.value_code_id) sub on sub.id = m.id
					-- m.instructions_id -- this field is not used for displaying info at the moment
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Medications
			SELECT DISTINCT
				m.medication_stopped,
				m.medication_started,
				m.free_text_sig,
				m.status_code,
				m.id,
				m_inf.product_name_text,
				cc.display_name,
				ins.text,
				m.resident_id
			FROM Medication m
				LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
				LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
				LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
				LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
			WHERE resident_id in (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND m_inf.product_name_text IS NOT NULL AND m_inf.product_name_text<>'';
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

END