import React from 'react'

import { ReactComponent as TeamManagementIcon } from 'images/marketplace/team-management.svg'
import { ReactComponent as EducationIcon } from 'images/marketplace/education.svg'
import { ReactComponent as TargetIcon } from 'images/marketplace/target.svg'
import { ReactComponent as FoodIcon } from 'images/marketplace/food.svg'
import { ReactComponent as HardAndSoftwareIcon } from 'images/marketplace/hard-and-software.svg'
import { ReactComponent as MedicationIcon } from 'images/marketplace/medication.svg'
import { ReactComponent as ToolsIcon } from 'images/marketplace/tools.svg'
import { ReactComponent as HealthcareIcon } from 'images/marketplace/healthcare.svg'
import { ReactComponent as HealthReportIcon } from 'images/marketplace/health-report.svg'
import { ReactComponent as MedChestIcon } from 'images/marketplace/med-chest.svg'
import { ReactComponent as HospitalIcon } from 'images/marketplace/hospital.svg'
import { ReactComponent as BuildingIcon } from 'images/marketplace/building.svg'
import { ReactComponent as SandwichIcon } from 'images/marketplace/sandwich.svg'

export const SERVICE_CATEGORIES = {
	CONSULTING_AND_MANAGEMENT_SOLUTIONS: 'CONSULTING_&_MANAGEMENT_SOLUTIONS',
	STAFFING_AND_TRAINING_AND_EDUCATION: 'STAFFING_&_TRAINING_&_EDUCATION',
	MARKETING_AND_OFFICE_SOLUTIONS: 'MARKETING_&_OFFICE_SOLUTIONS',
	FOOD_SERVICES: 'FOOD_SERVICES',
	TECHNOLOGY_AND_SOFTWARE: 'TECHNOLOGY_&_SOFTWARE',
	PHARMACY_AND_LABS_AND_MEDICATION_MANAGEMENT_AND_SUPPLIES: 'PHARMACY_&_LABS_&_MEDICATION_MANAGEMENT_&_SUPPLIES',
	FACILITY_MAINTENANCE_AND_SERVICES: 'FACILITY_MAINTENANCE_&_SERVICES',
	HEALTH_CARE_SERVICES: 'HEALTH_CARE_SERVICES',
	ADDITIONAL_CARE_SERVICES: 'ADDITIONAL_CARE_SERVICES',
	OUTPATIENT_CARE_SERVICES: 'OUTPATIENT_CARE_SERVICES',
	RESIDENT_HOME_MAINTENANCE_AND_SERVICES: 'RESIDENT_HOME_MAINTENANCE_&_SERVICES',
	FINANCING_AND_LEGAL_SERVICES: 'FINANCING_&_LEGAL_SERVICES',
	OTHER: 'OTHER'
}

export const SERVICE_CATEGORY_ICONS = {
	[SERVICE_CATEGORIES.CONSULTING_AND_MANAGEMENT_SOLUTIONS]: TeamManagementIcon,
	[SERVICE_CATEGORIES.STAFFING_AND_TRAINING_AND_EDUCATION]: EducationIcon,
	[SERVICE_CATEGORIES.MARKETING_AND_OFFICE_SOLUTIONS]: TargetIcon,
	[SERVICE_CATEGORIES.FOOD_SERVICES]: FoodIcon,
	[SERVICE_CATEGORIES.TECHNOLOGY_AND_SOFTWARE]: HardAndSoftwareIcon,
	[SERVICE_CATEGORIES.PHARMACY_AND_LABS_AND_MEDICATION_MANAGEMENT_AND_SUPPLIES]: MedicationIcon,
	[SERVICE_CATEGORIES.FACILITY_MAINTENANCE_AND_SERVICES]: ToolsIcon,
	[SERVICE_CATEGORIES.HEALTH_CARE_SERVICES]: HealthcareIcon,
	[SERVICE_CATEGORIES.ADDITIONAL_CARE_SERVICES]: HealthReportIcon,
	[SERVICE_CATEGORIES.OUTPATIENT_CARE_SERVICES]: MedChestIcon,
	[SERVICE_CATEGORIES.RESIDENT_HOME_MAINTENANCE_AND_SERVICES]: HospitalIcon,
	[SERVICE_CATEGORIES.FINANCING_AND_LEGAL_SERVICES]: BuildingIcon,
	[SERVICE_CATEGORIES.OTHER]: SandwichIcon
}