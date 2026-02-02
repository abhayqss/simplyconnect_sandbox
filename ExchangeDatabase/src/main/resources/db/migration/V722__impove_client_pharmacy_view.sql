alter view ClientPharmacyFilterView as with
    comprehensives as (
        select id,
               resident_id,
               iif(assessment_status = 'IN_PROCESS', 1, 2) as assessment_status,
               iif(assessment_status = 'IN_PROCESS', date_assigned, date_completed) as date
        from ResidentAssessmentResult
        where assessment_id = (select id from Assessment where code = 'COMPREHENSIVE')
          and archived = 0
          and assessment_status in ('IN_PROCESS', 'COMPLETED')
    ),
    merged_CA as (
        select c.id, mrv.merged_resident_id as resident_id, c.assessment_status, c.date
        from comprehensives c
        join MergedResidentsView mrv on c.resident_id = mrv.resident_id
    ),
    latest_assessment as (
        select top 1 with ties id, resident_id, assessment_status
        from merged_CA
        order by row_number() over (partition by resident_id, assessment_status order by date desc)
    ),
    latest_in_progress_assessment_preffered as (
        select top 1 with ties id, resident_id
        from latest_assessment
        order by row_number() over (partition by resident_id order by assessment_status)
    ),
    active_or_unknown_medications as (
        select id, resident_id, dispensing_pharmacy_id, pharmacy_id
        from medication
        where dbo.fn_client_medication_status(status_code, medication_started, medication_stopped) in ('ACTIVE', 'UNKNOWN')
          and (dispensing_pharmacy_id is not null or pharmacy_id is not null)
    ),
    merged_medications as (
        select id, merged_resident_id as resident_id, dispensing_pharmacy_id, pharmacy_id
        from active_or_unknown_medications m
        join MergedResidentsView mrv on m.resident_id = mrv.resident_id
    )
--current pharmacy name column
select id as resident_id,
       current_pharmacy_name as pharmacy_name
from resident
where current_pharmacy_name is not null
  and current_pharmacy_name <> ''

--Comprehensive assessment pharmacy
union
select a.resident_id,
       rca.pharmacy_name
from ResidentComprehensiveAssessment rca
join latest_in_progress_assessment_preffered a on rca.resident_assessment_result_id = a.id
where pharmacy_name is not null
  and pharmacy_name <> ''

--Datasync ResPharmacy
union
select rph.resident_id,
       o.name as pharmacy_name
from ResPharmacy rph
join Organization o on rph.pharmacy_id = o.id
where o.name is not null
  and o.name <> ''

-- medications dispensing pharmacies
union
select m.resident_id,
       o.name as pharmacy_name
from merged_medications m
join Organization o on m.dispensing_pharmacy_id = o.id
where o.name is not null
  and o.name <> ''

-- medications pharmacies
union
select m.resident_id,
       o.name as pharmacy_name
from merged_medications m
join Organization o on m.pharmacy_id = o.id
where o.name is not null
  and o.name <> ''
go

