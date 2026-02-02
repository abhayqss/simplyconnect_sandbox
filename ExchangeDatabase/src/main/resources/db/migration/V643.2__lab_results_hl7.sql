IF OBJECT_ID('LabResearchOrderORM') IS NOT NULL
  DROP VIEW LabResearchOrderORM
GO

IF OBJECT_ID('LabResearchOrderORM_enc') IS NOT NULL
  DROP TABLE LabResearchOrderORM_enc
GO

IF OBJECT_ID('LabResearchOrderORU') IS NOT NULL
  DROP TABLE LabResearchOrderORU
GO

IF COL_LENGTH('Document', 'lab_obx_id') IS NOT NULL
  BEGIN
    alter table Document
      drop constraint FK_Document_OBX_lab_obx_id;
    alter table Document
      drop column lab_obx_id;
  END
GO

IF COL_LENGTH('Document', 'lab_research_order_id') IS NOT NULL
  BEGIN
    alter table Document
      drop constraint FK_Document_LabResearchOrder_lab_research_order_id;
    alter table Document
      drop column lab_research_order_id;
  END
GO

IF COL_LENGTH('MSH_MessageHeaderSegment', 'processing_id') IS NOT NULL
  BEGIN
    alter table MSH_MessageHeaderSegment
      drop constraint FK_MSH_PT_processing_id;
    alter table MSH_MessageHeaderSegment
      drop column processing_id;
  END
GO

-- ========================================== ORM =============================================================
create table LabResearchOrderORM_enc (
  [id]                    [bigint]       not null identity (1, 1),
  constraint PK_LabResearchOrderORM primary key ([id]),

  [lab_research_order_id] bigint         not null,
  constraint FK_LabResearchOrderORM_LabResearchOrder_lab_research_order_id FOREIGN KEY ([lab_research_order_id]) references LabResearchOrder ([id]),

  [orm_raw]               varbinary(max) not null,
  [sent_datetime]         datetime2(7)
)
GO

create view LabResearchOrderORM
  as
    select
      [id],
      [lab_research_order_id],
      CONVERT(varchar(MAX), DecryptByKey([orm_raw])) orm_raw,
      [sent_datetime]
    from LabResearchOrderORM_enc
GO

create trigger LabResearchOrderORM_insert
  on LabResearchOrderORM
  instead of insert
AS
  BEGIN
    INSERT INTO LabResearchOrderORM_enc
    ([lab_research_order_id], [orm_raw], [sent_datetime])
      SELECT
        [lab_research_order_id],
        EncryptByKey(Key_GUID('SymmetricKey1'), [orm_raw]) orm_raw,
        [sent_datetime]
      FROM inserted SELECT @@IDENTITY;
  END
GO

create trigger LabResearchOrderORM_update
  on LabResearchOrderORM
  instead of update
AS
  BEGIN
    update LabResearchOrderORM_enc
    set
      [lab_research_order_id] = i.[lab_research_order_id],
      [orm_raw]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[orm_raw]),
      [sent_datetime]         = i.sent_datetime
    FROM inserted i
    where LabResearchOrderORM_enc.id = i.id
  END
GO

--===================================================== testing =================================================
IF COL_LENGTH('SourceDatabase', 'is_labs_research_testing') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop constraint DF_SourceDatabase_is_labs_research_testing_0;
    alter table SourceDatabase
      drop column is_labs_research_testing;
  END
GO

alter table SourceDatabase
  add [is_labs_research_testing] bit not null
  CONSTRAINT DF_SourceDatabase_is_labs_research_testing_0 default (0)
GO

--============================================ HL7 cleanup ========================================

IF OBJECT_ID('ORU_R01_OBX') IS NOT NULL
  DROP TABLE ORU_R01_OBX
GO

IF OBJECT_ID('ORU_R01_NTE') IS NOT NULL
  DROP TABLE ORU_R01_NTE
GO

IF OBJECT_ID('ORU_R01') IS NOT NULL
  DROP TABLE ORU_R01
GO

