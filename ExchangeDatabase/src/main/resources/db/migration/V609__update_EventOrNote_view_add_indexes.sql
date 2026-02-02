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
	NULL			 AS sub_type_name,
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
    n.last_modified_date AS datetime,
	nst.description      AS sub_type_title,
    n.note_sub_type_id   AS sub_type_id,
	nst.code			 AS sub_type_name,
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

if (IndexProperty(Object_Id('[Note]'),
                  'IX_Note_last_modified_date', 'IndexId') is not null)
  drop INDEX [IX_Note_last_modified_date]
    ON [dbo].[Note]
GO

CREATE NONCLUSTERED INDEX [IX_Note_last_modified_date] ON [dbo].[Note]
(
	[last_modified_date] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

if (IndexProperty(Object_Id('[Event_enc]'),
                  'IX_Event_datetime', 'IndexId') is not null)
  drop INDEX [IX_Event_datetime]
    ON [dbo].[Event_enc]
GO

CREATE NONCLUSTERED INDEX [IX_Event_datetime] ON [dbo].[Event_enc]
(
	[event_datetime] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

