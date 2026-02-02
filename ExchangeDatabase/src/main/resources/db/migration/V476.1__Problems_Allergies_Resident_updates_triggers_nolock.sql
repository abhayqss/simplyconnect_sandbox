SET XACT_ABORT ON
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
          join dbo.Problem (NOLOCK) p on p.id = i.problem_id
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
          join dbo.Problem (NOLOCK) p on p.id = d.problem_id
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
          join dbo.Allergy (NOLOCK) a on a.id = i.allergy_id
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
          join dbo.Allergy (NOLOCK) a on a.id = d.allergy_id
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
          join dbo.AllergyObservation_ReactionObservation (NOLOCK) ar on i.id = ar.reaction_observation_id
          join dbo.AllergyObservation (NOLOCK) ao on ao.id = ar.allergy_observation_id
          join dbo.Allergy (NOLOCK) a on a.id = ao.allergy_id
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
          join dbo.AllergyObservation_ReactionObservation (NOLOCK) ar on d.id = ar.reaction_observation_id
          join dbo.AllergyObservation (NOLOCK) ao on ao.id = ar.allergy_observation_id
          join dbo.Allergy (NOLOCK) a on a.id = ao.allergy_id
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
          join dbo.AllergyObservation (NOLOCK) ao on ao.severity_observation_id = i.id
          join dbo.Allergy (NOLOCK) a on a.id = ao.allergy_id
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
          join dbo.AllergyObservation (NOLOCK) ao on ao.severity_observation_id = d.id
          join dbo.Allergy (NOLOCK) a on a.id = ao.allergy_id
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
