/****** Object:  StoredProcedure [dbo].[load_ccd_medications_count]    Script Date: 05/06/2015 17:06:09 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [dbo].[load_ccd_medications_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	DECLARE @T TABLE (	
		mdc_stopped datetime2(7),
		mdc_started datetime2(7),
		mdc_free_text_sig varchar(max),	
		mdc_status_code varchar(50),
		mdc_id bigint,    
		mdc_info_product_name_text varchar(max),
		ind_display_name varchar(max),
		instr_text varchar(255)
	)

	-- select data
	INSERT INTO @T 
		SELECT
			m.medication_stopped,
			m.medication_started,
			m.free_text_sig,
			m.status_code,
			m.id,
			m_inf.product_name_text,
			cc.display_name,
			ins.text
		FROM Medication m
			LEFT OUTER JOIN MedicationInformation m_inf
				ON m.medication_information_id = m_inf.id
			LEFT OUTER JOIN Medication_Indication m_ind
				ON m_ind.medication_id = m.id
			LEFT OUTER JOIN Indication ind
				ON m_ind.indication_id = ind.id
			LEFT OUTER JOIN CcdCode cc
				ON ind.value_code_id = cc.id
			LEFT OUTER JOIN Instructions ins
				ON m.instructions_id = ins.id
		WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.mdc_stopped, 
			t.mdc_started, 
			t.mdc_free_text_sig, 
			t.mdc_status_code, 
			t.mdc_info_product_name_text, 
			t.ind_display_name, 
			t.instr_text, 
				STUFF (
					(SELECT ', ' + s.ind_display_name FROM @T as s WHERE s.mdc_id = t.mdc_id FOR XML PATH('')), 1, 1, ''
				) AS indications
		FROM @T AS t
		GROUP BY t.mdc_id, t.mdc_started, t.mdc_stopped, t.mdc_status_code, t.mdc_free_text_sig, t.mdc_info_product_name_text, t.ind_display_name, t.instr_text
	)
	
	-- count & output
	SELECT COUNT (*) as [count] 
	FROM SortedTable
	
END

GO