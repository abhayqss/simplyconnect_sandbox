SET XACT_ABORT ON
GO

BEGIN TRANSACTION TransactionWithGos;
GO

-- ================================================== utilities =======================================================
IF (OBJECT_ID('[dbo].[SplitString]') IS NOT NULL)
  DROP FUNCTION [dbo].[SplitString]
GO

CREATE FUNCTION [dbo].[SplitString]
(
  @List  nvarchar(MAX),
  @Delim nvarchar(255)
)
  RETURNS TABLE
    AS
    RETURN(SELECT [Value] /*stas what is [Value]*/
           FROM
             (
               SELECT [Value] = LTRIM(RTRIM(SUBSTRING(@List, [Number],
                                                      CHARINDEX(@Delim, @List + @Delim, [Number]) - [Number]))) /*search for delimeter in string*/
               FROM (SELECT Number = ROW_NUMBER() /*sequantial number of row in partition*/
                                         OVER (
                                           ORDER BY name )
                     FROM sys.all_columns) AS x   /*Shows the union of all columns belonging to user-defined objects and system objects.*/
               WHERE Number <= LEN(@List)
                 AND SUBSTRING(@Delim + @List, [Number], LEN(@Delim)) = @Delim
             ) AS y
          );
GO


IF (OBJECT_ID('[dbo].[SplitArrowNotatedListString]') IS NOT NULL)
  DROP FUNCTION [dbo].[SplitArrowNotatedListString]
GO

/*stas discuss function*/
CREATE FUNCTION [dbo].[SplitArrowNotatedListString]
(
  @List nvarchar(MAX)
)
  RETURNS TABLE
    AS
    RETURN(SELECT y.*
           FROM
             (
               SELECT
                 [first] = REPLACE(LTRIM(RTRIM((SELECT TOP (1) Value
                                        FROM dbo.SplitString(x.value, '->')))), CHAR(10) , ''),
                 [second] = LTRIM(RTRIM((SELECT r.Value /*stas как понять как обращаться к результатам сплит стринга */
                                         FROM dbo.SplitString(x.value, '->') r
                                         ORDER BY (SELECT NULL) /*stas */
                                           OFFSET 1 ROWS FETCH NEXT 1 ROW ONLY)))
               FROM (select LTRIM(RTRIM(Value)) value
                     from dbo.SplitString(@List, ';')) AS x
             ) AS y
          );
GO

-- ============================================= import csv ============================================================
if (OBJECT_ID('tempdb..#Marketplace_insert_temp') IS NOT NULL)
  DROP table #Marketplace_insert_temp

create table #Marketplace_insert_temp
(
  id                            bigint identity (1, 1)                not null,
  community_name                varchar(max) COLLATE database_default NOT NULL,
  community_type                varchar(max) COLLATE database_default,
  levels_of_care                varchar(max) COLLATE database_default,
  age_group                     varchar(max) COLLATE database_default,
  services_summary_description  varchar(512) COLLATE database_default,
  services_treatment_approaches varchar(max) COLLATE database_default,
  emergency_services            varchar(max) COLLATE database_default,
  language_services             varchar(max) COLLATE database_default,
  ancilliary_services           varchar(100) COLLATE database_default,
  allow_app_phr                 varchar(10) COLLATE database_default,
  email                         varchar(150) COLLATE database_default,
  secure_email                  varchar(100) COLLATE database_default,
  accepted_payment_types        varchar(max) COLLATE database_default
)
go

BULK INSERT #Marketplace_insert_temp
  FROM 'C:\import\Marketplace.csv'
  WITH ( FIELDTERMINATOR = '|', ROWTERMINATOR = '\n', FIRSTROW = 2)
GO

select *
from #Marketplace_insert_temp

declare @index int = 1;
declare @count int;

select @count = count(*)
from #Marketplace_insert_temp

print @count

