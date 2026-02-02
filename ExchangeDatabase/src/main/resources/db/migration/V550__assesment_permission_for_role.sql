CREATE TABLE AssessmentPermission (
  [role]          varchar(100) NOT NULL,
  [assessment_id] bigint       not null
)


INSERT INTO AssessmentPermission (role, assessment_id)
VALUES
  ('ROLE_PARENT_GUARDIAN', (select id
                            from Assessment
                            where [code] = 'GAD7')),
  ('ROLE_PARENT_GUARDIAN', (select id
                            from Assessment
                            where [code] = 'PHQ9')),
  ('ROLE_PERSON_RECEIVING_SERVICES', (select id
                                      from Assessment
                                      where [code] = 'GAD7')),
  ('ROLE_PERSON_RECEIVING_SERVICES', (select id
                                      from Assessment
                                      where [code] = 'PHQ9'))

--indexes defragmentation
ALTER INDEX all ON resident_enc REBUILD;
