if object_id('build_code_from_name') is not null
  drop function build_code_from_name;
go

create FUNCTION [dbo].[build_code_from_name](
  @name VARCHAR(255)
)
  RETURNS VARCHAR(255)
AS BEGIN
  -- Remove non-digits, non-letters, non-underscore from string
  WHILE patindex('%[^0-9a-z_]%', @name) > 0
    SET @name = replace(@name, substring(@name, patindex('%[^0-9a-z_]%', @name), 1), '_');

  --remove multiple underscores
  while patindex('%[_][_]%', @name) > 0
    SET @name = replace(@name, substring(@name, patindex('%[_][_]%', @name), 2), '_');

  --remove underscore in the end if needed
  if @name like '%[_]'
    SET @name = left(@name, LEN(@name) - 1);

  RETURN @name;
END;
go


update Agegroup
set display_order = 5
where display_order = 3
insert into AgeGroup (display_name, display_order) values ('Seniors (55 or older)', 3), ('Seniors (60 and older)', 4)
go

insert into LevelOfCare (display_name) values
  ('Independent/assisted- some skilled care'),
  ('Independent'),
  ('Skilled care'),
  ('Assistance'),
  ('Transportation'),
  ('Senior Center and Nutrition Site'),
  ('Hospice services'),
  ('Services for Hearing Impaired'),
  ('Podiatry Services'),
  ('Legal Services'),
  ('Resource Advocacy'),
  ('Home Health'),
  ('VA Center'),
  ('Eye Care'),
  ('PT'),
  ('OT'),
  ('ST '),
  ('Nursing'),
  ('Aides'),
  ('Medication'),
  ('Organization/Immunizations')
go

insert into LanguageService (display_name) values
  ('English'),
  ('Translation services')
go

insert into EmergencyService (display_name) values
  ('Emergency Rent and Utility payment'),
  ('Financial')
go

insert into InNetworkInsurance (display_name, code) values
  ('Aetna Better Health (My Care Ohio)', 'AETNA_BETTER_HEALTH_OHIO'),
  ('American Medical Security/United Healthcare', 'AMERICAN_MEDICAL_SECURITY'),
  ('BCE Emergis/Multiplan', 'BCE_EMERGIS_MULTIPLAN'),
  ('Beach Street/Multiplan', 'BEACH_STREET_MULTIPLAN'),
  ('Definity Health/United HealthCare', 'DEFINITY_HEALTH_UNITED_HEALTHCARE'),
  ('Golden Rule/United HealthCare', 'GOLDEN_RULE_UNITED_HEALTHCARE'),
  ('Health Plan of the Upper Ohio Valley/The Health Plan', 'HEALTH_PLAN_UPPER_OHIO_VALLEY'),
  ('HomeTown Health Network/The Health Plan', 'HOMETOWN_HEALTH_NETWORK'),
  ('Private Healthcare Systems (PHCS)/MultiPlan', 'PRIVATE_HEALTHCARE_SYSTEMS_PHCS_MULTIPLAN'),
  ('Secure Horizons/United HealthCare', 'SECURE_HORIZONS_UNITED_HEALTHCARE')
go

insert into AncillaryService (display_name)
values ('IV Fusion')
go

insert into PrimaryFocus (display_name, code)
values
  ('Home and Community Based Services - Governmental', 'Home_and_Community_Based_Services_Governmental'),
  ('Government', 'Government'),
  ('Transportation', 'Transportation')
go

declare @insert table(
  primary_focus_name varchar(255),
  display_name       varchar(255)
)

insert into @insert (primary_focus_name, display_name) values
  ('Transportation', 'Transportation'),
  ('Home and Community Based Services - Governmental', 'Crisis Assistance'),
  ('Home and Community Based Services - Social', 'Public Assistance'),
  ('Home and Community Based Services - Social', 'Transportation'),
  ('Government', 'Emergency Assistance'),
  ('Government', 'Veteran Assistance')


if (select count(*)
    from @insert i
      left join PrimaryFocus pf on i.primary_focus_name = pf.display_name
    where pf.id is null) > 0
  RAISERROR ('Primary focus for community type not found', 15, 1);

if (select count(*)
    from @insert i
      left join PrimaryFocus pf on i.primary_focus_name = pf.display_name
      join CommunityType ct on i.display_name = ct.display_name and ct.primary_focus_id = pf.id) > 0
  RAISERROR ('Community type already exists', 15, 1);


