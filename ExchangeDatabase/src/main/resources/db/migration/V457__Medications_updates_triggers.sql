SET XACT_ABORT ON
GO


-- ================================================== Medication =======================================================
IF OBJECT_ID('TRG_Medication_insert_update') IS NOT NULL
  DROP TRIGGER TRG_Medication_insert_update
GO

CREATE TRIGGER TRG_Medication_insert_update
  ON dbo.Medication
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from inserted
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_Medication_delete') IS NOT NULL
  DROP TRIGGER TRG_Medication_delete
GO

CREATE TRIGGER TRG_Medication_delete
  ON dbo.Medication
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from deleted
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


-- ============================================ MedicationInformation ==================================================
IF OBJECT_ID('TRG_MedicationInformation_insert_update') IS NOT NULL
  DROP TRIGGER TRG_MedicationInformation_insert_update
GO

CREATE TRIGGER TRG_MedicationInformation_insert_update
  ON dbo.MedicationInformation
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from inserted i
          join dbo.Medication m on m.medication_information_id = i.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_MedicationInformation_delete') IS NOT NULL
  DROP TRIGGER TRG_MedicationInformation_delete
GO

CREATE TRIGGER TRG_MedicationInformation_delete
  ON dbo.MedicationInformation
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from deleted d
          join dbo.Medication m on m.medication_information_id = d.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


-- ============================================ MedicationSupplyOrder ==================================================

IF OBJECT_ID('TRG_MedicationSupplyOrder_insert_update') IS NOT NULL
  DROP TRIGGER TRG_MedicationSupplyOrder_insert_update
GO

CREATE TRIGGER TRG_MedicationSupplyOrder_insert_update
  ON dbo.MedicationSupplyOrder
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from inserted i
          join dbo.Medication m on m.medication_supply_order_id = i.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_MedicationSupplyOrder_delete') IS NOT NULL
  DROP TRIGGER TRG_MedicationSupplyOrder_delete
GO

CREATE TRIGGER TRG_MedicationSupplyOrder_delete
  ON dbo.MedicationSupplyOrder
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from deleted d
          join dbo.Medication m on m.medication_supply_order_id = d.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

-- ================================================ MedicationDispense =================================================

IF OBJECT_ID('TRG_MedicationDispense_insert_update') IS NOT NULL
  DROP TRIGGER TRG_MedicationDispense_insert_update
GO

CREATE TRIGGER TRG_MedicationDispense_insert_update
  ON dbo.MedicationDispense
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct m.resident_id
        from inserted i
          join dbo.Medication_MedicationDispense mmd on mmd.medication_dispense_id = i.id
          join dbo.Medication m on mmd.medication_dispense_id = m.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_MedicationDispense_delete') IS NOT NULL
  DROP TRIGGER TRG_MedicationDispense_delete
GO

CREATE TRIGGER TRG_MedicationDispense_delete
  ON dbo.MedicationDispense
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct m.resident_id
        from deleted d
          join dbo.Medication_MedicationDispense mmd on mmd.medication_dispense_id = d.id
          join dbo.Medication m on mmd.medication_dispense_id = m.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


-- ======================================== ImmunizationMedicationInformation ==========================================

IF OBJECT_ID('TRG_ImmunizationMedicationInformation_insert_update') IS NOT NULL
  DROP TRIGGER TRG_ImmunizationMedicationInformation_insert_update
GO

CREATE TRIGGER TRG_ImmunizationMedicationInformation_insert_update
  ON dbo.ImmunizationMedicationInformation
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
      SELECT resident_id
      from inserted i
        join dbo.MedicationDispense md on md.immunization_medication_information_id = i.id
        join dbo.Medication_MedicationDispense mmd on mmd.medication_dispense_id = md.id
        join dbo.Medication m on mmd.medication_dispense_id = m.id
      union
      SELECT resident_id
      from inserted i
        join dbo.MedicationSupplyOrder mso on mso.immunization_medication_information_id = i.id
        join dbo.Medication m on m.medication_supply_order_id = mso.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_ImmunizationMedicationInformation_delete') IS NOT NULL
  DROP TRIGGER TRG_ImmunizationMedicationInformation_delete
GO

CREATE TRIGGER TRG_ImmunizationMedicationInformation_delete
  ON dbo.ImmunizationMedicationInformation
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
      SELECT resident_id
      from deleted d
        join dbo.MedicationDispense md on md.immunization_medication_information_id = d.id
        join dbo.Medication_MedicationDispense mmd on mmd.medication_dispense_id = md.id
        join dbo.Medication m on mmd.medication_dispense_id = m.id
      union
      SELECT resident_id
      from deleted d
        join dbo.MedicationSupplyOrder mso on mso.immunization_medication_information_id = d.id
        join dbo.Medication m on m.medication_supply_order_id = mso.id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'MEDICATION',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO
