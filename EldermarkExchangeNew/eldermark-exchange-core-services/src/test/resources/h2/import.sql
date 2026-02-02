-- auto-generated definition
DROP TABLE IF EXISTS DiagnosisSetup CASCADE;
create table DiagnosisSetup
(
    id             bigint auto_increment
        constraint PK_DiagnosisSetup primary key,

    legacy_id      bigint,
    database_id    bigint,
    name           varchar(1000),
    code           varchar(200),
    icd9cm         varchar(200),
    icd10cm        varchar(200),
    icd10pcs       varchar(200),
    isManual       bit,
    isStandardCode bit,
    inactive       bit
);

DROP TABLE CcdCode cascade;
CREATE VIEW CcdCode
AS
SELECT acc.id,
       ccc.value_set_name,
       ccc.value_set,
       ccc.code,
       ccc.code_system,
       ccc.display_name,
       ccc.inactive,
       ccc.code_system_name,
       0 as is_interpretation
FROM AnyCcdCode acc
         INNER JOIN ConcreteCcdCode ccc ON acc.id = ccc.id
UNION
SELECT acc.id,
       NULL AS value_set_name,
       NULL AS value_set,
       ucc.code,
       ucc.code_system,
       ucc.display_name,
       0    AS inactive,
       ucc.code_system_name,
       0    as is_interpretation
FROM AnyCcdCode acc
         INNER JOIN UnknownCcdCode ucc ON acc.id = ucc.id
UNION
SELECT acc.id,
       occ.value_set_name,
       occ.value_set,
       occ.code,
       occ.code_system,
       icc.display_name,
       occ.inactive,
       occ.code_system_name,
       1 as is_interpretation
FROM AnyCcdCode acc
         INNER JOIN InterpretiveCcdCode icc ON acc.id = icc.id
         INNER JOIN ConcreteCcdCode occ ON icc.referred_ccd_code = occ.id
UNION
SELECT acc.id,
       NULL AS value_set_name,
       NULL AS value_set,
       COALESCE(diagnosis.code, dcc.code),
       dcc.code_system,
       COALESCE(diagnosis.name, dcc.display_name),
       diagnosis.inactive,
       dcc.code_system_name,
       0    as is_interpretation
FROM AnyCcdCode acc
         INNER JOIN DiagnosisCcdCode dcc ON acc.id = dcc.id
         LEFT OUTER JOIN DiagnosisSetup diagnosis ON dcc.diagnosis_setup_id = diagnosis.id;

INSERT INTO SourceDatabase (name, alternative_id, oid, is_eldermark,
                            receive_non_network_referrals, labs_enabled, is_labs_research_testing, sdoh_reports_enabled,
                            is_chat_enabled, is_video_enabled, is_signature_enabled, is_appointments_enabled, exclude_from_record_search)
VALUES ('External Providers', 'EXT', 'EXT4567', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

SET @id = select id from SourceDatabase where oid='EXT4567';


INSERT INTO SystemSetup (database_id, login_company_id)
VALUES (@id, 'EXT');

INSERT INTO AnyCcdCode (id)
values (9999);

INSERT INTO ConcreteCcdCode (id, value_set_name, value_set, code, code_system, display_name, inactive, code_system_name)
VALUES (9999, 'ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '282291009', '2.16.840.1.113883.6.96', 'Diagnosis', 0,
        'SNOMED-CT');
