if (OBJECT_ID('Note_Resident') IS NOT NULL)
  DROP TABLE Note_Resident
GO

create table Note_Resident (

  [note_id]     bigint not null,
  constraint [FK_NoteResident_note] foreign key ([note_id]) references Note ([id]),

  [resident_id] bigint not null,
  constraint [FK_NoteResident_resident] foreign key ([resident_id]) references resident_enc ([id])

)
GO


insert into Note_Resident select
                            id,
                            resident_id
                          from Note
GO

Create index IX_Note_Resident_note
  on Note_Resident (note_id) include (resident_id)
GO

Create index IX_Note_Resident_resident
  on Note_Resident (resident_id) include (note_id)
GO

Create index IX_Note_Resident_note_resident
  on Note_Resident (note_id, resident_id)
GO

--make resident nullable
alter table Note
  alter column resident_id bigint
go

alter table Note
  add [note_name] varchar(256) null
GO

insert into NoteSubType (description, position, code, is_manual)
values
  ('Transportation', 20, 'TRANSPORTATION', 1),
  ('Emergency Response System (Lifeline)', 20, 'EMERGENCY_RESPONSE_SYSTEM_LIFELINE', 1),
  ('Nutrition', 20, 'NUTRITION', 1),
  ('Homemaking services', 20, 'HOMEMAKING_SERVICES', 1),
  ('Jobs & Family Services (Public Assistance)', 20, 'JOBS_AND_FAMILY_SERVICES_PUBLIC_ASSISTANCE', 1),
  ('Adult Protective Services', 20, 'ADULT_PROTECTIVE_SERVICES', 1),
  ('Durable Medical Equipment', 20, 'DURABLE_MEDICAL_EQUIPMENT', 1),
  ('Legal Aid', 20, 'LEGAL_AID', 1)
GO

alter table NoteSubType
  add [allowed_for_group_note] bit not null default 0
GO

update NoteSubType
set allowed_for_group_note = 1
where is_manual = 1 and follow_up_code is null


if (OBJECT_ID('EVENT_NOTE') IS NOT NULL)
  DROP VIEW EVENT_NOTE
GO

if (OBJECT_ID('EventOrNote') IS NOT NULL)
  DROP VIEW EventOrNote
GO

CREATE VIEW [dbo].[EventOrNote] AS
  SELECT
    CAST(CONCAT('e', e.id) as varchar(100)) as item_id,
    e.id                                    as numeric_id,
    r.first_name,
    r.middle_name,
    r.last_name,
    e.resident_id,
    et.description                          AS type_title,
    et.code                                 AS type_name,
    e.event_type_id                         AS type_id,
    e.event_datetime                        AS datetime,
    NULL                                    AS sub_type_title,
    NULL                                    AS sub_type_id,
    NULL                                    AS sub_type_name,
    e.is_er_visit                           As is_er_visit,
    e.id                                    as event_id,
    null                                    as note_id
  FROM event e
    INNER JOIN resident r
      ON e.resident_id = r.id
    INNER JOIN eventtype et
      ON et.id = e.event_type_id AND et.is_service = 0
  UNION ALL
  SELECT
    CAST(CONCAT('n', n.id) as varchar(100)) as id,
    n.id                                    as numeric_id,
    r.first_name,
    r.middle_name,
    r.last_name,
    n.resident_id,
    NULL                                    AS type_title,
    n.type                                  AS type_name,
    NULL                                    AS type_id,
    n.last_modified_date                    AS datetime,
    nst.description                         AS sub_type_title,
    n.note_sub_type_id                      AS sub_type_id,
    nst.code                                AS sub_type_name,
    null                                    As is_er_visit,
    null                                    as event_id,
    n.id                                    as note_id
  FROM note n
    LEFT JOIN resident r
      ON n.resident_id = r.id
    INNER JOIN notesubtype nst
      ON nst.id = n.note_sub_type_id
  WHERE n.archived = 0
GO
