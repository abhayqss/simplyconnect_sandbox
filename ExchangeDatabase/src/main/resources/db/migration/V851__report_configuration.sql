if object_id('ReportConfiguration_Organization_Disabled') is not null
    drop table ReportConfiguration_Organization_Disabled
go

if object_id('ReportConfiguration_Organization_Enabled') is not null
    drop table ReportConfiguration_Organization_Enabled
go

if object_id('ReportConfiguration_SourceDatabase_Disabled') is not null
    drop table ReportConfiguration_SourceDatabase_Disabled
go

if object_id('ReportConfiguration_SourceDatabase_Enabled') is not null
    drop table ReportConfiguration_SourceDatabase_Enabled
go

if object_id('ReportConfiguration') is not null
    drop table ReportConfiguration
go

create table ReportConfiguration
(
    report_type              varchar(32) not null,
    constraint PK_ReportConfiguration primary key (report_type),
    display_name             varchar(64) not null,
    shared                   bit         not null,
    depends_on_assessment_id bigint      null,
    constraint FK_ReportConfiguration_Assessment foreign key (depends_on_assessment_id) references Assessment (id)
)
go

create table ReportConfiguration_SourceDatabase_Enabled
(
    report_type varchar(32) not null,
    database_id bigint      not null,
    constraint PK_ReportConfiguration_SourceDatabase_Enabled primary key (report_type, database_id),
    constraint FK_ReportConfiguration_SourceDatabase_Enabled_database_id foreign key (database_id) references SourceDatabase (id)
)
go

create table ReportConfiguration_SourceDatabase_Disabled
(
    report_type varchar(32) not null,
    database_id bigint      not null,
    constraint PK_ReportConfiguration_SourceDatabase_Disabled primary key (report_type, database_id),
    constraint FK_ReportConfiguration_SourceDatabase_Disabled_database_id foreign key (database_id) references SourceDatabase (id)
)
go

create table ReportConfiguration_Organization_Enabled
(
    report_type     varchar(32) not null,
    organization_id bigint      not null,
    constraint PK_ReportConfiguration_Organization_Enabled primary key (report_type, organization_id),
    constraint FK_ReportConfiguration_Organization_Enabled_organization_id foreign key (organization_id) references Organization (id)
)
go

create table ReportConfiguration_Organization_Disabled
(
    report_type     varchar(32) not null,
    organization_id bigint      not null,
    constraint PK_ReportConfiguration_Organization_Disabled primary key (report_type, organization_id),
    constraint FK_ReportConfiguration_Organization_Disabled_organization_id foreign key (organization_id) references Organization (id)
)
go

insert into ReportConfiguration(report_type, display_name, shared, depends_on_assessment_id)
values ('DEMOGRAPHICS', 'Demographics, Assessments and Service Plans', 1, null),
       ('TIME_TO_COMPLETE_ASSESSMENT', 'Time to Complete Assessment', 1, null),
       ('IN_PERSON_TIME', 'In person time with individuals', 1, null),
       ('PHONE_CALL_TIME', 'Phone call time with individuals or coordinating services', 1, null),
       ('SERVICE_PLANS', 'Service Plans Completed on Individuals', 1, null),
       ('HUD', 'HUD Report', 0, null),
       ('HUD_MFSC', 'HUD MFSC Report', 1, null),
       ('CLIENT_SERVICES', 'Resident/Client current services', 1, null),
       ('HOSPITALIZATIONS', 'Hospitalizations', 1, null),
       ('COMPREHENSIVE', 'Comprehensive assessment', 1, null),
       ('REFERRALS', 'Referrals', 1, null),
       ('COVID_19_LOG', 'COVID-19 Log', 1, null),
       ('CLIENT_INTAKES', 'Client Intakes & Exits', 1, null),
       ('ADL_REPORT', 'ADLs, ADL, Medical history', 1, null),
       ('STAFF_CASELOAD', 'Number of individuals on staff caseload', 1, null),
       ('INSTITUTIONAL_RATE', 'Institutional Rate (ER, SNF, Hospital)', 1, null),
       ('CLIENT_PROGRAMS', 'Client programs', 1, null),
       ('IN_TUNE', 'InTune Report', 1, (select id from Assessment where code = 'CARE_MGMT')),
       ('ARIZONA_MATRIX', 'Arizona Matrix', 1, (select id from Assessment where code = 'ARIZONA_SSM')),
       ('ARIZONA_MATRIX_MONTHLY', 'Arizona Matrix Monthly', 1, (select id from Assessment where code = 'ARIZONA_SSM')),
       ('SIGNATURE_REQUESTS', 'Signatures Requests', 1, null),
       ('EVENTS_NOTES', 'Events/Notes Log', 1, null),
       ('ECM_TRACKING', 'ECM Tracking Report', 1, null),
       ('CS_TRACKING', 'CS Tracking Report', 1, null),
       ('CLIENT_EXPENSES', 'Client Expenses Report', 1, null),
       ('HOUSING_ASSESSMENT', 'Housing Assessment Report', 1, (select id from Assessment where code = 'HOUSING_ASSESSMENT')),
       ('OUTREACH_RETURN_TRACKER', 'Return Tracker and Outreach Report', 0, null)
go
