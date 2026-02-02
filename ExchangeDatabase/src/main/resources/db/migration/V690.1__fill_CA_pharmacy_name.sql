declare @pharmacyNameQuestion varchar(8) = '"name":"';

WITH answer_start_idx as (
    select rar.id,
           rar.resident_id,
           rar.json_result,
           charindex(@pharmacyNameQuestion, rar.json_result) + len(@pharmacyNameQuestion) as pharmacy_start
    from ResidentAssessmentResult rar
    where rar.assessment_id = (select id from Assessment where code = 'COMPREHENSIVE')
    ),
    asnwer_end_idx as (
        select id, resident_id, json_result, pharmacy_start, charindex('"', json_result, pharmacy_start) pharmacy_end
        from answer_start_idx
        where pharmacy_start > len(@pharmacyNameQuestion)
        ),
    pharmacy_answers as (
        select id, resident_id, substring(json_result, pharmacy_start, pharmacy_end - pharmacy_start) as pharmacy
        from asnwer_end_idx
        where pharmacy_end - pharmacy_start > 0
        )
    MERGE INTO ResidentComprehensiveAssessment rca
using pharmacy_answers source
on rca.resident_assessment_result_id = source.id
WHEN MATCHED THEN
    UPDATE
    SET rca.pharmacy_name = source.pharmacy
WHEN NOT matched THEN
    INSERT (resident_id, resident_assessment_result_id, pharmacy_name)
    VALUES (source.resident_id, source.id, source.pharmacy);
GO
