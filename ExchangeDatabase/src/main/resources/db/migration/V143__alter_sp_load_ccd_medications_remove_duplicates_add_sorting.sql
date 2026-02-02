SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

/****** Object:  StoredProcedure [dbo].[load_ccd_medications]    Script Date: 05/28/2015 19:16:20 ******/

ALTER PROCEDURE [dbo].[load_ccd_medications]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['mdc_started'|'mdc_stopped'|'mdc_free_text_sig'|'mdc_status_code'|'mdc_info_product_name_text'|'instr_text']
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
		SET @SortBy = 'mdc_started'
		SET @SortDir = 'DESC'
	END
	
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
  		LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
  		LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
  		LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
  		LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
  		LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
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
      		t.instr_text, 
			STUFF (
				(SELECT ', ' + s.ind_display_name FROM @T as s WHERE s.mdc_id = t.mdc_id FOR XML PATH('')), 1, 1, ''
			) AS indications,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'mdc_started' AND @SortDir = 'ASC'  THEN t.mdc_started END ASC,
					CASE WHEN @SortBy = 'mdc_started' AND @SortDir = 'DESC' THEN t.mdc_started END DESC,
					CASE WHEN @SortBy = 'mdc_stopped' AND @SortDir = 'ASC' THEN t.mdc_stopped END ASC,
					CASE WHEN @SortBy = 'mdc_stopped' AND @SortDir = 'DESC' THEN t.mdc_stopped END DESC,
					CASE WHEN @SortBy = 'mdc_free_text_sig' AND @SortDir = 'ASC'  THEN t.mdc_free_text_sig END ASC,
					CASE WHEN @SortBy = 'mdc_free_text_sig' AND @SortDir = 'DESC' THEN t.mdc_free_text_sig END DESC,					
					CASE WHEN @SortBy = 'mdc_status_code' AND @SortDir = 'ASC'  THEN t.mdc_status_code END ASC,
					CASE WHEN @SortBy = 'mdc_status_code' AND @SortDir = 'DESC' THEN t.mdc_status_code END DESC,
					CASE WHEN @SortBy = 'mdc_info_product_name_text' AND @SortDir = 'ASC'  THEN t.mdc_info_product_name_text END ASC,
					CASE WHEN @SortBy = 'mdc_info_product_name_text' AND @SortDir = 'DESC' THEN t.mdc_info_product_name_text END DESC,
					CASE WHEN @SortBy = 'instr_text' AND @SortDir = 'ASC'  THEN t.mdc_info_product_name_text END ASC,
					CASE WHEN @SortBy = 'instr_text' AND @SortDir = 'DESC' THEN t.mdc_info_product_name_text END DESC
			) AS RowNum
		FROM @T AS t
		GROUP BY 
			t.mdc_id, 
			t.mdc_started, t.mdc_stopped, t.mdc_status_code, t.mdc_free_text_sig, t.mdc_info_product_name_text, t.instr_text
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, mdc_stopped, mdc_started, mdc_free_text_sig, mdc_status_code, mdc_info_product_name_text, instr_text, indications
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 
	
END
GO


/****** Object:  StoredProcedure [dbo].[load_ccd_medications_count]    Script Date: 05/29/2015 12:10:25 ******/

ALTER PROCEDURE [dbo].[load_ccd_medications_count]
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
  		LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
  		LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
  		LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
  		LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
  		LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
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
      		t.instr_text, 
			STUFF (
				(SELECT ', ' + s.ind_display_name FROM @T as s WHERE s.mdc_id = t.mdc_id FOR XML PATH('')), 1, 1, ''
			) AS indications
		FROM @T AS t
		GROUP BY 
			t.mdc_id, 
			t.mdc_started, t.mdc_stopped, t.mdc_status_code, t.mdc_free_text_sig, t.mdc_info_product_name_text, t.instr_text
	)
	
	-- count & output
	SELECT COUNT (*) as [count] 
	FROM SortedTable
	
END