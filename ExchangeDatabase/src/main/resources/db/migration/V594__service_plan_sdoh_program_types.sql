
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ZCode](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NOT NULL,
	[description] [varchar](250) NULL,
 CONSTRAINT [PK_ZCode] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[ProgramType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](250) NOT NULL,
	[display_name] [varchar](250) NOT NULL,
	[need_type] [varchar](100) NOT NULL,
 CONSTRAINT [PK_ProgramType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[ProgramSubType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](250) NOT NULL,
	[display_name] [varchar](250) NOT NULL,
	[zcode_id] [bigint] NOT NULL,
	[program_type_id] [bigint] NOT NULL,
 CONSTRAINT [PK_ProgramSubType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ProgramSubType]  WITH CHECK ADD  CONSTRAINT [FK_ProgramSubType_ProgramType] FOREIGN KEY([program_type_id])
REFERENCES [dbo].[ProgramType] ([id])
GO

ALTER TABLE [dbo].[ProgramSubType] CHECK CONSTRAINT [FK_ProgramSubType_ProgramType]
GO

ALTER TABLE [dbo].[ProgramSubType]  WITH CHECK ADD  CONSTRAINT [FK_ProgramSubType_ZCode] FOREIGN KEY([zcode_id])
REFERENCES [dbo].[ZCode] ([id])
GO

ALTER TABLE [dbo].[ProgramSubType] CHECK CONSTRAINT [FK_ProgramSubType_ZCode]
GO

SET ANSI_PADDING OFF
GO

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('CHRONIC_DISEASE_MANAGEMENT','Chronic Disease Management','HEALTH_STATUS');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HEALTH_MANAGEMENT_SERVICES','Health Management Services','HEALTH_STATUS');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('TRANSPORTATION_SERVICES','Transportation Services','HEALTH_STATUS');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('TRANSPORTATION_SERVICES','Transportation Services','TRANSPORTATION');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('NUTRITION','Nutrition','NUTRITION_SECURITY');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('ADULT_CARE_SERVICES','Adult Care Services','SUPPORT');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('COUNSELING_INTERVENTION_TREATMENT','Counseling/Intervention & Treatment','SUPPORT');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HOME_GOODS_PERSONAL_CARE_ASSISTANCE','Home Goods/Personal Care Assistance','SUPPORT');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('CHILDREN_SERVICES','Children Services','OTHER');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('FAMILY_HELP_SERVICES','Family Help Services','OTHER');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('CHILDREN_SERVICES','Children Services','EDUCATION_TASK');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('CHRONIC_DISEASE_MANAGEMENT','Chronic Disease Management','EDUCATION_TASK');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('DIY_WORKSHOPS_LEARNING_SESSIONS','DIY/Workshops/Learning Sessions','EDUCATION_TASK');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('ENERGY_ASSISTANCE','Energy Assistance','HOUSING_ONLY');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HOME_REPAIR','Home Repair','HOUSING_ONLY');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HOUSING_ASSISTANCE','Housing Assistance','HOUSING_ONLY');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('MONEY','Money','HOUSING_ONLY');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('EMPLOYMENT_SERVICES','Employment Services','EMPLOYMENT');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HEALTH_MANAGEMENT_SERVICES','Health Management Services','MENTAL_WELLNESS');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('FITNESS_EXERCISE_CLASSES','Fitness/Exercise Classes','PHYSICAL_WELLNESS');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HEALTH_MANAGEMENT_SERVICES','Health Management Services','PHYSICAL_WELLNESS');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('TELEPHONE_SERVICES','Telephone Services','PHYSICAL_WELLNESS');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('LEGAL_AID','Legal Aid','LEGAL');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('COPAY_ASSISTANCE','CoPay Assistance','FINANCES');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HOUSING_ASSISTANCE','Housing Assistance','FINANCES');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('MONEY','Money','FINANCES');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('CHRONIC_DISEASE_MANAGEMENT','Chronic Disease Management','MEDICAL_OTHER_SUPPLY');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HEALTH_MANAGEMENT_SERVICES','Health Management Services','MEDICATION_MGMT_ASSISTANCE');
INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('PATIENT_ASSISTANCE','Patient Assistance','MEDICATION_MGMT_ASSISTANCE');

