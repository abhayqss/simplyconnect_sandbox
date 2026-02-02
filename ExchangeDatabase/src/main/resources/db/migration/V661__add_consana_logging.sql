IF OBJECT_ID('ConsanaMedicationLog') IS NOT NULL
  DROP TABLE ConsanaMedicationLog;
GO

IF OBJECT_ID('ConsanaAllergyObservationLog') IS NOT NULL
  DROP TABLE ConsanaAllergyObservationLog;
GO

IF OBJECT_ID('ConsanaProblemObservationLog') IS NOT NULL
  DROP TABLE ConsanaProblemObservationLog;
GO

IF OBJECT_ID('ConsanaEncounterLog') IS NOT NULL
  DROP TABLE ConsanaEncounterLog;
GO

IF OBJECT_ID('ConsanaMedicationActionPlanLog') IS NOT NULL
  DROP TABLE ConsanaMedicationActionPlanLog;
GO

IF OBJECT_ID('ConsanaPatientLog') IS NOT NULL
  DROP TABLE ConsanaPatientLog;
GO

IF OBJECT_ID('ConsanaPatientDispatchLog') IS NOT NULL
  DROP TABLE ConsanaPatientDispatchLog;
GO

IF OBJECT_ID('ConsanaEventDispatchLog') IS NOT NULL
  DROP TABLE ConsanaEventDispatchLog;
GO

CREATE TABLE ConsanaPatientLog (
  [id]                 bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]   datetime2(7) NOT NULL,
  [is_success]         bit          NOT NULL,
  [update_type]        varchar(255),
  [error_message]      varchar(max),
  [consana_patient_id] varchar(255),
  [organization_oid]   varchar(255) NOT NULL,
  [community_oid]      varchar(255) NOT NULL,
  CONSTRAINT PK_ConsanaPatientLog PRIMARY KEY ([id])
);
GO

CREATE TABLE ConsanaMedicationLog (
  [id]                    bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]      datetime2(7) NOT NULL,
  [is_success]            bit          NOT NULL,
  [update_type]           varchar(255),
  [error_message]         varchar(max),
  [consana_medication_id] varchar(255),
  [patient_log_id]        bigint       NOT NULL,
  CONSTRAINT PK_ConsanaMedicationLog PRIMARY KEY ([id]),
  CONSTRAINT FK_ConsanaMedicationLog_ConsanaPatientLog FOREIGN KEY ([patient_log_id]) REFERENCES ConsanaPatientLog ([id])
);
GO

CREATE TABLE ConsanaAllergyObservationLog (
  [id]                 bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]   datetime2(7) NOT NULL,
  [is_success]         bit          NOT NULL,
  [update_type]        varchar(255),
  [error_message]      varchar(max),
  [consana_allergy_id] varchar(255),
  [patient_log_id]     bigint       NOT NULL,
  CONSTRAINT PK_ConsanaAllergyObservationLog PRIMARY KEY ([id]),
  CONSTRAINT FK_ConsanaAllergyObservationLog_ConsanaPatientLog FOREIGN KEY ([patient_log_id]) REFERENCES ConsanaPatientLog ([id])
);
GO

CREATE TABLE ConsanaEncounterLog (
  [id]                   bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]     datetime2(7) NOT NULL,
  [is_success]           bit          NOT NULL,
  [update_type]          varchar(255),
  [error_message]        varchar(max),
  [consana_encounter_id] varchar(255),
  [patient_log_id]       bigint       NOT NULL,
  CONSTRAINT PK_ConsanaEncounterLog PRIMARY KEY ([id]),
  CONSTRAINT FK_ConsanaEncounterLog_ConsanaPatientLog FOREIGN KEY ([patient_log_id]) REFERENCES ConsanaPatientLog ([id])
);
GO

CREATE TABLE ConsanaProblemObservationLog (
  [id]                 bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]   datetime2(7) NOT NULL,
  [is_success]         bit          NOT NULL,
  [update_type]        varchar(255),
  [error_message]      varchar(max),
  [consana_problem_id] varchar(255),
  [patient_log_id]     bigint       NOT NULL,
  CONSTRAINT PK_ConsanaProblemObservationLog PRIMARY KEY ([id]),
  CONSTRAINT FK_ConsanaProblemObservationLog_ConsanaPatientLog FOREIGN KEY ([patient_log_id]) REFERENCES ConsanaPatientLog ([id])
);
GO

CREATE TABLE ConsanaMedicationActionPlanLog (
  [id]               bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime] datetime2(7) NOT NULL,
  [is_success]       bit          NOT NULL,
  [update_type]      varchar(255),
  [error_message]    varchar(max),
  [consana_plan_id]  varchar(255),
  [patient_log_id]   bigint       NOT NULL,
  CONSTRAINT PK_ConsanaMedicationActionPlanLog PRIMARY KEY ([id]),
  CONSTRAINT FK_ConsanaMedicationActionPlanLog_ConsanaPatientLog FOREIGN KEY ([patient_log_id]) REFERENCES ConsanaPatientLog ([id])
);
GO

CREATE TABLE ConsanaPatientDispatchLog (
  [id]                             bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]               datetime2(7) NOT NULL,
  [update_type]                    varchar(255) NOT NULL,
  [update_time]                    datetime2(7) NOT NULL,
  [consana_patient_id]             varchar(255),
  [organization_oid]               varchar(255) NOT NULL,
  [community_oid]                  varchar(255) NOT NULL,
  [was_already_processed_datetime] datetime2(7),
  [is_success]                     bit          NOT NULL,
  [error_message]                  varchar(max),
  CONSTRAINT PK_ConsanaPatientDispatchLog PRIMARY KEY ([id])
);
GO

CREATE TABLE ConsanaEventDispatchLog (
  [id]                 bigint       NOT NULL IDENTITY (1, 1),
  [process_datetime]   datetime2(7) NOT NULL,
  [event_id]           bigint,
  [consana_patient_id] varchar(255),
  [organization_oid]   varchar(255) NOT NULL,
  [community_oid]      varchar(255) NOT NULL,
  [is_success]         bit          NOT NULL,
  [error_message]      varchar(max),
  CONSTRAINT PK_ConsanaEventDispatchLog PRIMARY KEY ([id])
);
GO