IF OBJECT_ID('SPM_Specimen') IS NOT NULL
  DROP TABLE SPM_Specimen
GO

IF OBJECT_ID('OBX_IS_abnormal_flags') IS NOT NULL
  DROP TABLE OBX_IS_abnormal_flags
GO

IF OBJECT_ID('OBX_Observation_Result_value') IS NOT NULL
  DROP TABLE OBX_Observation_Result_value
GO

IF OBJECT_ID('OBX_Observation_Result') IS NOT NULL
  DROP TABLE OBX_Observation_Result
GO

IF OBJECT_ID('ORC_XCN_ordering_provider') IS NOT NULL
  DROP TABLE ORC_XCN_ordering_provider
GO

IF OBJECT_ID('ORC_XCN_action_by') IS NOT NULL
  DROP TABLE ORC_XCN_action_by
GO

IF OBJECT_ID('ORC_CommonOrderSegment') IS NOT NULL
  DROP TABLE ORC_CommonOrderSegment
GO

IF OBJECT_ID('NTE_NotesAndComments_comment') IS NOT NULL
  DROP TABLE NTE_NotesAndComments_comment
GO

IF OBJECT_ID('NTE_NotesAndComments') IS NOT NULL
  DROP TABLE NTE_NotesAndComments
GO

IF OBJECT_ID('PT_ProcessingType') IS NOT NULL
  DROP TABLE PT_ProcessingType
GO

IF OBJECT_ID('DR_DateRange') IS NOT NULL
  DROP TABLE DR_DateRange
GO

IF OBJECT_ID('EIP_EntityIdentifierPair') IS NOT NULL
  DROP TABLE EIP_EntityIdentifierPair
GO

IF OBJECT_ID('EI_Entity_Identifier') IS NOT NULL
  DROP TABLE EI_Entity_Identifier
GO

--==================================== HL7 Data Types =================================================
create table EI_Entity_Identifier (
  [id]                bigint not null identity (1, 1),
  CONSTRAINT PK_EI_Entity_Identifier PRIMARY KEY ([id]),

  [entity_identifier] varchar(200),
  [namespace_id]      varchar(80),
  [universal_id]      varchar(200),
  [universal_id_type] varchar(6)
)
GO

create table EIP_EntityIdentifierPair (
  [id]                            bigint not null identity (1, 1),
  CONSTRAINT PK_EIP_EntityIdentifierPair PRIMARY KEY ([id]),

  [placer_assigned_identifier_id] bigint,
  CONSTRAINT FK_EIP_EI_placer_assigned_identifier FOREIGN KEY ([placer_assigned_identifier_id]) REFERENCES EI_Entity_Identifier ([id]),


  [filler_assigned_identifier_id] bigint,
  CONSTRAINT FK_EIP_EI_filler_assigned_identifier FOREIGN KEY ([filler_assigned_identifier_id]) REFERENCES EI_Entity_Identifier ([id]),

)
GO

create table DR_DateRange (
  [id]                   bigint not null identity (1, 1),
  CONSTRAINT PK_DR_DateRange PRIMARY KEY ([id]),

  [range_start_datetime] datetime2(7),
  [range_end_datetime]   datetime2(7)
)
GO

create table PT_ProcessingType (
  [id]              bigint not null identity (1, 1),
  CONSTRAINT PK_PT_Processing_Type PRIMARY KEY ([id]),

  [processing_id]   varchar(3),
  [processing_mode] varchar(3)
)
GO

IF COL_LENGTH('XCN_ExtendedCompositeIdNumberAndNameForPersons', 'id_number') IS NOT NULL
  BEGIN
    alter table XCN_ExtendedCompositeIdNumberAndNameForPersons
      drop column id_number;
  END
GO

alter table XCN_ExtendedCompositeIdNumberAndNameForPersons
  add [id_number] varchar(200)
GO