INSERT INTO [dbo].[ProgramType]([code],[display_name],[need_type]) VALUES ('HOMECARE','Homecare','HOME_HEALTH');




INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z74.2','Need for assistance at home and no other household member able to render care');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z75.5','Holiday relief care');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZCHIL','Unable to find or pay for child care');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z55.9','Problems related to education and literacy, unspecified');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z71.89','Other specified counseling');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZCARE','Unable to pay for medical care');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZOTH','Unable to pay for other needed items');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z71.9','Counseling, unspecified');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z59.9','Problem related to housing and economic circumstances, unspecified');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z56.89','Other problems related to employment');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZUTIL','Unable to pay for utilities');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZFIT','Participating in Fitness/Exercise Classes');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZPHN','Unable to pay for phone');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z65.3','Problems related to other legal circumstances');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZLIS','Member enrolled in MSP (Medicare Savings Program), Partial Dual member MSP coverage (QMB only, SLMB only, QDWI only QI only)');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZMSPP','Member enrolled in MSP (Medicare Savings Program), Full Dual member MSP coverage (QMB+, SLMB+)');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZMSPF','Member enrolled in LIS (Low Income Subsidy)');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z59.6','Low Income');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('Z59.4','Lack of adequate food and safe drinking water');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZSNAP','Member enrolled in Supplemental Nutrition Assistance Program');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZWIC','Member enrolled in Women, Infants and Children Nutrition Assistance Program');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZMED','Unable to pay for prescriptions');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZPHN','Unable to pay for phone');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZTRAN1','Unable to get or pay for transportation for Medical Appointments or Prescriptions');
INSERT INTO [dbo].[ZCode]([code],[description]) VALUES ('ZTRAN3','Unable to get or pay for transportation unrelated to health care - getting things needed for daily living');

GO


declare @zcode_id bigint; 
declare @program_type_id bigint;

