SET XACT_ABORT ON
GO

-- ============================================= Mpi Merged residents ==================================================
IF OBJECT_ID('TRG_MPI_merged_residents_insert') IS NOT NULL
  DROP TRIGGER TRG_MPI_merged_residents_insert
GO

CREATE TRIGGER TRG_MPI_merged_residents_insert
  ON dbo.MPI_merged_residents
  AFTER INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct
          surviving_resident_id,
          merged_resident_id
        from inserted
        where inserted.merged = 1
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.surviving_resident_id,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
      union
      SELECT
        upd.merged_resident_id,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_MPI_merged_residents_update') IS NOT NULL
  DROP TRIGGER TRG_MPI_merged_residents_update
GO

CREATE TRIGGER TRG_MPI_merged_residents_update
  ON dbo.MPI_merged_residents
  AFTER UPDATE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct
          inserted.surviving_resident_id isr,
          inserted.merged_resident_id imr,
          deleted.surviving_resident_id dsr,
          deleted.merged_resident_id dmr
        from inserted
          join deleted on inserted.id = deleted.id
        where inserted.merged <> deleted.merged or inserted.surviving_resident_id <> deleted.surviving_resident_id or
              inserted.merged_resident_id <> deleted.merged_resident_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.isr,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
      union
      SELECT
        upd.imr,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
      union
      SELECT
        upd.dsr,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
      union
      SELECT
        upd.dmr,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


IF OBJECT_ID('TRG_MPI_merged_residents_delete') IS NOT NULL
  DROP TRIGGER TRG_MPI_merged_residents_delete
GO

CREATE TRIGGER TRG_MPI_merged_residents_delete
  ON dbo.MPI_merged_residents
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct
          surviving_resident_id,
          merged_resident_id
        from deleted
        where deleted.merged = 1
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.surviving_resident_id,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
      union
      SELECT
        upd.merged_resident_id,
        'RESIDENT_MERGE',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO
