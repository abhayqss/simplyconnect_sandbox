alter table Medication
    add comment varchar(max),
        creation_datetime datetime2(7),
        update_datetime datetime2(7),
        created_by bigint,
        updated_by bigint,
        medi_span_id varchar(16),
        is_manually_created bit,
        constraint FK_Medication_created_by foreign key (created_by) references Employee_enc (id),
        constraint FK_Medication_updated_by foreign key (updated_by) references Employee_enc (id)
go

alter table MedicationSupplyOrder
    add effective_time_low datetime2(7)
go

alter view ClientMedication as
    select m.id                          as id,
           m.free_text_sig               as free_text_sig,
           m.medication_started          as medication_started,
           m.medication_stopped          as medication_stopped,
           m.medication_information_id   as medication_information_id,
           m.repeat_number               as repeat_number,
           m.database_id                 as database_id,
           m.resident_id                 as resident_id,
           m.person_id                   as person_id,
           m.end_date_future             as end_date_future,
           m.pharmacy_origin_date        as pharmacy_origin_date,
           m.pharm_rx_id                 as pharm_rx_id,
           m.dispensing_pharmacy_id      as dispensing_pharmacy_id,
           m.pharmacy_id                 as pharmacy_id,
           m.refill_date                 as refill_date,
           m.medication_supply_order_id  as medication_supply_order_id,
           m.last_update                 as last_update,
           m.stop_delivery_after_date    as stop_delivery_after_date,
           m.prn_scheduled               as prn_scheduled,
           m.schedule                    as schedule,
           m.recurrence                  as recurrence,
           [dbo].[fn_client_medication_status](
                   m.status_code,
                   m.medication_started,
                   m.medication_stopped) as status,
           m.medi_span_id                as medi_span_id,
           m.comment                     as comment,
           m.creation_datetime           as creation_datetime,
           m.created_by                  as created_by,
           m.update_datetime             as update_datetime,
           m.updated_by                  as updated_by,
           m.is_manually_created         as is_manually_created

    from Medication m
go

