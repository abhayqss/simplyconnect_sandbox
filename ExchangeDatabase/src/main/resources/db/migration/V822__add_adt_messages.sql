--adt a05
IF OBJECT_ID('ADT_MSG2SGMNT_A05_TO_AL1') IS NOT NULL
    DROP table ADT_MSG2SGMNT_A05_TO_AL1
GO

IF OBJECT_ID('ADT_MSG2SGMNT_A05_TO_DG1') IS NOT NULL
    DROP table ADT_MSG2SGMNT_A05_TO_DG1
GO

IF OBJECT_ID('ADT_MSG2SGMNT_A05_TO_PR1') IS NOT NULL
    DROP table ADT_MSG2SGMNT_A05_TO_PR1
GO

IF OBJECT_ID('ADT_MSG2SGMNT_A05_TO_GT1') IS NOT NULL
    DROP table ADT_MSG2SGMNT_A05_TO_GT1
GO

IF OBJECT_ID('ADT_MSG2SGMNT_A05_TO_IN1') IS NOT NULL
    DROP table ADT_MSG2SGMNT_A05_TO_IN1
GO

IF OBJECT_ID('ADT_A05') IS NOT NULL
    DROP table ADT_A05
GO

create table ADT_A05
(
    id     bigint not null
        constraint PK_ADT_A05
            primary key
        constraint FK_adta05_AdtMessage
            references AdtMessage,
    evn_id bigint not null
        constraint FK_adta05_evn
            references EVN_EventTypeSegment,
    pid_id bigint not null
        constraint FK_adta05_pid
            references PID_PatientIdentificationSegment,
    pv1_id bigint not null
        constraint FK_adta05_pv1
            references PV1_PatientVisitSegment,
    pd1_id bigint
        constraint FK__adta05_pd1
            references ADT_SGMNT_PD1_Patient_Additional_Demographic
)
go

create table ADT_MSG2SGMNT_A05_TO_AL1
(
    id         bigint identity,
    constraint PK_ADT_MSG2SGMNT_A05_TO_AL1 primary key (id),

    message_id bigint not null
        constraint FK_A05_TO_AL1_MSG references ADT_A05 (id),
    segment_id bigint not null
        constraint FK_A05_TO_AL1_SGMNT references ADT_SGMNT_AL1_Allergy (id)
)
go


create table ADT_MSG2SGMNT_A05_TO_DG1
(
    id           bigint identity,
    constraint PK_ADT_MSG2SGMNT_A05_TO_DG1 primary key (id),

    message_id   bigint not null
        constraint FK_A05_TO_DG1_MSG references ADT_A05 (id),
    DG1_SGMNT_id bigint not null
        constraint FK_A05_TO_DG1_SGMNT references ADT_SGMNT_DG1_Diagnosis (id)
)
go

create table ADT_MSG2SGMNT_A05_TO_PR1
(
    message_id bigint not null
        constraint FK_A05_TO_PR1_MSG
            references ADT_A05,
    pr1_id     bigint not null
        constraint FK_A05_TO_PR1_SGMNT
            references PR1_Procedures,

    constraint PK_ADT_MSG2SGMNT_A05_TO_PR1 primary key (message_id, pr1_id)
)
go

create table ADT_MSG2SGMNT_A05_TO_GT1
(
    id           bigint identity
        constraint PK_ADT_MSG2SGMNT_A05_TO_GT1 primary key,
    message_id   bigint not null
        constraint FK_A05_TO_GT1_MSG
            references ADT_A05,
    GT1_SGMNT_id bigint not null
        constraint FK_A05_TO_GT1_SGMNT
            references ADT_SGMNT_GT1_Guarantor
)
go

create table ADT_MSG2SGMNT_A05_TO_IN1
(
    message_id bigint not null
        constraint FK_A05_TO_IN1_MSG
            references ADT_A05,
    in1_id     bigint not null
        constraint FK_A05_TO_IN1_SGMNT
            references IN1_Insurance,
    constraint PK_ADT_MSG2SGMNT_A05_TO_IN1 primary key (message_id, in1_id)
)
go

--adt a60
create table ADT_A60
(
    id     bigint not null
        constraint PK_ADT_A60
            primary key
        constraint FK_adta60_AdtMessage
            references AdtMessage,
    evn_id bigint not null
        constraint FK_adta60_evn
            references EVN_EventTypeSegment,
    pid_id bigint not null
        constraint FK_adta60_pid
            references PID_PatientIdentificationSegment,
    pv1_id bigint not null
        constraint FK_adta60_pv1
            references PV1_PatientVisitSegment
)
go

create table ADT_MSG2SGMNT_A60_TO_AL1
(
    id         bigint identity,
    constraint PK_ADT_MSG2SGMNT_A60_TO_AL1 primary key (id),

    message_id bigint not null
        constraint FK_A60_TO_AL1_MSG references ADT_A60 (id),
    segment_id bigint not null
        constraint FK_A60_TO_AL1_SGMNT references ADT_SGMNT_AL1_Allergy (id)
)
go
