create table ResidentComprehensiveAssessment
  (
    id                          bigint identity
      primary key,
    resident_id                 bigint      not null
      constraint FK_resident_comprehensive_assessment
        references resident_enc,
    resident_assessment_result_id bigint  not null
      constraint FK_resident_comprehensive_assessment2
        references ResidentAssessmentResult,
    primary_care_physician_first_name varchar(max),
    primary_care_physician_last_name varchar(max));
