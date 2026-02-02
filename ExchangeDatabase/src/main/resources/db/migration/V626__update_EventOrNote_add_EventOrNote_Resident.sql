if (OBJECT_ID('EventOrNote') IS NOT NULL)
  DROP VIEW EventOrNote
GO

CREATE VIEW [dbo].[EventOrNote] AS
  SELECT
    CAST(CONCAT('e', e.id) as varchar(100)) as item_id,
    e.id                                    as numeric_id,
	1										as item_type_id,
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
	2										as item_type_id,
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

if (OBJECT_ID('EventOrNote_Resident') IS NOT NULL)
  DROP VIEW EventOrNote_Resident
GO

CREATE VIEW [dbo].[EventOrNote_Resident] AS
select  1 as item_type_id,
		e.id as numeric_id,
		e.resident_id as resident_id
 from event e
 UNION ALL
 select 2 as item_type_id,
		n.id as numeric_id,
		nr.resident_id as resident_id
 from note n
 join note_resident nr on nr.note_id = n.id


GO