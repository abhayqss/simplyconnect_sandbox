alter table MedicationDispense
    alter column quantity DECIMAL(20,6)
GO

IF EXISTS(SELECT *
              FROM sys.indexes
              WHERE name = 'IX_Medication_MedicationDispense_medication_id' AND object_id = OBJECT_ID('dbo.Medication_MedicationDispense'))
        DROP INDEX [IX_Medication_MedicationDispense_medication_id] ON [dbo].[Medication_MedicationDispense]
GO

create index IX_Medication_MedicationDispense_medication_id
    on Medication_MedicationDispense (medication_id) include (medication_dispense_id)
go
