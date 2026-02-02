CREATE VIEW EVENT_NOTE AS
SELECT e.id,
       'event'          AS item_type,
       r.first_name,
       r.middle_name,
       r.last_name,
       e.resident_id,
       et.description   AS type,
       e.event_type_id  AS type_id,
       e.event_datetime AS datetime,
       NULL             AS category,
	   e.is_er_visit    As is_er_visit
FROM   resident r
       INNER JOIN event e
               ON e.resident_id = r.id
       INNER JOIN eventtype et
               ON et.id = e.event_type_id
UNION ALL
SELECT n.id,
       'note'               AS item_type,
       r.first_name,
       r.middle_name,
       r.last_name,
       n.resident_id,
       nst.description      AS type,
       n.note_sub_type_id   AS type_id,
       n.last_modified_date AS datetime,
       n.type               AS category,
	   null    As is_er_visit
FROM   note n
       INNER JOIN resident r
               ON n.resident_id = r.id
       INNER JOIN notesubtype nst
               ON nst.id = n.note_sub_type_id
WHERE  n.archived = 0; 