while @index <= @count
BEGIN

  DECLARE @communityName varchar(max);
  set @communityName = NULL
  SELECT @communityName = community_name
  from #Marketplace_insert_temp
  where id = @index
  print 'Community Name = ' + @communityName

  declare @organizationId bigint
  set @organizationId = NULL
  select @organizationId = id
  from Organization
  where name = @communityName

  IF (@organizationId IS NULL)
    BEGIN
      RAISERROR ('Community with given name (%s) is absent.', 15, 1, @communityName);
      RETURN;
    end;

  declare @databaseId bigint
  select @databaseId = id
  from SourceDatabase
  where id = (SELECT com.database_id FROM Organization com where id = @organizationId)

  DECLARE @OutputTable table(
    [id] bigint
                            )
  -- ============================================== 1. Create MarketPlace ================================================
  DECLARE @marketplaceId BIGINT
  SET @marketplaceId = NULL

  INSERT INTO Marketplace (
    discoverable,
    allow_appointments,
    all_insurances_accepted,
    email,
    secure_email,
    summary,
    database_id,
    organization_id)
    OUTPUT Inserted.ID INTO @OutputTable([id])
  select
    1,
    CASE WHEN 'yes' = allow_app_phr
           THEN 1
           ELSE 0
         END,
    0,
    email,
    secure_email,
    services_summary_description,
    @databaseId,
    @organizationId
  FROM #Marketplace_insert_temp
  where id = @index

  SELECT TOP (1) @marketplaceId = [id]
  FROM @OutputTable;
  DELETE FROM @OutputTable WHERE 1 = 1;

-- ============================================== 2. Calculate and populate community types ============================
DECLARE @primaryFocuses table(
  primary_focus_id bigint not null
)

DELETE FROM @primaryFocuses WHERE 1 = 1

DECLARE @communityTypes table(
                               primary_focus_id bigint,
                               comunity_type_id bigint
                             )
DELETE FROM @communityTypes WHERE 1 = 1

insert into @communityTypes (primary_focus_id, comunity_type_id)

         select pf.id,
                --parsed.first,
                ct.id
                --,parsed.second
         from dbo.SplitArrowNotatedListString((SELECT community_type
                                               from #Marketplace_insert_temp
                                               where id = 1)) parsed
                LEFT JOIN PrimaryFocus pf on pf.display_name = parsed.first
                LEFT JOIN CommunityType ct on ct.primary_focus_id = pf.id and ct.display_name = parsed.second

select * from @communityTypes

IF EXISTS(select comunity_type_id
          from @communityTypes
          where comunity_type_id IS NULL)
  BEGIN
    RAISERROR ('Couldn''t process community type', 16, 1) WITH LOG;
    RETURN;
  end

insert into @primaryFocuses
select distinct primary_focus_id
from @communityTypes

INSERT INTO Marketplace_CommunityType (marketplace_id, community_type_id)
SELECT distinct
  @marketplaceId,
  comunity_type_id