--============================================== HL7 Segments ====================================================
CREATE TABLE NTE_NotesAndComments (
  [id]                   bigint not null identity (1, 1),
  CONSTRAINT PK_NTE_NotesAndComments PRIMARY KEY ([id]),

  [set_id]               varchar(4),

  [source_of_comment_id] bigint,
  CONSTRAINT FK_NTE_ID_source_of_comment_id FOREIGN KEY ([source_of_comment_id]) REFERENCES ID_CodedValuesForHL7Tables ([id]),
)
GO

create table NTE_NotesAndComments_comment (
  [nte_id]  bigint       not null
    CONSTRAINT FK_NTE_NotesAndComments_comment_nte_id FOREIGN KEY ([nte_id]) REFERENCES NTE_NotesAndComments ([id]),

  [comment] varchar(250) not null
)
GO

CREATE TABLE ORC_CommonOrderSegment (
  [id]                       bigint not null identity (1, 1),
  CONSTRAINT PK_ORC_CommonOrderSegment PRIMARY KEY ([id]),

  [order_control]            varchar(2),

  [place_order_number_id]    bigint,
  CONSTRAINT FK_ORC_EI_place_order_number FOREIGN KEY ([place_order_number_id]) REFERENCES EI_Entity_Identifier ([id]),

  [filler_order_number_id]   bigint,
  CONSTRAINT FK_ORC_EI_filler_order_number FOREIGN KEY (filler_order_number_id) REFERENCES EI_Entity_Identifier ([id]),

  [datetime_of_transaction]  datetime2(7),

  [enterer_location_id]      bigint,
  CONSTRAINT FK_ORC_PL_enterer_location FOREIGN KEY ([enterer_location_id]) REFERENCES PL_PatientLocation ([id]),

  [order_effective_datetime] datetime2(7),

  [entering_organization_id] bigint,
  CONSTRAINT FK_ORC_CE_entering_organization FOREIGN KEY ([entering_organization_id]) REFERENCES CE_CodedElement ([id])
)
GO

create table ORC_XCN_ordering_provider (
  [orc_id]               bigint not null,
  CONSTRAINT FK_ORC_XCN_ordering_provider_orc_id FOREIGN KEY ([orc_id]) REFERENCES ORC_CommonOrderSegment ([id]),

  [ordering_provider_id] bigint not null,
  CONSTRAINT FK_ORC_XCN_ordering_provider_ordering_provider_id FOREIGN KEY ([ordering_provider_id]) REFERENCES XCN_ExtendedCompositeIdNumberAndNameForPersons ([id]),
)
GO

create table ORC_XCN_action_by (
  [orc_id]       bigint not null,
  CONSTRAINT ORC_XCN_action_by_orc_id FOREIGN KEY ([orc_id]) REFERENCES ORC_CommonOrderSegment ([id]),

  [action_by_id] bigint not null,
  CONSTRAINT FK_ORC_XCN_action_by_action_by_id FOREIGN KEY ([action_by_id]) REFERENCES XCN_ExtendedCompositeIdNumberAndNameForPersons ([id])
)
GO

CREATE TABLE OBX_Observation_Result (
  [id]                                 bigint not null identity (1, 1),
  CONSTRAINT PK_OBX_Observation_Result PRIMARY KEY ([id]),

  [set_id]                             varchar(4),
  [value_type]                         varchar(4),

  [obsv_identifier_id]                 bigint not null,
  CONSTRAINT FK_OBX_CE_obsv_identifier FOREIGN KEY ([obsv_identifier_id]) REFERENCES CE_CodedElement ([id]),

  --   [obsv_value]                         varchar(max), list

  [units_id]                           bigint,
  CONSTRAINT FK_OBX_CE_units FOREIGN KEY ([units_id]) REFERENCES CE_CodedElement ([id]),

  [references_range]                   varchar(60),

  --   [abnormal_flags_id]                  bigint, list
  --   CONSTRAINT FK_OBX_IS_abnormal_flags FOREIGN KEY ([abnormal_flags_id]) REFERENCES IS_CodedValueForUserDefinedTables ([id]),

  [observation_result_status_id]       bigint,
  CONSTRAINT FK_OBX_ID_observation_result_status FOREIGN KEY ([observation_result_status_id]) REFERENCES ID_CodedValuesForHL7Tables ([id]),

  [datetime_of_observation]            datetime2(7),

  [performing_org_name_id]             bigint,
  CONSTRAINT FK_OBX_XON_performing_org_name FOREIGN KEY ([performing_org_name_id]) REFERENCES XON_ExtendedCompositeNameAndIdForOrganizations ([id]),

  [performing_org_addr_id]             bigint,
  CONSTRAINT FK_OBX_XAD_performing_org_addr FOREIGN KEY ([performing_org_addr_id]) REFERENCES XAD_PatientAddress ([id]),

  [performing_org_medical_director_id] bigint,
  CONSTRAINT FK_OBX_XCN_performing_org_medical_director FOREIGN KEY ([performing_org_medical_director_id]) REFERENCES XCN_ExtendedCompositeIdNumberAndNameForPersons ([id])

)
GO

