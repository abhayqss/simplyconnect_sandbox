BEGIN
  DECLARE @primary_focus_id bigint;

  SELECT @primary_focus_id = id FROM [dbo].[PrimaryFocus] WHERE code ='Home_and_Community_Based_Services_Health';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Dental care', 'Dental_care', @primary_focus_id);

  UPDATE [dbo].[ServicesTreatmentApproach] SET display_name = 'Safety & education services'
  WHERE code = 'Educational_Program_8';
  UPDATE [dbo].[ServicesTreatmentApproach] SET display_name = 'Grocery Shopping/Savings'
  WHERE code = 'Grocery_Shopping_8';
END;


DECLARE @insert TABLE(
  primary_focus_name varchar(255),
  display_name       varchar(255)
)

INSERT INTO @insert (primary_focus_name, display_name) VALUES
('Community Residential Services', 'Passport'),
('Home and Community Based Services - Health', 'Dental Services'),
('Home and Community Based Services - Health', 'Minor Medical Supplies'),
('Home and Community Based Services - Health', 'Vaccine Assistance'),
('Home and Community Based Services - Health', 'Condition Related Education / Prevention / Helpline'),
('Home and Community Based Services - Social', 'Adult Protective Services'),
('Home and Community Based Services - Social', 'Housing Financial Assistance'),
('Home and Community Based Services - Social', 'Financial Hotline'),
('Home and Community Based Services - Social', 'Condition Related Financial Assistance'),
('Home and Community Based Services - Social', 'WIC Nutrition Programs'),
('Home and Community Based Services - Social', 'Lifelines'),
('Home and Community Based Services - Social', 'Fitness & Health'),
('Home and Community Based Services - Social', 'School Supplies'),
('Home and Community Based Services - Social', 'Energy Assistance'),
('Home and Community Based Services - Social', 'Financial assistance / Subsidy'),
('Home and Community Based Services - Social', 'Health / Wellness / Helpline'),
('Home and Community Based Services - Social', 'Passport'),
('Mental/Behavioral Health', 'Condition Related Education / Prevention / Helpline'),
('Post Acute Care', 'Minor Medical Supplies'),
('Post Acute Care', 'Service Animal'),
('Transportation', 'NEMT/Paratransit/Demand Response'),
('Transportation', 'Public Transportation'),
('Transportation', 'Senior & Disabled Reduced Fares'),
('Transportation', 'Senior & Disabled Ride Service'),
('Home and Community Based Services - Governmental', 'Energy Assistance'),
('Home and Community Based Services - Governmental', 'Housing Financial Assistance'),
('Home and Community Based Services - Governmental', 'Financial Hotline'),
('Home and Community Based Services - Governmental', 'Lifelines'),
('Home and Community Based Services - Governmental', 'Condition Related Education / Prevention / Helpline'),
('Home and Community Based Services - Governmental', 'Low Income Subsidy'),
('Home and Community Based Services - Governmental', 'Medicare Savings Program - Partial'),
('Home and Community Based Services - Governmental', 'Medicare Savings Program - Full'),
('Home and Community Based Services - Governmental', 'Property Tax Assistance'),
('Home and Community Based Services - Governmental', 'Condition Related Financial Assistance'),
('Home and Community Based Services - Governmental', 'Passport'),
('Government', 'Food Pantry'),
('Government', 'Energy Assistance')

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN PrimaryFocus pf ON i.primary_focus_name = pf.display_name
  WHERE pf.id IS NULL) > 0
RAISERROR ('Primary focus not found', 15, 1);

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN PrimaryFocus pf ON i.primary_focus_name = pf.display_name
  JOIN ServicesTreatmentApproach sta ON sta.display_name = i.display_name AND sta.primary_focus_id = pf.id
  WHERE pf.id IS NULL) > 0
RAISERROR ('Primary focus for service not found', 15, 1);

WITH cte AS (
  SELECT
  pf.id                                                                   AS pf_id,
  i.display_name                                                          AS name,
  dbo.build_code_from_name(i.display_name) + '_' + CAST(pf.id AS varchar) AS code
  FROM @insert i
  JOIN PrimaryFocus pf ON i.primary_focus_name = pf.display_name
)
merge INTO ServicesTreatmentApproach sta
USING cte
ON 1 <> 1
WHEN NOT matched THEN INSERT (display_name, code, primary_focus_id) VALUES (cte.name, cte.code, cte.pf_id);
GO