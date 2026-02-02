with cte as (
    select r.id as resident_id
    from resident r left join mpi m on r.id = m.resident_id
    where m.patient_id is null
)
Insert into MPI (registry_patient_id, resident_id, assigning_authority, patient_id, deleted, merged, assigning_authority_namespace,
                 assigning_authority_universal, assigning_authority_universal_type)
  select
    NEWID(),
    resident_id,
    'EXCHANGE&2.16.840.1.113883.3.6492&ISO',
    resident_id,
    'N',
    'N',
    'EXCHANGE',
    '2.16.840.1.113883.3.6492',
    'ISO' from cte
GO
