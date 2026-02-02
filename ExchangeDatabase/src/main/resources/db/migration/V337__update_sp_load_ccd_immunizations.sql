SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[load_ccd_immunizations_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_immunizations_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_immunizations_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	);

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Immunizations
			SELECT DISTINCT i.id, imi.text, i.immunization_started, i.immunization_stopped,  i.status_code, i.resident_id
			FROM Immunization i
				LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
			WHERE i.id IN (
				SELECT min(i.id)
				FROM Immunization i
					LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
				WHERE i.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					imi.text,
					i.immunization_stopped,
					i.immunization_started,
					i.status_code
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Immunizations
			SELECT DISTINCT i.id, imi.text, i.immunization_started, i.immunization_stopped, i.status_code, i.resident_id
			FROM Immunization i
				LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
			WHERE  resident_id IN (select resident_id from @found_residents);
	END;

END
GO