create table OBX_Observation_Result_value (
  [id]         bigint       not null identity (1, 1),
  CONSTRAINT PK_OBX_Observation_Result_value PRIMARY KEY ([id]),

  [obx_id]     bigint       not null,
  CONSTRAINT OBX_Observation_Result_value_obx_id FOREIGN KEY ([obx_id]) REFERENCES OBX_Observation_Result ([id]),

  [obsv_value] varchar(max) not null
)
GO

create table OBX_IS_abnormal_flags (
  [obx_id]           bigint not null,
  CONSTRAINT OBX_IS_abnormal_flags_obx_id FOREIGN KEY ([obx_id]) REFERENCES OBX_Observation_Result ([id]),

  [abnormal_flag_id] bigint not null,
  CONSTRAINT OBX_IS_abnormal_flags_abnormal_flag_id FOREIGN KEY ([abnormal_flag_id]) REFERENCES IS_CodedValueForUserDefinedTables ([id])

)
GO

create table SPM_Specimen (
  [id]                              bigint not null identity (1, 1),
  CONSTRAINT PK_SPM_Specimen PRIMARY KEY ([id]),

  [set_id]                          varchar(4),
  [specimen_ID_id]                  bigint,
  CONSTRAINT FK_SPM_EIP_specimen_ID FOREIGN KEY ([specimen_ID_id]) REFERENCES EIP_EntityIdentifierPair ([id]),

  [specimen_type_id]                bigint,
  CONSTRAINT FK_SPM_CE_specimen_ID FOREIGN KEY ([specimen_type_id]) REFERENCES CE_CodedElement ([id]),

  [specimen_collection_datetime_id] bigint,
  CONSTRAINT FK_SPM_DR_specimen_collection_datetime FOREIGN KEY ([specimen_collection_datetime_id]) REFERENCES DR_DateRange ([id]),

  [specimen_received_datetime]      datetime2(7)
)
GO

--============================================ HL7 Message =============================================
create table ORU_R01 (
  [id]     bigint not null identity (1, 1),
  CONSTRAINT PK_ORU_R01 PRIMARY KEY ([id]),

  [msh_id] bigint not null,
  CONSTRAINT FK_ORU_R01_MSH FOREIGN KEY ([msh_id]) REFERENCES MSH_MessageHeaderSegment ([id]),

  [pid_id] bigint not null,
  CONSTRAINT FK_ORU_R01_PID FOREIGN KEY ([pid_id]) REFERENCES PID_PatientIdentificationSegment ([id]),

  [pv1_id] bigint,
  CONSTRAINT FK_ORU_R01_PV1 FOREIGN KEY ([pv1_id]) REFERENCES PV1_PatientVisitSegment ([id]),

  [orc_id] bigint,
  CONSTRAINT FK_ORU_R01_ORC FOREIGN KEY ([orc_id]) REFERENCES ORC_CommonOrderSegment ([id]),

  [spm_id] bigint,
  CONSTRAINT FK_ORU_R01_SPM FOREIGN KEY ([spm_id]) REFERENCES SPM_Specimen ([id]),
)
GO