from @communityTypes

  -- ============================================ 3. Level of care =======================================================

   INSERT INTO @OutputTable select lc.id
  from dbo.SplitString((Select levels_of_care
                        from #Marketplace_insert_temp
                        where id = @index), ';') parsed
         LEFT JOIN LevelOfCare lc on display_name = LTRIM(RTRIM(parsed.Value))
  where LTRIM(RTRIM(parsed.Value)) <> 'None'

  IF EXISTS(select id
            from @OutputTable
            where id IS NULL)
    BEGIN
      RAISERROR ('Couldn''t find some level of care.', 16, 1) WITH LOG;
      RETURN;
    end

  INSERT INTO Marketplace_LevelOfCare (marketplace_id, level_of_care_id)
  Select
    @marketplaceId,
    id
  from @OutputTable

  delete from @OutputTable where 1= 1

  -- ========================================== 4.Age Group ==============================================================
  insert into @OutputTable select ag.id
  from dbo.SplitString((Select age_group
                        from #Marketplace_insert_temp
                        where id = @index), ';') parsed
         LEFT JOIN AgeGroup ag
                   on 'All' = LTRIM(RTRIM(parsed.Value)) OR display_name = LTRIM(RTRIM(parsed.Value))

  IF EXISTS(select *
            from @OutputTable
            where id is null)
    BEGIN
      RAISERROR ('Couldn''t find age group.', 16, 1) WITH LOG;
      RETURN;
    end

  select * from @OutputTable

  INSERT INTO Marketplace_AgeGroup (marketplace_id, age_group_id)
  SELECT
    @marketplaceId,
    id
  from @OutputTable

  delete from @OutputTable where 1 = 1

  -- =========================================== 5. Services /Treatment Approaches =======================================

  DECLARE @approaches table(
                             primary_focus_id bigint,
                             approach_id      bigint
                           )
  DELETE from @approaches where 1 = 1

  insert into @approaches
  select
    pf.id,
    --  pf.display_name,
    sta.id
    --  sta.display_name
  from
    dbo.SplitArrowNotatedListString((SELECT REPLACE(services_treatment_approaches, '"', '')
                                     from #Marketplace_insert_temp
                                     where id = @index)) parsed
      LEFT JOIN PrimaryFocus pf on pf.display_name = parsed.first
      LEFT JOIN ServicesTreatmentApproach sta on sta.primary_focus_id = pf.id and sta.display_name = parsed.second

    select * from @approaches

  --     SELECT
  --       pf.id,
  --       pf.display_name,
  --       sta.id,
  --       sta.display_name
  --     from @approaches a LEFT JOIN PrimaryFocus pf on pf.id = a.primary_focus_id
  --       LEFT JOIN ServicesTreatmentApproach sta on sta.id = a.approach_id

  IF EXISTS(select approach_id
            from @approaches
            where approach_id IS NULL)
    BEGIN
      RAISERROR ('Couldn''t process Services Treatment Approaches.', 16, 1) WITH LOG;
      RETURN;
    end

  insert into @primaryFocuses
  select distinct primary_focus_id
  from @approaches except (select primary_focus_id
                           from @primaryFocuses)

  INSERT INTO Marketplace_ServicesTreatmentApproach (marketplace_id, services_treatment_approach_id)
  SELECT distinct
    @marketplaceId,
    approach_id
  from @approaches

  -- ===================================== 6. Emergency Services =================================
  DECLARE @emergencyServices VARCHAR(MAX)
  SET @emergencyServices = NULL
  select @emergencyServices = emergency_services from #Marketplace_insert_temp
  where id = @index

  IF @emergencyServices IS NOT NULL
  BEGIN
     insert into @OutputTable
      select es.id
      from dbo.SplitString(@emergencyServices, ';') parsed
             LEFT JOIN EmergencyService es
                       on 'All' = LTRIM(RTRIM(parsed.Value)) OR display_name = LTRIM(RTRIM(parsed.Value))


     IF EXISTS(select *
              from @OutputTable
              where id is null)
      BEGIN
        RAISERROR ('Couldn''t find emergency service.', 16, 1) WITH LOG;
        RETURN;
      end

     INSERT INTO Marketplace_EmergencyService (marketplace_id, emergency_service_id)
     SELECT @marketplaceId,
           id
     from @OutputTable

     delete
     from @OutputTable
     where 1 = 1
  END

 -- ======================================== 7. Language Services =========================================================

   INSERT INTO @OutputTable select ls.id
   from dbo.SplitString((Select REPLACE(language_services, '"', '')
                         from #Marketplace_insert_temp
                         where id = @index), ';') parsed
          LEFT JOIN LanguageService ls on display_name = LTRIM(RTRIM(parsed.Value))
   where LTRIM(RTRIM(parsed.Value)) <> 'None'

   select * from @OutputTable

   IF EXISTS(select id
             from @OutputTable
             where id IS NULL)
     BEGIN
       RAISERROR ('Couldn''t find language service, row (%d)', 16, 1, @index) WITH LOG;
       RETURN;
     end

   INSERT INTO Marketplace_LanguageService (marketplace_id, language_service_id)
   Select
     @marketplaceId,
     id
   from @OutputTable

   delete from @OutputTable WHERE 1 = 1

   SELECT * FROM Marketplace_LanguageService

  -- ======================================= 8. AncillaryService ============================================================

   DECLARE @ancillaryServices VARCHAR(MAX)
   SET @ancillaryServices = NULL
   select @ancillaryServices = ancilliary_services from #Marketplace_insert_temp
   where id = @index

   IF @ancillaryServices IS NOT NULL
   BEGIN

     INSERT INTO @OutputTable select aas.id
     from dbo.SplitString(@ancillaryServices, ';') parsed
            LEFT JOIN AncillaryService aas on display_name = LTRIM(RTRIM(parsed.Value))
     where LTRIM(RTRIM(parsed.Value)) <> 'None'

     IF EXISTS(select id
               from @OutputTable
               where id IS NULL)
       BEGIN
         RAISERROR ('Couldn''t find AncillaryService', 16, 1) WITH LOG;
         RETURN;
       end

     INSERT INTO Marketplace_AncillaryService (marketplace_id, ancillary_service_id)
     Select
       @marketplaceId,
       id
     from @OutputTable

     delete from @OutputTable where 1 = 1

     select * from Marketplace_AncillaryService
   END

  -- ================================================ 9. PaymentTypes =======================================================

  DECLARE @payments table(
                           network_id bigint,
                           plan_id    bigint,
                           had_plan   bit
                         )
  DELETE FROM @payments where 1 = 1

  insert into @payments
  select
      n.id,
      --parsed.first,
      p.id,
      --parsed.second,
      CASE WHEN parsed.second is not NULL
             THEN 1
           ELSE 0 END had_plans
    from
       dbo.SplitArrowNotatedListString((SELECT REPLACE(REPLACE(accepted_payment_types, '???', '–'),
                                                       '"', '')
                                        from #Marketplace_insert_temp
                                        where id = @index)) parsed
       LEFT JOIN InNetworkInsurance n on parsed.first = 'All' or n.display_name = parsed.first
       LEFT JOIN InsurancePlan p
                 on p.in_network_insurance_id = n.id and (parsed.first = 'All' or p.display_name = parsed.second)

  SELECT * FROM @payments

   IF EXISTS(select *
             from @payments
             where plan_id IS NULL and had_plan = 1)
     BEGIN
       RAISERROR ('Couldn''t process Payment Type, row (%d)', 16, 1, @index) WITH LOG
       RETURN;
     end


   INSERT INTO Marketplace_InNetworkInsurance (marketplace_id, in_network_insurance_id)
   SELECT DISTINCT
     @marketplaceId,
     network_id
   FROM @payments

   INSERT INTO Marketplace_InsurancePlan (marketplace_id, insurance_plan_id)
   SELECT DISTINCT
     @marketplaceId,
     plan_id
   FROM @payments
   where plan_id is not null

   IF (OBJECT_ID('Marketplace_InNetworkInsurance_InsurancePlan') IS NOT NULL)
     INSERT INTO Marketplace_InNetworkInsurance_InsurancePlan (marketplace_id, in_network_insurance_id, insurance_plan_id)
     SELECT DISTINCT
       @marketplaceId,
       network_id,
       plan_id
     from @payments

  -- ================================================ 10. Primary Focuses ====================================================
  INSERT INTO Marketplace_PrimaryFocus (marketplace_id, primary_focus_id)
  select distinct
    @marketplaceId,
    primary_focus_id
  from @primaryFocuses

  set @index = @index + 1
