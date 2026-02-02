ALTER PROCEDURE [dbo].[Load_ccd_results_core] @ResidentId BIGINT, 
                                              @Aggregated TINYINT 
AS 
  BEGIN 
      SET nocount ON; 

      DECLARE @found_residents TABLE 
        ( 
           resident_id BIGINT 
        ); 

      IF ( @Aggregated = 1 ) 
        BEGIN 
            INSERT INTO @found_residents 
            EXEC dbo.Find_merged_patients 
              @ResidentId -- select data without duplicates 

            INSERT INTO #tmp_results 
            SELECT DISTINCT ro.id, 
                            ro.effective_time, 
                            ro.result_text, 
                            ro.status_code, 
                            cc.display_name, 
                            ro.result_value, 
                            ro.result_value_unit, 
                            rs.resident_id 
            FROM   result rs 
                   LEFT JOIN result_resultobservation rro 
                          ON rs.id = rro.result_id 
                   LEFT JOIN resultobservation ro 
                          ON rro.result_observation_id = ro.id 
                   LEFT JOIN resultobservationinterpretationcode roic 
                          ON ro.id = roic.result_observation_id 
                   LEFT JOIN ccdcode cc 
                          ON roic.interpretation_code_id = cc.id 
                   INNER JOIN (SELECT Min(ro.id) id 
                               FROM   result rs 
                                      LEFT JOIN result_resultobservation rro 
                                             ON rs.id = rro.result_id 
                                      LEFT JOIN resultobservation ro 
                                             ON rro.result_observation_id = 
                                                ro.id 
                   LEFT JOIN resultobservationinterpretationcode 
                             roic 
                          ON ro.id = roic.result_observation_id 
                   LEFT JOIN resultobservationrange ror 
                          ON ro.id = ror.result_observation_id 
                               WHERE  resident_id IN (SELECT resident_id 
                                                      FROM   @found_residents) 
                                      --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns 
                                      AND ro.result_text IS NOT NULL AND ro.result_text<>''
                                      AND ro.result_value IS NOT NULL AND ro.result_value<>''
                                      AND ro.result_value_unit IS NOT NULL AND ro.result_value_unit<>''
                               --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns 
                               GROUP  BY ro.effective_time, 
                                         ro.result_text, 
                                         ro.status_code, 
                                         roic.interpretation_code_id, 
                                         ror.result_range, 
                                         ro.result_value, 
                                         ro.result_value_unit) sub 
                           ON sub.id = ro.id 
        END 
      ELSE 
        BEGIN 
            INSERT INTO @found_residents 
            SELECT @ResidentId -- select data 

            INSERT INTO #tmp_results 
            SELECT DISTINCT ro.id, 
                            ro.effective_time, 
                            ro.result_text, 
                            ro.status_code, 
                            cc.display_name, 
                            ro.result_value, 
                            ro.result_value_unit, 
                            rs.resident_id 
            FROM   result rs 
                   LEFT JOIN result_resultobservation rro 
                          ON rs.id = rro.result_id 
                   LEFT JOIN resultobservation ro 
                          ON rro.result_observation_id = ro.id 
                   LEFT JOIN resultobservationinterpretationcode roic 
                          ON ro.id = roic.result_observation_id 
                   LEFT JOIN ccdcode cc 
                          ON roic.interpretation_code_id = cc.id 
            WHERE  resident_id IN (SELECT resident_id 
                                   FROM   @found_residents) 
                   --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns 
                   AND ro.result_text IS NOT NULL AND ro.result_text<>''
                   AND ro.result_value IS NOT NULL AND ro.result_value<>''
                   AND ro.result_value_unit IS NOT NULL AND ro.result_value_unit<>'';
        --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns 
        END; 

      INSERT INTO #tmp_results_s 
      SELECT DISTINCT ro.id, 
                      ror.result_range 
      FROM   result rs 
             LEFT JOIN result_resultobservation rro 
                    ON rs.id = rro.result_id 
             LEFT JOIN resultobservation ro 
                    ON rro.result_observation_id = ro.id 
             LEFT JOIN resultobservationrange ror 
                    ON ro.id = ror.result_observation_id 
      WHERE  resident_id IN (SELECT resident_id 
                             FROM   @found_residents); 
  END 
