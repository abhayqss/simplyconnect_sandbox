ALTER TABLE IncidentReport
  add [is_submit] bit not null,
  constraint DF_IncidentReport_is_submit default 0 FOR [is_submit]

;with rec (id, chain_id, lvl) as (
  select
    id,
    chain_id,
    1 as level
  from ResidentAssessmentResult
  union all
  select
    rec.id,
    r.chain_id,
    lvl + 1
  from rec
    inner join ResidentAssessmentResult r on rec.chain_id = r.id
)
MERGE INTO [dbo].[ResidentAssessmentResult] rar
USING (SELECT
         id,
         min(chain_id) as chain_id
       from rec
       group by id) n
ON rar.id = n.id and rar.chain_id <> n.chain_id
WHEN MATCHED THEN
  UPDATE SET chain_id = n.chain_id;

;with rec (id, chain_id, lvl) as (
  select
    id,
    chain_id,
    1 as level
  from ServicePlan
  union all
  select
    rec.id,
    r.chain_id,
    lvl + 1
  from rec
    inner join ServicePlan r on rec.chain_id = r.id
)
MERGE INTO [dbo].[ServicePlan] rar
USING (SELECT
         id,
         min(chain_id) as chain_id
       from rec
       group by id) n
ON rar.id = n.id and rar.chain_id <> n.chain_id
WHEN MATCHED THEN
  UPDATE SET chain_id = n.chain_id;

;with rec (id, chain_id, lvl) as (
  select
    id,
    chain_id,
    1 as level
  from IncidentReport
  union all
  select
    rec.id,
    r.chain_id,
    lvl + 1
  from rec
    inner join IncidentReport r on rec.chain_id = r.id
)
MERGE INTO [dbo].[IncidentReport] rar
USING (SELECT
         id,
         min(chain_id) as chain_id
       from rec
       group by id) n
ON rar.id = n.id and rar.chain_id <> n.chain_id
WHEN MATCHED THEN
  UPDATE SET chain_id = n.chain_id;
