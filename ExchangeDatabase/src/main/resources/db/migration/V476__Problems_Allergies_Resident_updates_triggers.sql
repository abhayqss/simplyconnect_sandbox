SET XACT_ABORT ON
GO

-- Using sequence for ResidentUpdateQueue because usage of identity makes @@IDENTITY return value generated for ResidentUpdateQueue
-- trigger instead of id generated for table insertion being done into
ALTER TABLE ResidentUpdateQueue
  DROP COLUMN id
GO

ALTER TABLE ResidentUpdateQueue
  add [order] BIGINT
GO

IF OBJECT_ID('SEQ_ResidentUpdateQueue_id') IS NOT NULL
  DROP SEQUENCE SEQ_ResidentUpdateQueue_id
GO

CREATE SEQUENCE SEQ_ResidentUpdateQueue_id
  AS BIGINT
  CYCLE;

UPDATE ResidentUpdateQueue
set [order] = NEXT VALUE FOR SEQ_ResidentUpdateQueue_id

ALTER TABLE ResidentUpdateQueue
  ALTER COLUMN [order] BIGINT NOT NULL
GO

IF OBJECT_ID('TRG_ResidentUpdateQueue_insert') IS NOT NULL
  DROP TRIGGER TRG_ResidentUpdateQueue_insert
GO

-- Even if a big chunk of residents are enqueued the order within it isn't important so we assign single order number
-- for all of them to speed up enqueueing.
CREATE TRIGGER TRG_ResidentUpdateQueue_insert
  ON dbo.ResidentUpdateQueue
  INSTEAD OF INSERT AS
  BEGIN
    INSERT INTO ResidentUpdateQueue (resident_id, update_type, update_time, [order])
      SELECT
        resident_id,
        update_type,
        update_time,
        NEXT VALUE FOR SEQ_ResidentUpdateQueue_id
      from inserted
  END
GO

-- changing ordering by id -> by [order]
ALTER PROCEDURE dequeue_resident_update_queue @batchSize bigint = 1
AS
  BEGIN;
    WITH extracted as (
        SELECT TOP (@batchSize)
          [resident_id],
          [update_type],
          [update_time]
        FROM ResidentUpdateQueue with ( rowlock, readpast )
        ORDER BY [order]
    ) delete from extracted
    output deleted.resident_id, deleted.update_type, deleted.update_time
  END;
GO


-- ============================================== Problem ==============================================================
IF OBJECT_ID('TRG_Problem_insert_update') IS NOT NULL
  DROP TRIGGER TRG_Problem_insert_update
GO

CREATE TRIGGER TRG_Problem_insert_update
  ON dbo.Problem
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
        'PROBLEM',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_Problem_delete') IS NOT NULL
  DROP TRIGGER TRG_Problem_delete
GO

CREATE TRIGGER TRG_Problem_delete
  ON dbo.Problem
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
        'PROBLEM',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

-- ============================================= ProblemObservation ====================================================
IF OBJECT_ID('TRG_ProblemObservation_insert_update') IS NOT NULL
  DROP TRIGGER TRG_ProblemObservation_insert_update
GO

CREATE TRIGGER TRG_ProblemObservation_insert_update
  ON dbo.ProblemObservation
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from inserted i
          join dbo.Problem p on p.id = i.problem_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'PROBLEM',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_ProblemObservation_delete') IS NOT NULL
  DROP TRIGGER TRG_ProblemObservation_delete
GO

CREATE TRIGGER TRG_ProblemObservation_delete
  ON dbo.ProblemObservation
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from deleted d
          join dbo.Problem p on p.id = d.problem_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'PROBLEM',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


-- ================================================= Allergy ===========================================================
IF OBJECT_ID('TRG_Allergy_insert_update') IS NOT NULL
  DROP TRIGGER TRG_Allergy_insert_update
GO

CREATE TRIGGER TRG_Allergy_insert_update
  ON dbo.Allergy
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
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


IF OBJECT_ID('TRG_Allergy_delete') IS NOT NULL
  DROP TRIGGER TRG_Allergy_delete
