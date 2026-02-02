IF OBJECT_ID('EventOrNote', 'V') IS NOT NULL
	DROP VIEW [dbo].[EventOrNote]
GO

CREATE VIEW [dbo].[EventOrNote] AS
  SELECT
    e.id,
    'EVENT'          AS item_type,
    r.first_name,
    r.middle_name,
    r.last_name,
    e.resident_id,
    et.description   AS type_title,
	et.code          AS type_name,
    e.event_type_id  AS type_id,
    e.event_datetime AS datetime,
	NULL             AS sub_type_title,
	NULL			 AS sub_type_id,
    e.is_er_visit    As is_er_visit,
    e.id             as event_id,
    null             as note_id
  FROM event e 
    INNER JOIN resident r
      ON e.resident_id = r.id
    INNER JOIN eventtype et
      ON et.id = e.event_type_id AND et.is_service = 0
  UNION ALL
  SELECT
    n.id,
    'NOTE'               AS item_type,
    r.first_name,
    r.middle_name,
    r.last_name,
    n.resident_id,
	NULL				 AS type_title,
    n.type				 AS type_name,
    NULL				 AS type_id,
    case when n.note_date is not null then n.note_date else n.last_modified_date end AS datetime,
	nst.description      AS sub_type_title,
    n.note_sub_type_id   AS sub_type_id,
    null                 As is_er_visit,
    null                 as event_id,
    n.id                 as note_id
  FROM note n
    INNER JOIN resident r
      ON n.resident_id = r.id
    INNER JOIN notesubtype nst
      ON nst.id = n.note_sub_type_id
  WHERE n.archived = 0
GO