print 'End'
END;
commit TRANSACTION TransactionWithGos;
GO

---------------------------------------------------------------------------------------------

-- declare @allNetworks varchar(max)
-- set @allNetworks = 'Aetna -> Basic HMO (available in CA only);Aetna -> Behavioral Health Program;Aetna -> Bronze HNOption;Aetna -> Elect Choice EPO;Aetna -> Freedom 10;Aetna -> Freedom 15;Aetna -> Freedom 1525;Aetna -> Freedom 2030;Aetna -> Freedom 2035;Aetna -> HMO;Aetna -> HMO (available in CA and NV only);Aetna -> Leap Basic ? Banner;Aetna -> Leap Everyday;Aetna -> Liberty;Aetna -> Managed Choice (Open Access) on the Altius Network;Aetna -> Medicare Value Plan (HMO);Aetna -> Minimum Basic Plan;Aetna -> NYC Community Plan;Blue Cross and Blue Shield of Illinois;  Blue Cross and Blue Shield of Louisiana;  Blue Cross Blue Shield Massachusetts, Medicare Advantage;  Blue Cross Blue Shield of Massachusetts;Blue Cross Blue Shield of North Dakota;Blue Shield of CA;  CareFirst BlueCross BlueShield;Cash or self-payment;  Medicaid ->  Fee For Service (FFS);Medicaid -> Managed Care;Medicaid -> Managed Long Term Services and Supports (MLTSS);Medicaid -> Medicaid;Medicaid -> Medically Needy / Share of Cost;'
-- -- --
-- select
--   distinct
--   n.id,
--   parsed.first,
--   p.id,
--   parsed.second,
--   CASE WHEN parsed.second is not NULL
--     THEN 1
--   ELSE 0 END had_plans
-- from
--       dbo.SplitArrowNotatedListString(REPLACE(REPLACE(@allNetworks, CHAR(13), ''), CHAR(10), '')) parsed
--   LEFT JOIN InNetworkInsurance n on parsed.first = 'All' or n.display_name = LTRIM(RTRIM(parsed.first))
--   LEFT JOIN InsurancePlan p
--     on p.in_network_insurance_id = n.id and (parsed.first = 'All' or p.display_name = parsed.second)


