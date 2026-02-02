if object_id('ClientAllergy') is not null
  drop view ClientAllergy
GO

create view ClientAllergy as
  select
    ao.id                                                               as allergy_observation_id,
    a.id                                                                as allergy_id,
    a.resident_id                                                       as client_id,
    ao.product_text                                                     as product_text,
    ISNULL(ao.allergy_type_text, ty.display_name)                       as type_text,
    ao.allergy_type_code_id                                             as type_code_id,
    ISNULL(severity.severity_text, sCode.display_name)                  as severity_text,
    severity.severity_code_id                                           as severity_code_id,
    STUFF
    (
        (
          SELECT distinct ', ' + ro.reaction_text
          from ReactionObservation ro
            JOIN AllergyObservation_ReactionObservation aoro on ro.id = aoro.reaction_observation_id
          where aoro.allergy_observation_id = ao.id
          ORDER BY ', ' + ro.reaction_text
          FOR XML PATH ('')
        ), 1, 2, ''
    )                                                                   as combined_reactions_texts,
    ISNULL(a.effective_time_low, ao.effective_time_low)                 as effective_time_low,
    ISNULL(a.effective_time_high, ao.effective_time_high)               as effective_time_high,
    ao.observation_status_code_id                                       as observation_status_code_id,
    IIF(st.display_name is not null, UPPER(st.display_name), 'UNKNOWN') as status
  from
    AllergyObservation ao
    join Allergy a on ao.allergy_id = a.id
    left join CcdCode st on st.id = ao.observation_status_code_id
    left join CcdCode ty on ty.id = ao.allergy_type_code_id
    left join SeverityObservation severity on severity.id = ao.severity_observation_id
    left join CcdCode sCode on sCode.id = severity.severity_code_id