with cte as (
    select
      pf.id                                                                   as pf_id,
      i.display_name                                                          as name,
      dbo.build_code_from_name(i.display_name) + '_' + cast(pf.id as varchar) as code
    from @insert i
      join PrimaryFocus pf on i.primary_focus_name = pf.display_name
)
merge into CommunityType ct
using cte
on 1 <> 1
when not matched then insert (display_name, code, primary_focus_id) VALUES (cte.name, cte.code, cte.pf_id);
go

declare @insert table(
  primary_focus_name varchar(255),
  display_name       varchar(255)
)
delete from @insert;


insert into @insert (primary_focus_name, display_name) values
  ('Community Residential Services', 'Care Coordination'),
  ('Community Residential Services', 'Dietary Management'),
  ('Community Residential Services', 'Skilled Nursing'),
  ('Community Residential Services', 'Case Management'),
  ('Community Residential Services', 'Chronic Care Management (CCM)'),
  ('Community Residential Services', 'Transitional Care Management (TCM)'),
  ('Community Residential Services', 'Remote Patient Monitoring (RPM)'),
  ('Transportation', 'Transportation'),
  ('Home and Community Based Services - Health', 'Mental health services'),
  ('Home and Community Based Services - Health', 'Assistance'),
  ('Home and Community Based Services - Health', 'Hospice'),
  ('Home and Community Based Services - Health', 'Personal care'),
  ('Home and Community Based Services - Governmental', 'Financial Services'),
  ('Home and Community Based Services - Governmental', 'Home delivered meals'),
  ('Home and Community Based Services - Governmental', 'Home modifications'),
  ('Home and Community Based Services - Governmental', 'Home repairs/safety assessments'),
  ('Home and Community Based Services - Governmental', 'Homemaking/CHORE'),
  ('Home and Community Based Services - Governmental', 'Personal Care'),
  ('Home and Community Based Services - Governmental', 'Care Coordination'),
  ('Home and Community Based Services - Governmental', 'Crisis Intervention'),
  ('Home and Community Based Services - Social', 'Transportation'),
  ('Home and Community Based Services - Social', 'Socialization'),
  ('Home and Community Based Services - Social', 'Educational Program'),
  ('Home and Community Based Services - Social', 'Senior Center'),
  ('Home and Community Based Services - Social', 'Health Screening'),
  ('Home and Community Based Services - Social', 'Dining Site'),
  ('Home and Community Based Services - Social', 'Grocery Shopping'),
  ('Home and Community Based Services - Social', 'Home-Delivered Meals'),
  ('Home and Community Based Services - Social', 'Nutrition Education'),
  ('Home and Community Based Services - Social', 'Caregiver Support'),
  ('Home and Community Based Services - Social', 'Public assistance'),
  ('Home and Community Based Services - Social', 'Employment and Training'),
  ('Home and Community Based Services - Social', 'Care Coordination'),
  ('Home and Community Based Services - Social', 'Crisis Intervention'),
  ('Home and Community Based Services - Social', 'Adult Day Programs'),
  ('Home and Community Based Services - Social', 'Legal Services'),
  ('Government', 'Crisis Services'),
  ('Government', 'Disaster Relief'),
  ('Government', 'Blood Donation'),
  ('Government', 'International Services'),
  ('Government', 'Military Services'),
  ('Government', 'Training and Certificate'),
  ('Government', 'Care Coordination'),
  ('Government', 'Case management'),
  ('Government', 'Mental health services'),
  ('Government', 'Veteran public assistance'),
  ('Government', 'Transportation'),
  ('Government', 'Financial services'),
  ('Mental/Behavioral Health', 'Mental health counseling'),
  ('Pharmacy', 'Medication delivery')

if (select count(*)
    from @insert i
      left join PrimaryFocus pf on i.primary_focus_name = pf.display_name
    where pf.id is null) > 0
  RAISERROR ('Primary focus for service not found', 15, 1);

if (select count(*)
    from @insert i
      left join PrimaryFocus pf on i.primary_focus_name = pf.display_name
      join ServicesTreatmentApproach sta on sta.display_name = i.display_name and sta.primary_focus_id = pf.id
    where pf.id is null) > 0
  RAISERROR ('Primary focus for service not found', 15, 1);


with cte as (
    select
      pf.id                                                                   as pf_id,
      i.display_name                                                          as name,
      dbo.build_code_from_name(i.display_name) + '_' + cast(pf.id as varchar) as code
    from @insert i
      join PrimaryFocus pf on i.primary_focus_name = pf.display_name
)
merge into ServicesTreatmentApproach sta
using cte
on 1 <> 1
when not matched then insert (display_name, code, primary_focus_id) VALUES (cte.name, cte.code, cte.pf_id);
go
