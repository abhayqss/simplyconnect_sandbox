ALTER PROCEDURE [dbo].[load_ccd_social_history_CORE]
  @ResidentId BIGINT,
  @Aggregated TINYINT
AS
BEGIN
  SET NOCOUNT ON;
   DECLARE @found_residents TABLE(
    resident_id BIGINT
  )
   IF (@Aggregated = 1)
    BEGIN
      INSERT INTO @found_residents EXEC dbo.find_merged_patients @ResidentId
       -- select data without duplicates
      INSERT INTO #Tmp_Social_History
        SELECT DISTINCT
          coalesce(tc.display_name, sho.free_text),
          coalesce(vc.display_name, sho.free_text_value),
          sh.resident_id
        FROM SocialHistory sh
          JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          LEFT JOIN CcdCode vc ON sho.value_code_id = vc.id
          LEFT JOIN CcdCode tc ON sho.type_code_id = tc.id
		  INNER JOIN (
			SELECT min(sho.id) id FROM SocialHistory sh
				JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			sho.free_text,
			sho.free_text_value,
			sho.type_code_id,
			sho.value_code_id
			) sub on sub.id = sho.id
		--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		WHERE coalesce(tc.display_name, sho.free_text) is NOT NULL AND coalesce(tc.display_name, sho.free_text)<>''
			AND coalesce(vc.display_name, sho.free_text_value) IS NOT NULL
			AND coalesce(vc.display_name, sho.free_text_value)<>''
		--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns

        -- Should we include SmokingStatusObservation into SocialHistory?
        -- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
         -- Added for i94 Care CoordinationCCN-1523 [CCD][Qualifacts] Result of "Social history" parsing is not displayed
		UNION ALL
		SELECT DISTINCT
          'Smoking Status',
          vc.display_name,
          sh.resident_id
        FROM SocialHistory sh
          JOIN SmokingStatusObservation sso ON sso.social_history_id = sh.id
		  LEFT JOIN CcdCode vc ON sso.value_code_id = vc.id
		  INNER JOIN (
			SELECT min(sso.id) id FROM SocialHistory sh
				JOIN SmokingStatusObservation sso ON sso.social_history_id = sh.id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			sso.effective_time_high,
			sso.effective_time_low,
			sso.value_code_id
			) sub on sub.id = sso.id
         --CCN-1523 end
		 --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		 WHERE vc.display_name is NOT NULL AND vc.display_name<>''
		--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		
		UNION ALL
		
        SELECT DISTINCT
          'Pregnancy',
          CASE WHEN po.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_low, 101)
          --Commented below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  --ELSE '?' END + ' - ' +
		  --Inserted below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  ELSE 'N/A' END + ' - ' +
          CASE WHEN po.effective_time_high IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_high, 101)
          --Commented below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  --ELSE '?' END,
		  --Inserted below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  ELSE 'N/A' END,
          sh.resident_id
        FROM SocialHistory sh
          JOIN PregnancyObservation po ON po.social_history_id = sh.id
		  INNER JOIN (
			SELECT min(po.id) id FROM SocialHistory sh
				JOIN PregnancyObservation po ON po.social_history_id = sh.id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			po.effective_time_high,
			po.effective_time_low
			) sub on sub.id = po.id
        UNION ALL
        SELECT DISTINCT
          'Tobacco Use',
          cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101)
            --Commented below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			--ELSE '?' END + ' - ?',
			--inserted below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			ELSE 'N/A' END + ' - N/A',
          sh.resident_id
        FROM SocialHistory sh
          JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          JOIN CcdCode cc ON tob.value_code_id = cc.id
		  INNER JOIN (
			SELECT min(tob.id) id FROM SocialHistory sh
				JOIN TobaccoUse tob ON tob.social_history_id = sh.id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			tob.value_code_id,
			tob.effective_time_low
			) sub on sub.id=tob.id
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE cc.display_name is NOT NULL AND cc.display_name<>'';
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
    END
  ELSE
    BEGIN
      INSERT INTO @found_residents SELECT @ResidentId
       -- select data
      INSERT INTO #Tmp_Social_History
        SELECT DISTINCT
          coalesce(tc.display_name, sho.free_text),
          coalesce(vc.display_name, sho.free_text_value),
          sh.resident_id
        FROM SocialHistory sh
          JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          LEFT JOIN CcdCode vc ON sho.value_code_id = vc.id
          LEFT JOIN CcdCode tc ON sho.type_code_id = tc.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
		--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		 AND coalesce(tc.display_name, sho.free_text) is NOT NULL AND coalesce(tc.display_name, sho.free_text)<>''
			AND coalesce(vc.display_name, sho.free_text_value) IS NOT NULL
			AND coalesce(vc.display_name, sho.free_text_value)<>''
		--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
        UNION ALL
        -- Should we include SmokingStatusObservation into SocialHistory?
        -- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
		
		 -- Added for i94 Care CoordinationCCN-1523 [CCD][Qualifacts] Result of "Social history" parsing is not displayed
		
		SELECT DISTINCT
          'Smoking Status',
          vc.display_name,
          sh.resident_id
        FROM SocialHistory sh
          JOIN SmokingStatusObservation sso ON sso.social_history_id = sh.id
		  LEFT JOIN CcdCode vc ON sso.value_code_id = vc.id
		  INNER JOIN (
			SELECT min(sso.id) id FROM SocialHistory sh
				JOIN SmokingStatusObservation sso ON sso.social_history_id = sh.id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			) sub on sub.id = sso.id
			--CCN-1523 end
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE vc.display_name is NOT NULL AND vc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		
		UNION ALL
        
        SELECT DISTINCT
          'Pregnancy',
          CASE WHEN po.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_low, 101)
          --Commented below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  --ELSE '?' END + ' - ' +
		  --inserted below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  ELSE 'N/A' END + ' - ' +
          CASE WHEN po.effective_time_high IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_high, 101)
          --Commented below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  --ELSE '?' END,
          --inserted below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		  ELSE 'N/A' END,
		  sh.resident_id
        FROM SocialHistory sh
          JOIN PregnancyObservation po ON po.social_history_id = sh.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
        UNION ALL
        SELECT DISTINCT
          'Tobacco Use',
          cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101)
            --Commented below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			--ELSE '?' END + ' - ?',
			--inserted below line for : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			ELSE 'N/A' END + ' - N/A',
          sh.resident_id
        FROM SocialHistory sh
          JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          JOIN CcdCode cc ON tob.value_code_id = cc.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
		--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		AND cc.display_name is NOT NULL AND cc.display_name<>'';
		--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
    END;
 END