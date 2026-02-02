BEGIN TRANSACTION TransactionWithGos;
GO

SET XACT_ABORT ON; -- Roll back everything if error occurs in script
GO


if (OBJECT_ID('tempdb..#CcdCode_insert_temp') IS NOT NULL)
  DROP table #CcdCode_insert_temp

create table #CcdCode_insert_temp
(
  --id             bigint identity (1, 1) not null,
  code         varchar(8) COLLATE database_default   NOT NULL,
  display_name varchar(300) COLLATE database_default NOT NULL
)
go

BULK INSERT #CcdCode_insert_temp FROM 'C:\projects\exchange_extras\icd10\icd-10-codes.txt' WITH ( FIELDTERMINATOR = '|', ROWTERMINATOR = '\n')
GO

select
  t.*,
  c.*,
  d.id as from_diag,
  u.id as from_unk
FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
  left join DiagnosisCcdCode d on d.id = c.id
  left join UnknownCcdCode u on u.id = c.id
where c.id is not null and t.display_name <> c.display_name
order by t.code


-- identify multiple code occurrences
select
  t.*,
  c.*,
  d.id as from_diag,
  u.id as from_unk
FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
  left join DiagnosisCcdCode d on d.id = c.id
  left join UnknownCcdCode u on u.id = c.id
where t.code in (select min(t.code)
                 FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
                 group by t.code
                 having count(t.code)>1)
order by t.code

-- update Unknown codes
;
with source as
(
    select
      u.id           as id,
      t.code         as code,
      t.display_name as name
    FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
      left join UnknownCcdCode u on u.id = c.id
    where c.id is not null and t.display_name <> c.display_name and u.id is not null
)
merge into UnknownCcdCode as target
using source
on (source.id = target.id)
when matched then update set target.display_name = source.name;

-- update DiagnosisCcd codes
;
with source as
(
    select
      d.id           as id,
      t.code         as code,
      t.display_name as name
    FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
      left join DiagnosisCcdCode d on d.id = c.id
    where c.id is not null and t.display_name <> c.display_name and d.id is not null
)
merge into DiagnosisCcdCode as target
using source
on (source.id = target.id)
when matched then update set target.display_name = source.name;

-- update DiagnosisSetup
;
with source as
(
    select
      d.diagnosis_setup_id as id,
      t.code               as code,
      t.display_name       as name
    FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
      left join DiagnosisCcdCode d on d.id = c.id
    where c.id is not null and t.display_name <> c.display_name and d.id is not null
)
merge into DiagnosisSetup as target
using source
on (source.id = target.id)
when matched then update set target.name = source.name;

-- at this point, this should give empty result
select
  t.*,
  c.*,
  d.id as from_diag,
  u.id as from_unk
FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
  left join DiagnosisCcdCode d on d.id = c.id
  left join UnknownCcdCode u on u.id = c.id
where c.id is not null and t.display_name <> c.display_name
order by t.code

-- now insert remaining into DiangnosisCcdCode
declare @diagnosisCcdInsert table(
  id           bigint not null,
  code         varchar(25),
  display_name varchar(max)
);
with source as
(
    select
      t.code         as code,
      t.display_name as display_name
    FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
    where c.id is null
)
merge into AnyCcdCode as target
using source
on (1 <> 1)
when not matched then INSERT DEFAULT VALUES
OUTPUT inserted.id, source.code, source.display_name into @diagnosisCcdInsert (id, code, display_name);

select *
from @diagnosisCcdInsert

Insert into DiagnosisCcdCode (id, diagnosis_setup_id, code, code_system, display_name, code_system_name)
  select
    s.id,
    null,
    s.code,
    '2.16.840.1.113883.6.90',
    s.display_name,
    'ICD-10-CM'
  from @diagnosisCcdInsert s

-- at this point, this should give empty result
select
  t.*,
  c.*,
  d.id as from_diag,
  u.id as from_unk
FROM #CcdCode_insert_temp t left join CcdCode c on c.code_system = '2.16.840.1.113883.6.90' and t.code = c.code
  left join DiagnosisCcdCode d on d.id = c.id
  left join UnknownCcdCode u on u.id = c.id
where c.id is null
order by t.code


select *
from CcdCode
where code_system = '2.16.840.1.113883.6.90'

-- commit after checking that everything is good
rollback TRANSACTION TransactionWithGos;
GO