create table ORU_R01_NTE (
  [oru_id] bigint not null,
  CONSTRAINT FK_ORU_R01_NTE_oru_id FOREIGN KEY ([oru_id]) REFERENCES ORU_R01 ([id]),

  [nte_id] bigint not null,
  CONSTRAINT FK_ORU_R01_NTE_nte_id FOREIGN KEY ([nte_id]) REFERENCES NTE_NotesAndComments ([id])
)
GO

create table ORU_R01_OBX (
  [oru_id] bigint not null,
  CONSTRAINT FK_ORU_R01_OBX_oru_id FOREIGN KEY ([oru_id]) REFERENCES ORU_R01 ([id]),

  [obx_id] bigint not null,
  CONSTRAINT FK_ORU_R01_OBX_obx_id FOREIGN KEY ([obx_id]) REFERENCES OBX_Observation_Result ([id])
)
GO

-- ========================================== order ORU =============================================================
create table LabResearchOrderORU (
  [id]                    [bigint]     not null identity (1, 1),
  constraint PK_LabResearchOrderORU primary key ([id]),

  [lab_research_order_id] bigint       null,
  constraint FK_LabResearchOrderORU_LabResearchOrder_lab_research_order_id FOREIGN KEY ([lab_research_order_id]) references LabResearchOrder ([id]),

  [oru_log_file_name]     varchar(200) not null,
  [oru_id]                bigint,
  constraint FK_LabResearchOrderORU_ORU_R01 FOREIGN KEY ([oru_id]) references ORU_R01 ([id]),


  [received_datetime]     datetime2(7) not null,
  [success]               bit          not null CONSTRAINT [DF_LabResearchOrderORU_success] default 0,
  [is_testing]            bit          not null CONSTRAINT [DF_LabResearchOrderORU_is_testing] default 0,
  [file_name]             varchar(100) null,
  [error_message]         varchar(max)
)
GO

CREATE INDEX IX_LabResearchOrderORU_lab_research_order_id
  on LabResearchOrderORU (lab_research_order_id) include (id, oru_id)
--===================================================== MSH =================================================

alter table MSH_MessageHeaderSegment
  add [processing_id] bigint,
  CONSTRAINT FK_MSH_PT_processing_id FOREIGN KEY ([processing_id]) REFERENCES PT_ProcessingType ([id])
GO

-- =================================================== PID ===================================================
IF COL_LENGTH('PID_PatientIdentificationSegment', 'species_code_id') IS NOT NULL
  BEGIN
    alter table PID_PatientIdentificationSegment
      drop constraint FK_PID_CE_species_code;
    alter table PID_PatientIdentificationSegment
      drop column species_code_id;
  END
GO

IF COL_LENGTH('PID_PatientIdentificationSegment', 'patientID_id') IS NOT NULL
  BEGIN
    alter table PID_PatientIdentificationSegment
      drop constraint FK_PID_CX_patientID;
    alter table PID_PatientIdentificationSegment
      drop column patientID_id;
  END
GO

alter table PID_PatientIdentificationSegment
  add [patientID_id] bigint,
  CONSTRAINT FK_PID_CX_patientID FOREIGN KEY ([patientID_id]) REFERENCES CX_ExtendedCompositeId ([id]),

  [species_code_id] bigint,
  CONSTRAINT FK_PID_CE_species_code FOREIGN KEY ([species_code_id]) REFERENCES CE_CodedElement ([id])
GO

-- ================================================= Results ===================================================

alter table Document
  add [lab_obx_id] bigint,
  CONSTRAINT FK_Document_OBX_lab_obx_id FOREIGN KEY ([lab_obx_id]) REFERENCES OBX_Observation_Result ([id]),

  [lab_research_order_id] bigint,
  CONSTRAINT FK_Document_LabResearchOrder_lab_research_order_id FOREIGN KEY ([lab_research_order_id]) REFERENCES LabResearchOrder ([id])
