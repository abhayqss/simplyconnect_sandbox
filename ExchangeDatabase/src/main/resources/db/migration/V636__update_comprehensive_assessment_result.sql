declare @comprehensive_id bigint;

select @comprehensive_id = id from Assessment where code = 'COMPREHENSIVE';

UPDATE [dbo].[ResidentAssessmentResult]
   SET [json_result] = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(json_result, '"Phone number"', '"demographics_emergencyContact1_phoneNumber"'),'"First name1"','"demographics_emergencyContact1_firstName"'),'"Last Name1"','"demographics_emergencyContact1_lastName"'),'"Address"','"demographics_emergencyContact1_address"'),'"Street1"','"demographics_emergencyContact1_address_street"'),'"City1"','"demographics_emergencyContact1_address_city"'),'"State1"','"demographics_emergencyContact1_address_state"'),'"Zip Code1"','"demographics_emergencyContact1_address_zipCode"')
WHERE assessment_id = @comprehensive_id

UPDATE [dbo].[ResidentAssessmentResult]
   SET [json_result] = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(json_result, '"demographics.emergencyContact2.phoneNumber"', '"demographics_emergencyContact2_phoneNumber"'),'"demographics.emergencyContact2.firstName"','"demographics_emergencyContact2_firstName"'),'"demographics.emergencyContact2.lastName"','"demographics_emergencyContact2_lastName"'),'"demographics.emergencyContact2.address"','"demographics_emergencyContact2_address"'),'"demographics.emergencyContact2.address.street"','"demographics_emergencyContact2_address_street"'),'"demographics.emergencyContact2.address.city"','"demographics_emergencyContact2_address_city"'),'"demographics.emergencyContact2.address.state"','"demographics_emergencyContact2_address_state"'),'"demographics.emergencyContact2.address.zipCode"','"demographics_emergencyContact2_address_zipCode"')
WHERE assessment_id = @comprehensive_id

UPDATE [dbo].[ResidentAssessmentResult]
   SET [json_result] = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(json_result, '"demographics emergencyContact2 phoneNumber"', '"demographics_emergencyContact2_phoneNumber"'),'"demographics emergencyContact2 firstName"','"demographics_emergencyContact2_firstName"'),'"demographics emergencyContact2 lastName"','"demographics_emergencyContact2_lastName"'),'"demographics emergencyContact2 address"','"demographics_emergencyContact2_address"'),'"demographics emergencyContact2 address street"','"demographics_emergencyContact2_address_street"'),'"demographics emergencyContact2 address city"','"demographics_emergencyContact2_address_city"'),'"demographics emergencyContact2 address state"','"demographics_emergencyContact2_address_state"'),'"demographics emergencyContact2 address zipCode"','"demographics_emergencyContact2_address_zipCode"')
WHERE assessment_id = @comprehensive_id

UPDATE [dbo].[ResidentAssessmentResult]
   SET [json_result] = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(json_result, '"demographics.emergencyContact3.phoneNumber"', '"demographics_emergencyContact3_phoneNumber"'),'"demographics.emergencyContact3.firstName"','"demographics_emergencyContact3_firstName"'),'"demographics.emergencyContact3.lastName"','"demographics_emergencyContact3_lastName"'),'"demographics.emergencyContact3.address"','"demographics_emergencyContact3_address"'),'"demographics.emergencyContact3.address.street"','"demographics_emergencyContact3_address_street"'),'"demographics.emergencyContact3.address.city"','"demographics_emergencyContact3_address_city"'),'"demographics.emergencyContact3.address.state"','"demographics_emergencyContact3_address_state"'),'"demographics.emergencyContact3.address.zipCode"','"demographics_emergencyContact3_address_zipCode"')
WHERE assessment_id = @comprehensive_id

UPDATE [dbo].[ResidentAssessmentResult]
   SET [json_result] = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(json_result, '"demographics emergencyContact3 phoneNumber"', '"demographics_emergencyContact3_phoneNumber"'),'"demographics emergencyContact3 firstName"','"demographics_emergencyContact3_firstName"'),'"demographics emergencyContact3 lastName"','"demographics_emergencyContact3_lastName"'),'"demographics emergencyContact3 address"','"demographics_emergencyContact3_address"'),'"demographics emergencyContact3 address street"','"demographics_emergencyContact3_address_street"'),'"demographics emergencyContact3 address city"','"demographics_emergencyContact3_address_city"'),'"demographics emergencyContact3 address state"','"demographics_emergencyContact3_address_state"'),'"demographics emergencyContact3 address zipCode"','"demographics_emergencyContact3_address_zipCode"')
WHERE assessment_id = @comprehensive_id
GO