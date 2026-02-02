UPDATE [dbo].[ServicesTreatmentApproach] SET can_additional_clinical_info_be_shared = 0 WHERE code = 'Adult_Day_Programs_Home_and_Community_Based_Services_Social';
UPDATE [dbo].[ServicesTreatmentApproach] SET can_additional_clinical_info_be_shared = 1 WHERE code = 'Adult_Protective_Services_Home_and_Community_Based_Services_Social';
UPDATE [dbo].[ServicesTreatmentApproach] SET can_additional_clinical_info_be_shared = 0 WHERE code = 'Burial_and_Cremation_Services_Home_and_Community_Based_Services_Social';
GO