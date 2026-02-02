if object_id('ClientProblem') is not null
  drop view ClientProblem
GO

CREATE VIEW [dbo].[ClientProblem]
AS
SELECT 
			o.id as id,
			p.resident_id,
			o.problem_name as problem,
			isnull(translations.code, o.problem_value_code) as code,
			isnull(translations.code_system_name, o.problem_value_code_set) as code_set,
			c.display_name as problem_type,
			isnull(o.effective_time_low, p.effective_time_low) as identified_date,
			isnull(o.effective_time_high, p.effective_time_high) as stopped_date,

			o.onset_date,
			o.recorded_by,
			o.recorded_date,
			o.is_primary,
			o.comments,
			o.age_observation_unit,
			o.age_observation_value,
			o.problem_status_code_id,

			case 
				when o.problem_status_text = 'Active' 
					OR (o.problem_status_text in ('Intermittent', 'Rule out', 'Recurrent', 'Chronic') AND (isnull(o.effective_time_high, p.effective_time_high) IS NULL 
						OR isnull(o.effective_time_high, p.effective_time_high) > GETDATE())) 
					OR (o.problem_status_text IS NULL AND isnull(o.effective_time_low, p.effective_time_low) < GETDATE() 
						AND (isnull(o.effective_time_high, p.effective_time_high) IS NULL OR isnull(o.effective_time_high, p.effective_time_high) > GETDATE())) 
					then 'ACTIVE'
					when o.problem_status_text in ('Resolved', 'Ruled out', 'Inactive')
					OR (o.problem_status_text in ('Intermittent', 'Rule out', 'Recurrent', 'Chronic') AND isnull(o.effective_time_high, p.effective_time_high) IS NOT NULL 
						AND isnull(o.effective_time_high, p.effective_time_high) < GETDATE()) 
					OR (o.problem_status_text IS NULL AND isnull(o.effective_time_high, p.effective_time_high) < GETDATE())
					then 'RESOLVED'
					else 'OTHER' end as status
			FROM Problem p
			LEFT JOIN ProblemObservation o ON o.problem_id = p.id
			LEFT JOIN CcdCode c ON o.problem_type_code_id = c.id
			LEFT JOIN (Select pot.problem_observation_id, ccd.code_system_name, ccd.code from ProblemObservationTRanslation pot join CcdCode ccd on ccd.id = pot.translation_code_id where ccd.code_system_name = 'ICD-10-CM') translations on translations.problem_observation_id = o.id
GO