-- declare @allcomTyp varchar(max)
-- set @allcomTyp = 'Acute Care -> Hospital;
-- Community Residential Services -> Adult Foster Care;
-- Community Residential Services -> Group Home;
-- Community Residential Services -> Host Home;
-- Community Residential Services -> Semi-Independent Living;
-- Community Residential Services -> Other;
-- Home and Community Based Services - Social -> Community_Organization;
-- Home and Community Based Services - Social -> Other;
-- Home and Community Based Services - Health -> Ambulatory Care;
-- Home and Community Based Services - Health -> Home Health;
-- Home and Community Based Services - Health -> Hospice;
-- Home and Community Based Services - Health -> Other;
-- Mental/Behavioral Health -> Inpatient;
-- Mental/Behavioral Health -> Outpatient/Ambulatory care;
-- Mental/Behavioral Health ->  Other;
-- Pharmacy -> Clinical Pharmacy;
-- Pharmacy -> Closed-Door;
-- Pharmacy -> Community Pharmacy;
-- Pharmacy -> Consultant Pharmacy;
-- Pharmacy -> Hospital Pharmacy;
-- Pharmacy -> Long-Term Care;
-- Post Acute Care -> Assisted Living;
-- Post Acute Care -> In-patient Rehabilitation Center;
-- Post Acute Care -> Independent Living;
-- Post Acute Care -> Long-Term Care Facility;
-- Post Acute Care -> Skilled Nursing Facility;
-- Post Acute Care -> Transitional Care Unit;'
--
--
-- select
--   pf.id,
--   ct.id,
--   parsed.first,
--   parsed.second
-- from
--       dbo.SplitArrowNotatedListString(REPLACE(REPLACE(@allcomTyp, CHAR(13), ''), CHAR(10), '')) parsed
--   LEFT JOIN PrimaryFocus pf on pf.display_name = parsed.first
--   LEFT JOIN CommunityType ct on ct.primary_focus_id = pf.id and ct.display_name = parsed.second
--
--
-- declare @levOfCar varchar(max)
-- set @levOfCar = 'Inpatient Hospitalization;
-- Outpatient Treatment;
-- Partial Hospitalization (Day Treatment);
-- Residential Treatment;'
--
-- select
--   lc.id,
--   Value
-- from dbo.SplitString(REPLACE(REPLACE(@levOfCar, CHAR(13), ''), CHAR(10), ''), ';') parsed
--   LEFT JOIN LevelOfCare lc on display_name = LTRIM(RTRIM(parsed.Value))
-- where LTRIM(RTRIM(parsed.Value)) <> 'None'
--
-- declare @allSta varchar(max)
-- set @allSta = 'Acute Care -> Crisis stabilization;
-- Acute Care -> Critical care;
-- Acute Care -> Emergency care;
-- Acute Care -> Intensive care;
-- Acute Care -> Prenatal care;
-- Acute Care -> Surgery;
-- Acute Care -> Urgent care;
-- Community Residential Services -> Activities of Daily Living support;
-- Community Residential Services -> Medication pass;
-- Community Residential Services -> Recreational Activities;
-- Community Residential Services -> Transportation;
-- Home and Community Based Services - Social -> Adult Day Programs;
-- Home and Community Based Services - Social -> Financial Services;
-- Home and Community Based Services - Social -> Home delivered meals;
-- Home and Community Based Services - Social -> Home modifications;
-- Home and Community Based Services - Social -> Home repairs/safety assessments;
-- Home and Community Based Services - Social -> Homemaking/CHORE;
-- Home and Community Based Services - Social -> Personal Care;
-- Home and Community Based Services - Health -> Alzheimer/Dementia Care;
-- Home and Community Based Services - Health -> Assistive Technology;
-- Home and Community Based Services - Health -> Care Coordination;
-- Home and Community Based Services - Health -> Case Management;
-- Home and Community Based Services - Health -> Chronic Care Management (CCM);
-- Home and Community Based Services - Health -> Dietary Management;
-- Home and Community Based Services - Health -> Durable Medical Equipment;
-- Home and Community Based Services - Health -> Respite;
-- Home and Community Based Services - Health -> Skilled Nursing;
-- Home and Community Based Services - Health -> Therapies: Occupational, Speech, Physical;
-- Home and Community Based Services - Health -> Transitional Care Management (TCM);
-- Mental/Behavioral Health -> Adult Rehabilitative Mental Health;
-- Mental/Behavioral Health -> Art Therapy;
-- Mental/Behavioral Health -> Behavior Modification;
-- Mental/Behavioral Health -> Behavioral Health Home (BHH);
-- Mental/Behavioral Health -> Behavioral Health Integration (BHI);
-- Mental/Behavioral Health -> Case Management;
-- Mental/Behavioral Health -> Cognitive Behavioral Health Therapy;
-- Mental/Behavioral Health -> Colbert Consent Decree;
-- Mental/Behavioral Health -> Couples and family counseling;
-- Mental/Behavioral Health -> Crisis care;
-- Mental/Behavioral Health -> Dialect Behavioral Therapy (DBT);
-- Mental/Behavioral Health -> Dual diagnosis specialist;
-- Mental/Behavioral Health -> Group Therapy;
-- Mental/Behavioral Health -> Music Therapy;
-- Mental/Behavioral Health -> Psychiatry;
-- Mental/Behavioral Health -> Psychology;
-- Mental/Behavioral Health -> Psychotropic Medication Monitoring;
-- Mental/Behavioral Health -> Semi- Independent Living Skills (SILS);
-- Mental/Behavioral Health -> Services (ARMHS);
-- Mental/Behavioral Health -> Substance Abuse;
-- Pharmacy -> Behavioral Health Integration (BHI);
-- Pharmacy -> Chronic Care Management (CCM);
-- Pharmacy -> Compounding;
-- Pharmacy -> Comprehensive Medication Reviews;
-- Pharmacy -> Drug synchronization;
-- Pharmacy -> Medicare Annual Wellness visits;
-- Pharmacy -> Medication Therapy Management;
-- Pharmacy -> Prescription filling and refilling;
-- Pharmacy -> Remote patient monitoring;
-- Pharmacy -> Telehealth;
-- Pharmacy -> Transitional Care Management (TCM);
-- Post Acute Care -> Activities of Daily Living;
-- Post Acute Care -> Alzheimer''s/Dementia Care;
-- Post Acute Care -> Assistive Technology;
-- Post Acute Care -> Bariatric services;
-- Post Acute Care -> Medication administration;
-- Post Acute Care -> Memory care unit;
-- Post Acute Care -> Recreational activities;
--  Post Acute Care -> Sliding Scale Insulin;
-- Post Acute Care -> Therapies: Occupational, Speech, Physical;
-- Post Acute Care -> Transportation;
-- Post Acute Care -> Ventilator;'
--
-- select
--   pf.id,
--   parsed.first,
--   sta.id,
--   parsed.second
-- from
--       dbo.SplitArrowNotatedListString(REPLACE(REPLACE(@allSta, CHAR(13), ''), CHAR(10), '')) parsed
--   LEFT JOIN PrimaryFocus pf on pf.display_name = parsed.first
--   LEFT JOIN ServicesTreatmentApproach sta on sta.primary_focus_id = pf.id and sta.display_name = parsed.second
--
-- declare @allLangs varchar(max)
-- set @allLangs = 'Hmong;
-- Native American Indian or Alaska Native languages;
-- Russian;
-- Services for the deaf and hard of hearing;
-- Somalian;
-- Spanish;
-- Other languages;'
-- select
--   ls.id,
--   parsed.Value
-- from dbo.SplitString(REPLACE(REPLACE(@allLangs, CHAR(13), ''), CHAR(10), ''), ';') parsed
--   LEFT JOIN LanguageService ls on display_name = LTRIM(RTRIM(parsed.Value))
-- where LTRIM(RTRIM(parsed.Value)) <> 'None'