select @program_type_id=id from [dbo].[ProgramType] where code ='HOMECARE' and need_type = 'HOME_HEALTH';
select @zcode_id=id from [dbo].[ZCode] where code ='Z74.2';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('HOMECARE_ASSISTANCE','Homecare Assistance',@zcode_id,@program_type_id);

	 
select @program_type_id=id from [dbo].[ProgramType] where code ='ADULT_CARE_SERVICES' and need_type = 'SUPPORT';
select @zcode_id=id from [dbo].[ZCode] where code ='Z74.2';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('ADULT_DAY_CARE_SOCIALIZATION_AND_RECREATION_PROGRAMS','Adult Day Care, Socialization, and Recreation Programs',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='Z75.5';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CAREGIVER_SUPPORT_RESPITE','Caregiver Support - Respite',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='CHILDREN_SERVICES' and need_type = 'OTHER';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCHIL';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CAR_SEAT_PROGRAM','Car Seat Program',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CHILD_CARE_ASSISTANCE','Child Care Assistance',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='CHILDREN_SERVICES' and need_type = 'EDUCATION_TASK';
select @zcode_id=id from [dbo].[ZCode] where code ='Z55.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('EDUCATION','Education',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='CHRONIC_DISEASE_MANAGEMENT' and need_type = 'HEALTH_STATUS';
select @zcode_id=id from [dbo].[ZCode] where code ='Z71.89';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CONDITION_RELATED_EDUCATION','Condition Related Education',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CONDITION_RELATED_PREVENTION','Condition Related Prevention',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CONDITION_RELATED_TELEPHONE_HELPLINE','Condition Related Telephone Helpline',@zcode_id,@program_type_id);

select @program_type_id=id from [dbo].[ProgramType] where code ='CHRONIC_DISEASE_MANAGEMENT' and need_type = 'MEDICAL_OTHER_SUPPLY';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCARE';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('MEDICAL_EQUIPMENT_DEVICES','Medical Equipment & Devices',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('MINOR_MEDICAL_SUPPLIES','Minor Medical Supplies',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZOTH';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('PERSONAL_MEDICAL_ALERT_SYSTEM','Personal Medical Alert System',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SERVICE_ANIMAL','Service Animal',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='COPAY_ASSISTANCE' and need_type = 'FINANCES';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCARE';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CONDITION_RELATED_FINANCIAL_ASSISTANCE','Condition Related Financial Assistance',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='COUNSELING_INTERVENTION_TREATMENT' and need_type = 'SUPPORT';
select @zcode_id=id from [dbo].[ZCode] where code ='Z71.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CRISIS_HOTLINE','Crisis Hotline',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('EMOTIONAL_SUPPORT_HOTLINE','Emotional Support Hotline',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SUPPORT_GROUP','Support Group',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='Z59.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FINANCIAL_HOTLINE','Financial Hotline',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='DIY_WORKSHOPS_LEARNING_SESSIONS' and need_type = 'EDUCATION_TASK';
select @zcode_id=id from [dbo].[ZCode] where code ='Z55.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('COMPUTER_COURSEE','Computer Course',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='EMPLOYMENT_SERVICES' and need_type = 'EMPLOYMENT';
select @zcode_id=id from [dbo].[ZCode] where code ='Z56.89';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CAREER_CLOTHING_ASSISTANCE','Career Clothing Assistance',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('EMPLOYMENT_ASSISTANCE','Employment Assistance',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('EMPLOYMENT_WORKSHOPS','Employment Workshops',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='ENERGY_ASSISTANCE' and need_type = 'HOUSING_ONLY';
select @zcode_id=id from [dbo].[ZCode] where code ='ZUTIL';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('EAF_AND_GAF','EAF and GAF',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('ENERGY_REBATES','Energy Rebates',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FUEL','Fuel',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('UTILITIES_ELECTRIC_GAS','Utilities, Electric, Gas',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='FAMILY_HELP_SERVICES' and need_type = 'OTHER';
select @zcode_id=id from [dbo].[ZCode] where code ='ZOTH';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('BURIAL_FINANCIAL_ASSISTANCE','Burial Financial Assistance',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SCHOOL_SUPPLIES','School Supplies',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='FITNESS_EXERCISE_CLASSES' and need_type = 'PHYSICAL_WELLNESS';
select @zcode_id=id from [dbo].[ZCode] where code ='ZFIT';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FITNESS_HEALTH','Fitness & Health',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HEALTH_MANAGEMENT_SERVICES' and need_type = 'HEALTH_STATUS';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCARE';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('DENTAL_SERVICES','Dental Services',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('HEALTH_SCREENING','Health Screening',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('VISION_SERVICES','Vision Services',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HEALTH_MANAGEMENT_SERVICES' and need_type = 'MENTAL_WELLNESS';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCARE';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('MEMORY_DISORDER_ASSISTED_LIVING','Memory Disorder Assisted Living',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HEALTH_MANAGEMENT_SERVICES' and need_type = 'PHYSICAL_WELLNESS';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCARE';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('PHYSICAL_THERAPY','Physical Therapy',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZOTH';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FITNESS_MEMBERSHIP_DISCOUNT','Fitness Membership Discount',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HEALTH_MANAGEMENT_SERVICES' and need_type = 'MEDICATION_MGMT_ASSISTANCE';
select @zcode_id=id from [dbo].[ZCode] where code ='ZCARE';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('VACCINE_ASSISTANCE','Vaccine Assistance',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HOME_GOODS_PERSONAL_CARE_ASSISTANCE' and need_type = 'SUPPORT';
select @zcode_id=id from [dbo].[ZCode] where code ='ZPHN';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('DISCOUNT_INTERNET_SERVICES','Discount Internet Services',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZOTH';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('BASIC_NECESSITIES_AND_THRIFT_GOODS','Basic Necessities and Thrift Goods',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HOME_REPAIR' and need_type = 'HOUSING_ONLY';
select @zcode_id=id from [dbo].[ZCode] where code ='Z59.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('HOME_REPAIR_ASSISTANCE','Home Repair Assistance',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('HWAP','HWAP',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('MINOR_HOME_REPAIR_SERVICES','Minor Home Repair Services',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HOUSING_ASSISTANCE' and need_type = 'FINANCES';
select @zcode_id=id from [dbo].[ZCode] where code ='Z59.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FORECLOSURE_HELPLINE','Foreclosure Helpline',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='HOUSING_ASSISTANCE' and need_type = 'HOUSING_ONLY';
select @zcode_id=id from [dbo].[ZCode] where code ='Z59.9';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('RENTAL_ASSISTANCE','Rental Assistance',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='LEGAL_AID' and need_type = 'LEGAL';
select @zcode_id=id from [dbo].[ZCode] where code ='Z65.3';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('LEGAL_AID_SERVICES','Legal Aid Services',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('LEGAL_HOTLINE','Legal Hotline',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='MONEY' and need_type = 'FINANCES';
select @zcode_id=id from [dbo].[ZCode] where code ='ZOTH';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('DISABILITY_ASSISTANCE','Disability Assistance',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FINANCIAL_ASSISTANCE','Financial Assistance',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZMSPP';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id])
	VALUES ('MEDICARE_SAVINGS_PROGRAM_PARTIAL','Medicare Savings Program - Partial',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('MEDICARE_SAVINGS_PROGRAM_FULL','Medicare Savings Program - Full',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='Z59.6';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('PROPERTY_TAX_ASSISTANCE','Property Tax Assistance',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='MONEY' and need_type = 'HOUSING_ONLY';
select @zcode_id=id from [dbo].[ZCode] where code ='ZLIS';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('LOW_INCOME_SUBSIDY','Low Income Subsidy',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='NUTRITION' and need_type = 'NUTRITION_SECURITY';
select @zcode_id=id from [dbo].[ZCode] where code ='Z59.4';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('CONGREGATE_MEALS','Congregate Meals',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('FOOD_PANTRY','Food Pantry',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('GROCERY_SAVINGS','Grocery Savings',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('HOME_DELIVERED_MEALS','Home Delivered Meals',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZSNAP';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SNAP_NUTRITION_PROGRAMS','SNAP Nutrition Programs',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZWIC';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('WIC_NUTRITION_PROGRAMS','WIC Nutrition Programs',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='PATIENT_ASSISTANCE' and need_type = 'MEDICATION_MGMT_ASSISTANCE';
select @zcode_id=id from [dbo].[ZCode] where code ='ZMED';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('GENERIC_DRUG_PRESCRIPTION_ASSISTANCE','Generic Drug Prescription Assistance',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('PRESCRIPTION_DISCOUNT_PROGRAM','Prescription Discount Program',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SPECIALIZED_DRUG_PRESCRIPTION_ASSISTANCE','Specialized Drug Prescription Assistance',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='TELEPHONE_SERVICES' and need_type = 'PHYSICAL_WELLNESS';
select @zcode_id=id from [dbo].[ZCode] where code ='ZPHN';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('LIFELINES','Lifelines',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='TRANSPORTATION_SERVICES' and need_type = 'TRANSPORTATION';
select @zcode_id=id from [dbo].[ZCode] where code ='ZTRAN1';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('NEMT_PARATRANSIT_DEMAND_RESPONSE','NEMT/Paratransit/Demand Response',@zcode_id,@program_type_id);

select @zcode_id=id from [dbo].[ZCode] where code ='ZTRAN3';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('PUBLIC_TRANSPORTATION','Public Transportation',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SENIOR_DISABLED_REDUCED_FARES','Senior & Disabled Reduced Fares',@zcode_id,@program_type_id);
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('SENIOR_DISABLED_RIDE_SERVICE','Senior & Disabled Ride Service',@zcode_id,@program_type_id);


select @program_type_id=id from [dbo].[ProgramType] where code ='TRANSPORTATION_SERVICES' and need_type = 'HEALTH_STATUS';
select @zcode_id=id from [dbo].[ZCode] where code ='ZTRAN3';
INSERT INTO [dbo].[ProgramSubType]([code],[display_name],[zcode_id],[program_type_id]) 
	VALUES ('PASSPORT','Passport',@zcode_id,@program_type_id);

GO
