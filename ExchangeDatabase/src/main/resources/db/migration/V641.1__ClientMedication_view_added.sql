IF (OBJECT_ID('ClientMedication') IS NOT NULL)
DROP VIEW [dbo].[ClientMedication]
  GO

CREATE VIEW [dbo].[ClientMedication] AS
SELECT m.id AS id,
       m.free_text_sig              AS free_text_sig,
       m.medication_started         AS medication_started,
       m.medication_stopped         AS medication_stopped,
       m.medication_information_id  AS medication_information_id,
       m.dose_quantity              AS dose_quantity,
       m.dose_units                 AS dose_units,
       m.route_code_id              AS route_code_id,
       m.repeat_number              AS repeat_number,
       m.database_id                AS database_id,
       m.resident_id                AS resident_id,
       m.person_id                  AS person_id,
	   m.end_date_future            AS end_date_future,
	   m.pharmacy_origin_date       AS pharmacy_origin_date,
	   m.pharm_rx_id                AS pharm_rx_id,
	   m.dispensing_pharmacy_id     AS dispensing_pharmacy_id,
	   m.refill_date                AS refill_date,
       CASE
         WHEN m.status_code IS NOT NULL
           THEN IIF(m.status_code = 'active', 'ACTIVE', IIF(m.status_code = 'completed', 'COMPLETED', 'UNKNOWN'))
         WHEN m.medication_started IS NULL AND m.medication_stopped IS NULL
           THEN 'UNKNOWN'
         WHEN m.medication_stopped IS NOT NULL AND m.medication_stopped <= GETDATE()
           THEN 'COMPLETED'
         WHEN (m.medication_started IS NOT NULL AND m.medication_started < GETDATE()) AND (m.medication_stopped IS NULL OR m.medication_stopped > GETDATE())
           THEN 'ACTIVE'
         ELSE
           'UNKNOWN'
         END                         AS status
FROM Medication m
GO