GO

CREATE TRIGGER TRG_Allergy_delete
  ON dbo.Allergy
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
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

-- =========================================== AllergyObservation ======================================================
IF OBJECT_ID('TRG_AllergyObservation_insert_update') IS NOT NULL
  DROP TRIGGER TRG_AllergyObservation_insert_update
GO

CREATE TRIGGER TRG_AllergyObservation_insert_update
  ON dbo.AllergyObservation
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from inserted i
          join dbo.Allergy a on a.id = i.allergy_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_AllergyObservation_delete') IS NOT NULL
  DROP TRIGGER TRG_AllergyObservation_delete
GO

CREATE TRIGGER TRG_AllergyObservation_delete
  ON dbo.AllergyObservation
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct resident_id
        from deleted d
          join dbo.Allergy a on a.id = d.allergy_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

-- ============================================== ReactionObservation ==================================================
IF OBJECT_ID('TRG_ReactionObservation_insert_update') IS NOT NULL
  DROP TRIGGER TRG_ReactionObservation_insert_update
GO

CREATE TRIGGER TRG_ReactionObservation_insert_update
  ON dbo.ReactionObservation
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct a.resident_id
        from inserted i
          join dbo.AllergyObservation_ReactionObservation ar on i.id = ar.reaction_observation_id
          join dbo.AllergyObservation ao on ao.id = ar.allergy_observation_id
          join dbo.Allergy a on a.id = ao.allergy_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_ReactionObservation_delete') IS NOT NULL
  DROP TRIGGER TRG_ReactionObservation_delete
GO

CREATE TRIGGER TRG_ReactionObservation_delete
  ON dbo.ReactionObservation
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct a.resident_id
        from deleted d
          join dbo.AllergyObservation_ReactionObservation ar on d.id = ar.reaction_observation_id
          join dbo.AllergyObservation ao on ao.id = ar.allergy_observation_id
          join dbo.Allergy a on a.id = ao.allergy_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

-- =============================================== SeverityObservation =================================================
IF OBJECT_ID('TRG_SeverityObservation_insert_update') IS NOT NULL
  DROP TRIGGER TRG_SeverityObservation_insert_update
GO

CREATE TRIGGER TRG_SeverityObservation_insert_update
  ON dbo.SeverityObservation
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct a.resident_id
        from inserted i
          join dbo.AllergyObservation ao on ao.severity_observation_id = i.id
          join dbo.Allergy a on a.id = ao.allergy_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_SeverityObservation_delete') IS NOT NULL
  DROP TRIGGER TRG_SeverityObservation_delete
GO

CREATE TRIGGER TRG_SeverityObservation_delete
  ON dbo.SeverityObservation
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct a.resident_id
        from deleted d
          join dbo.AllergyObservation ao on ao.severity_observation_id = d.id
          join dbo.Allergy a on a.id = ao.allergy_id
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.resident_id,
        'ALLERGY',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO


-- ================================================== Resident =========================================================
IF OBJECT_ID('TRG_Resident_insert_update') IS NOT NULL
  DROP TRIGGER TRG_Resident_insert_update
GO

CREATE TRIGGER TRG_Resident_insert_update
  ON dbo.resident_enc
  AFTER UPDATE, INSERT AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct id
        from inserted
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.id,
        'RESIDENT',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO

IF OBJECT_ID('TRG_Resident_delete') IS NOT NULL
  DROP TRIGGER TRG_Resident_delete
GO

CREATE TRIGGER TRG_Resident_delete
  ON dbo.resident_enc
  AFTER DELETE AS
  BEGIN
    declare @arg RESIDENT_UPDATE_QUEUE_DATA;
    with upd as (
        SELECT distinct id
        from deleted d
    )
    INSERT INTO @arg (resident_id, update_type, update_time)
      SELECT
        upd.id,
        'RESIDENT',
        GETDATE()
      from upd
    exec enqueue_resident_update_queue @arg;
  END
GO
