IF COL_LENGTH('Medication', 'pharmacy_id') IS NOT NULL
    BEGIN
        alter table Medication
            drop constraint FK_Medication_Organization_pharmacy_id;
        alter table Medication
            drop column pharmacy_id;
    END
GO

ALTER TABLE Medication
    add pharmacy_id bigint,
        constraint FK_Medication_Organization_pharmacy_id FOREIGN KEY (pharmacy_id) references Organization (id)

IF COL_LENGTH('ResidentComprehensiveAssessment', 'pharmacy_name') IS NOT NULL
    BEGIN
        alter table ResidentComprehensiveAssessment
            drop column pharmacy_name;
    END
GO

ALTER TABLE ResidentComprehensiveAssessment
    add pharmacy_name varchar(256)
GO

if OBJECT_ID('fn_client_medication_status') is not null
    drop function [dbo].[fn_client_medication_status]
GO

create function [dbo].[fn_client_medication_status](@status_code varchar(50),
                                                    @medication_started datetime2,
                                                    @medication_stopped datetime2)
    returns varchar(9) as
BEGIN
    return CASE
               WHEN @status_code IS NOT NULL
                   THEN IIF(@status_code = 'active', 'ACTIVE',
                            IIF(@status_code = 'completed', 'COMPLETED', 'UNKNOWN'))
               WHEN @medication_started IS NULL AND @medication_stopped IS NULL
                   THEN 'UNKNOWN'
               WHEN @medication_stopped IS NOT NULL AND @medication_stopped <= GETDATE()
                   THEN 'COMPLETED'
               WHEN (@medication_started IS NOT NULL AND @medication_started < GETDATE()) AND
                    (@medication_stopped IS NULL OR @medication_stopped > GETDATE())
                   THEN 'ACTIVE'
               ELSE
                   'UNKNOWN'
        END
END
GO

if OBJECT_ID('ClientMedication') is not null
    drop view [dbo].[ClientMedication]
GO

CREATE VIEW [dbo].[ClientMedication] AS
SELECT m.id                          AS id,
       m.free_text_sig               AS free_text_sig,
       m.medication_started          AS medication_started,
       m.medication_stopped          AS medication_stopped,
       m.medication_information_id   AS medication_information_id,
       m.dose_quantity               AS dose_quantity,
       m.dose_units                  AS dose_units,
       m.route_code_id               AS route_code_id,
       m.repeat_number               AS repeat_number,
       m.database_id                 AS database_id,
       m.resident_id                 AS resident_id,
       m.person_id                   AS person_id,
       m.end_date_future             AS end_date_future,
       m.pharmacy_origin_date        AS pharmacy_origin_date,
       m.pharm_rx_id                 AS pharm_rx_id,
       m.dispensing_pharmacy_id      AS dispensing_pharmacy_id,
       m.pharmacy_id                 AS pharmacy_id,
       m.refill_date                 AS refill_date,
       m.medication_supply_order_id  as medication_supply_order_id,
       [dbo].[fn_client_medication_status](
               m.status_code,
               m.medication_started,
               m.medication_stopped) as status

FROM Medication m
GO


IF OBJECT_ID('ClientPharmacyFilterView') is not null
    drop view [dbo].[ClientPharmacyFilterView]
GO

create view [dbo].[ClientPharmacyFilterView] as
with comprehensives as (
    select id, resident_id, assessment_status, date_assigned, date_completed
    from ResidentAssessmentResult
    where assessment_id = (select id from Assessment where code = 'COMPREHENSIVE')
      and archived = 0
      and assessment_status in ('IN_PROCESS', 'COMPLETED')
),
     merged_CA as (
         select c.id, mrv.merged_resident_id as resident_id, c.assessment_status, c.date_assigned, c.date_completed
         from comprehensives c
                  join MergedResidentsView MRV on c.resident_id = MRV.resident_id
     ),
     latest_in_progress_assessment as (
         select id, resident_id
         from (select id,
                      resident_id,
                      row_number() over (partition by resident_id order by date_assigned desc) in_progress_rn
               from merged_CA
               where assessment_status = 'IN_PROCESS') tmp
         where in_progress_rn = 1
     ),
     latest_completed_assessment as (
         select id, resident_id
         from (select id,
                      resident_id,
                      row_number() over (partition by resident_id order by date_completed desc) completed_rn
               from merged_CA
               where assessment_status = 'COMPLETED') tmp
         where completed_rn = 1
     ),
     latest_in_progress_assessment_preffered as (
         select id, resident_id
         from latest_in_progress_assessment
         union all
         select id, resident_id
         from latest_completed_assessment lca
         where not exists(select 1 from latest_in_progress_assessment lin where lin.resident_id = lca.resident_id)
     ),
     active_or_unknown_medications as (
         select id, resident_id, dispensing_pharmacy_id, pharmacy_id
         from medication
         where [dbo].[fn_client_medication_status](
                       status_code, medication_started, medication_stopped) in
               ('ACTIVE', 'UNKNOWN')
           and (dispensing_pharmacy_id is not null or pharmacy_id is not null)
     ),
     merged_medications as (
         select id, merged_resident_id as resident_id, dispensing_pharmacy_id, pharmacy_id
         from active_or_unknown_medications m
                  join MergedResidentsView V on m.resident_id = V.resident_id
     )
--current pharmacy name column
select id                    as resident_id,
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
       ph.name as pharmacy_name
from ResPharmacy rph
         join Organization ph on rph.pharmacy_id = ph.id
where ph.name is not null
  and ph.name <> ''

-- medications dispensing pharmacies
union
select m.resident_id,
       ph.name as pharmacy_name
from merged_medications m
         join Organization ph on m.dispensing_pharmacy_id = ph.id
where ph.name is not null
  and ph.name <> ''

-- medications pharmacies
union
select m.resident_id,
       ph.name as pharmacy_name
from merged_medications m
         join Organization ph on m.pharmacy_id = ph.id
where ph.name is not null
  and ph.name <> ''
GO
