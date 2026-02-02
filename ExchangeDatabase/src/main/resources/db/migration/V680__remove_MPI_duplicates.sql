IF OBJECT_ID('MPI_deleted_duplicates') is null
  begin
    --this table will contain deleted duplicates. According to this table 'registry_patient_id' identifiers should
    -- be changed to 'target_registry_patient_id' in OpenXds PostgreSQL registyr. After this table can be deleted manually.
    create table MPI_deleted_duplicates (
      registry_patient_id        varchar(255) not null,
      resident_id                bigint       not null,
      target_registry_patient_id varchar(255) not null,
    );
  end
GO

with duplicates as (
    select
      min(registry_patient_id)   min_registry_patient_id,
      count(registry_patient_id) counts,
      resident_id,
      assigning_authority,
      patient_id,
      deleted,
      merged,
      surviving_patient_id,
      assigning_authority_namespace,
      assigning_authority_universal,
      assigning_authority_universal_type,
      assigning_facility_namespace,
      assigning_facility_universal,
      assigning_facility_universal_type,
      type_code,
      effective_date,
      expiration_date
    from MPI
    where resident_id is not null
    group by resident_id,
      assigning_authority,
      patient_id, deleted, merged, surviving_patient_id, assigning_authority_namespace,
      assigning_authority_universal, assigning_authority_universal_type, assigning_facility_namespace,
      assigning_facility_universal, assigning_facility_universal_type, type_code, effective_date, expiration_date
    having count(registry_patient_id) > 1
), excludedDuplicates as (
    select
      m.registry_patient_id,
      m.resident_id,
      d.min_registry_patient_id
    from duplicates d
      join MPI m on
                   d.resident_id = m.resident_id and
                   d.assigning_authority = m.assigning_authority and
                   d.patient_id = m.patient_id and
                   (d.deleted = m.deleted
                    or d.deleted is null and m.deleted is null) and
                   (d.merged = m.merged
                    or d.merged is null and m.merged is null) and
                   (d.surviving_patient_id = m.surviving_patient_id
                    or d.surviving_patient_id is null and m.surviving_patient_id is null) and
                   (d.assigning_authority_namespace = m.assigning_authority_namespace
                    or d.assigning_authority_namespace is null and m.assigning_authority_namespace is null) and
                   (d.assigning_authority_universal = m.assigning_authority_universal
                    or d.assigning_authority_universal is null and m.assigning_authority_universal is null) and
                   (d.assigning_authority_universal_type = m.assigning_authority_universal_type
                    or d.assigning_authority_universal_type is null and m.assigning_authority_universal_type is null)
                   and
                   (d.assigning_facility_namespace = m.assigning_facility_namespace or
                    d.assigning_facility_namespace is null and m.assigning_facility_namespace is null) and
                   (d.assigning_facility_universal = m.assigning_facility_universal
                    or d.assigning_facility_universal is null and m.assigning_facility_universal is null) and
                   (d.assigning_facility_universal_type = m.assigning_facility_universal_type
                    or d.assigning_facility_universal_type is null and m.assigning_facility_universal_type is null) and
                   (d.type_code = m.type_code
                    or d.type_code is null and m.type_code is null) and
                   (d.effective_date = m.effective_date
                    or d.effective_date is null and m.effective_date is null) and
                   (d.expiration_date = m.expiration_date
                    or d.expiration_date is null and m.expiration_date is null)
    where registry_patient_id <> d.min_registry_patient_id
)
insert into MPI_deleted_duplicates (registry_patient_id, resident_id, target_registry_patient_id)
  select
    registry_patient_id,
    resident_id,
    min_registry_patient_id
  from excludedDuplicates;

delete m from MPI m
  join MPI_deleted_duplicates d
    on m.registry_patient_id = d.registry_patient_id and
       m.resident_id = d.resident_id
