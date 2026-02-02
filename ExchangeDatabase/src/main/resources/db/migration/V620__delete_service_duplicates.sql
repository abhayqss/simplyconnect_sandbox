DECLARE @wrong_service_id bigint;
SELECT @wrong_service_id = id FROM [dbo].[ServicesTreatmentApproach] WHERE code ='Home_Delivered_Meals_8';
DECLARE @correct_service_id bigint;
SELECT @correct_service_id = id FROM [dbo].[ServicesTreatmentApproach] WHERE code ='Home_delivered_meals';

UPDATE [dbo].[Marketplace_ServicesTreatmentApproach] SET services_treatment_approach_id = @correct_service_id
WHERE services_treatment_approach_id = @wrong_service_id;

DELETE FROM [dbo].[ServicesTreatmentApproach] WHERE id = @wrong_service_id;
GO