GO

IF COL_LENGTH('LabResearchOrder', 'document_id') IS NOT NULL
  BEGIN
    alter table LabResearchOrder
      drop constraint FK_LabResearchOrder_LabResearchOrder_document_id;
    alter table LabResearchOrder
      drop column document_id;
  END
GO

alter table LabResearchOrder
  add [document_id] bigint
  CONSTRAINT FK_LabResearchOrder_LabResearchOrder_document_id FOREIGN KEY ([document_id]) REFERENCES Document ([id])
GO


if (OBJECT_ID('ClientDocument') IS NOT NULL)
  DROP VIEW [dbo].[ClientDocument]
GO

CREATE VIEW [dbo].[ClientDocument]
  AS
    SELECT
      d.[id],
      r.id as resident_id,
      e.id as employee_id,
      d.[author_db_alt_id],
      d.[author_legacy_id],
      d.[creation_time],
      d.[document_title],
      d.[mime_type],
      d.[original_file_name],
      d.[res_db_alt_id],
      d.[size],
      d.[uuid],
      d.[visible],
      d.[eldermark_shared],
      d.[deletion_time]
      --,d.[exists_in_file_store]
      ,
      d.[unique_id],
      d.[hash_sum],
      d.[is_cda],
      d.[marco_document_log_id],
      d.[lab_research_order_id],
      case when marco_document_log_id is not null
        then 'FAX'
      when lab_research_order_id is not null
        then 'LAB_RESULT'
      when is_cda = 1
        then 'CCD'
      else 'CUSTOM'
      end  as document_type
    FROM [dbo].[Document] d
      join SourceDatabase client_org on client_org.alternative_id = d.res_db_alt_id
      join Resident r on r.database_id = client_org.id and r.legacy_id = d.res_legacy_id
      join SourceDatabase author_org on author_org.alternative_id = d.author_db_alt_id
      join Employee e on e.database_id = author_org.id and e.legacy_id = d.author_legacy_id
GO

IF (OBJECT_ID('LabResearchOrderObservationResult') IS NOT NULL)
  DROP VIEW [dbo].[LabResearchOrderObservationResult]
GO

CREATE VIEW [dbo].[LabResearchOrderObservationResult]
  AS
    SELECT
      obx_5.id                               as id,
      orderOru.lab_research_order_id         as lab_research_order_id,
      obx_3.text                             as name,
      obx_5.obsv_value                       as value,
      obx_6.text                             as units_text,
      obx.references_range                   as limits,
      STUFF
      (
          (
            SELECT distinct ', ' + ISNULL(t.value, v.raw_code)
            from OBX_IS_abnormal_flags obx_8
              join IS_CodedValueForUserDefinedTables v on obx_8.abnormal_flag_id = v.id
              left join HL7CodeTable t on v.hl7_user_defined_code_table_id = t.id
            where obx_8.obx_id = obx.id
            order by ', ' + ISNULL(t.value, v.raw_code)
            FOR XML PATH ('')
          ), 1, 2, ''
      )                                      as abnormal_flags,

      obx_3.name_of_coding_system            as observation_source,
      obx.datetime_of_observation            as datetime_of_observation,
      obx_23.organization_name               as performing_org_name,
      obx.performing_org_addr_id             as performing_org_addr_id,
      obx.performing_org_medical_director_id as performing_org_medical_director_id
    FROM LabResearchOrderORU orderOru
      join ORU_R01_OBX oru_obx on oru_obx.oru_id = orderOru.oru_id
      join OBX_Observation_Result obx on obx.id = oru_obx.obx_id
      join OBX_Observation_Result_value obx_5 on obx.id = obx_5.obx_id
      left join CE_CodedElement obx_3 on obx.obsv_identifier_id = obx_3.id
      left join CE_CodedElement obx_6 on obx.units_id = obx_6.id
      left join XON_ExtendedCompositeNameAndIdForOrganizations obx_23 on obx_23.id = obx.performing_org_name_id
    where obx.value_type = 'ST'
GO