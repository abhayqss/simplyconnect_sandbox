if OBJECT_ID('ClientMedication') is not null
    drop view [dbo].[ClientMedication]
GO

CREATE VIEW [dbo].[ClientMedication] AS
SELECT m.id                          AS id,
       m.free_text_sig               AS free_text_sig,
       m.medication_started          AS medication_started,
       m.medication_stopped          AS medication_stopped,
       m.medication_information_id   AS medication_information_id,
       m.repeat_number               AS repeat_number,
       m.database_id                 AS database_id,
       m.resident_id                 AS resident_id,
       m.person_id                   AS person_id,
       m.end_date_future             AS end_date_future,
       m.pharmacy_origin_date        AS pharmacy_origin_date,
       m.pharm_rx_id                 AS pharm_rx_id,
       m.dispensing_pharmacy_id      AS dispensing_pharmacy_id,
       m.pharmacy_id                 AS pharmacy_id,
       m.refill_date                 AS refill_date,
       m.medication_supply_order_id  as medication_supply_order_id,
	   m.last_update                 AS last_update,
	   m.stop_delivery_after_date    AS stop_delivery_after_date,
	   m.prn_scheduled               AS prn_scheduled,
	   m.schedule                    AS schedule,
	   m.recurrence                  AS recurrence,
       [dbo].[fn_client_medication_status](
               m.status_code,
               m.medication_started,
               m.medication_stopped) as status

FROM Medication m
GO