export const SURVEY_QUESTIONS_TO_SERVICE_PLAN_NEEDS = [
    {
        condition: {
            "Would you like us to provide you with resources for safe housing throughout the community?": "YES"
        },
        need: {
            domainName: "HOUSING",
            needOpportunity: "Homeless or worried that s(he) might be in the future."
        }
    },
    {
        condition: {
            "Would you like us to provide you with resources that may help with assistance for utilizes?": "YES"
        },
        need: {
            domainName: "HOUSING_ONLY",
            needOpportunity: "Trouble paying for utilities (gas/electric/phone).",
            programTypeName: "ENERGY_ASSISTANCE",
            programSubTypeName: "UTILITIES_ELECTRIC_GAS"
        }
    },
    {
        condition: {
            "Would you like us to provide you with transportation assistance or resources for transportation?": "YES"
        },
        need: {
            domainName: "TRANSPORTATION",
            needOpportunity: "Trouble finding or paying for a ride.",
            programTypeName: "TRANSPORTATION_SERVICES"
        }
    },
    {
        condition: {
            "Do you need daycare or better daycare for your grand/kids?": "YES"
        },
        need: {
            domainName: "OTHER",
            needOpportunity: "Need daycare or better daycare for grand/kids.",
            programTypeName: "CHILDREN_SERVICES",
            programSubTypeName: "CHILD_CARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you need help finding a better job?": "YES"
        },
        need: {
            domainName: "EMPLOYMENT",
            needOpportunity: "Need help finding a better job.",
            programTypeName: "EMPLOYMENT_SERVICES",
            programSubTypeName: "EMPLOYMENT_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you have any trouble putting on clothes?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Trouble putting on clothes.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you wish you had help?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Dressing assistance.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you have any problems with getting to the bathroom on time?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Problems with getting to the bathroom on time.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Problems getting on/off the toilet?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Problems getting on/off the toilet.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you feel you need equipment?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Grab bars or equipment needed.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you have any trouble chewing or swallowing your food?": "YES"
        },
        need: {
            domainName: "HEALTH_STATUS",
            needOpportunity: "Trouble chewing or swallowing your food.",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            programSubTypeName: "HEALTH_SCREENING"
        }
    },
    {
        condition: {
            "Any problem with gripping or holding onto utensils or bringing your hand to your mouth?": "YES"
        },
        need: {
            domainName: "HEALTH_STATUS",
            needOpportunity: "Problem with gripping or holding onto utensils or bringing hand to mouth.",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            programSubTypeName: "HEALTH_SCREENING"
        }
    },
    {
        condition: {
            "Do you wish you hade more help with organizing your medications so you can take them easier?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with organizing medications.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Can you see the labels on the pill bottles?": "NO"
        },
        need: {
            domainName: "HEALTH_STATUS",
            needOpportunity: "Can't see the labels on the pill bottles.",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            programSubTypeName: "VISION_SERVICES"
        }
    },
    {
        condition: {
            "Do you wish you had help1?": "YES"
        },
        need: {
            domainName: "HOUSING",
            needOpportunity: "Help with cleaning home."
        }
    },
    {
        condition: {
            "Do you wish you had more help with meal preparation?": "YES"
        },
        need: {
            domainName: "NUTRITION_SECURITY",
            needOpportunity: "Help with meal preparation.",
            programTypeName: "NUTRITION"
        }
    },
    {
        condition: {
            "Do you feel you need help?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with laundry.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Are you able to adequately hear on your telephone?": "NO",
            "Do you have trouble dialing numbers?": "YES"
        },
        need: {
            domainName: "HEALTH_STATUS",
            needOpportunity: "Help with telephone.",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            programSubTypeName: "HEALTH_SCREENING"
        }
    },
    /*{
        condition: {
            "Do you have trouble dialing numbers?": "YES"
        },
        need: {
            domainName: "HEALTH_STATUS",
            needOpportunity: "Help with telephone.",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            programSubTypeName: "HEALTH_SCREENING"
        }
    },*/
    {
        condition: {
            "Do you wish you had help3?": "YES"
        },
        need: {
            domainName: "SUPPORT",
            needOpportunity: "Assistance with medical appointments.",
            programTypeName: "ADULT_CARE_SERVICES",
            programSubTypeName: "CAREGIVER_SUPPORT_RESPITE"
        }
    },
    {
        condition: {
            "Have you fallen in the last year?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with mobility.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you have difficulty leaving the home; changing position in bed?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with mobility.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Able to walk up a flight of stairs?": "NO"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with mobility.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you feel you need them?": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with grab bars.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you feel you need help?1": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with animals.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Do you feel you need them?1": "YES"
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with steps.",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Trip hazards1": [
                { "Clutter": "Yes" },
                { "Throw rugs": "Yes" },
                { "Trash": "Yes" },
                { "Uneven flooring": "Yes" }
            ]
        },
        need: {
            domainName: "HOME_HEALTH",
            needOpportunity: "Help with trip hazards: ",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE"
        }
    },
    {
        condition: {
            "Are you able to see your thermostat to adjust it?": "NO"
        },
        need: {
            domainName: "HOUSING",
            needOpportunity: "Help with heating and cooling."
        }
    },
    {
        condition: {
            "Do you have trouble getting in and out of bed?": "YES",
            "Do you think you need a device?": "YES"
        },
        need: {
            domainName: "MEDICAL_OTHER_SUPPLY",
            needOpportunity: "Trouble getting in and out of bed.",
            programTypeName: "CHRONIC_DISEASE_MANAGEMENT",
            programSubTypeName: "MEDICAL_EQUIPMENT_DEVICES"
        }
    },
    {
        condition: {
            "Does the resident have access to safe and affordable housing?": "NO"
        },
        need: {
            domainName: 'HOUSING',
            needOpportunity: 'The resident need access to safe and affordable housing.'
        }
    },
    {
        condition: {
            "Does the resident have access to needed food?": "NO"
        },
        need: {
            domainName: 'NUTRITION_SECURITY',
            programTypeName: "NUTRITION",
            needOpportunity: 'The resident need access to food.'
        }
    },
    {
        condition: {
            'Has the resident seen their PCP in the last year?': 'NO'
        },
        need: {
            domainName: 'HEALTH_STATUS',
            programTypeName: 'HEALTH_MANAGEMENT_SERVICES',
            programSubTypeName: 'HEALTH_SCREENING',
            needOpportunity: 'Visit with PCP.'
        }
    },
    {
        condition: {
            'Can I get you a glass of water?': 'YES'
        },
        need: {
            domainName: 'HEALTH_STATUS',
            programTypeName: 'HEALTH_MANAGEMENT_SERVICES',
            programSubTypeName: 'HEALTH_SCREENING',
            needOpportunity: 'Health check-up (indicative of UTI).'
        }
    },
    {
        condition: {
            'Have they swollen anymore?': 'YES'
        },
        need: {
            domainName: 'HEALTH_STATUS',
            programTypeName: 'HEALTH_MANAGEMENT_SERVICES',
            programSubTypeName: 'HEALTH_SCREENING',
            needOpportunity: 'Health check-up (indicative of Heart disease).'
        }
    },
    {
        condition: {
            'Can I help you order refills today?': 'YES'
        },
        need: {
            domainName: 'MEDICATION_MGMT_ASSISTANCE',
            needOpportunity: 'Med management.'
        }
    },
    {
        condition: {
            'Do you feel well rested today?': 'NO'
        },
        need: {
            domainName: 'BEHAVIORAL',
            needOpportunity: 'Sleep or behavioral needs.'
        }
    },
    {
        condition: {
            'Any aches or pains that you want to talk about?': 'YES'
        },
        need: {
            domainName: 'PHYSICAL_WELLNESS',
            needOpportunity: 'Physical health.'
        }
    },
    {
        condition: {
            'Do you want your care coordinator to reach out to you?': 'YES'
        },
        need: {
            domainName: 'SUPPORT',
            needOpportunity: 'Barriers in accessing care.'
        }
    },
    {
        condition: {
            'Have you tripped or fell since my last visit?': 'YES'
        },
        need: {
            domainName: 'PHYSICAL_WELLNESS',
            needOpportunity: 'Injury or physical health need.'
        }
    },
    {
        condition: {
            'Have you had a chance to connect with friends or family since my last visit?': 'NO'
        },
        need: {
            domainName: 'SOCIAL_WELLNESS',
            needOpportunity: 'Social needs.'
        }
    },
    {
        condition: {
            "Income": [
                "No income",
                "Inadequate income and/or spontaneous or inappropriate spending",
                "Can meet basic needs with subsidy; appropriate spending"
            ]
        },
        need: {
            domainName: "FINANCES",
            programTypeName: "MONEY",
            programSubTypeName: "FINANCIAL_ASSISTANCE",
            needOpportunity: "Needs help identifying income opportunities."
        }
    },
    {
        condition: {
            "Credit Status": [
                "No Credit History",
                "Needs Improvement (300-579)",
                "Fair (580-669)"
            ]
        },
        need: {
            domainName: "FINANCES",
            needOpportunity: "Needs help with credit score."
        }
    },
    {
        condition: {
            "Employment": [
                "No job",
                "Temporary, part-time or seasonal; inadequate pay, no benefits",
                "Employed full time; inadequate pay; few or no benefits"
            ]
        },
        need: {
            domainName: "EMPLOYMENT",
            programTypeName: "EMPLOYMENT_SERVICES",
            programSubTypeName: "EMPLOYMENT_ASSISTANCE",
            needOpportunity: "Needs help finding a better job."
        }
    },
    {
        condition: {
            "Shelter": [
                "Homeless or threatened with eviction",
                "In transitional, temporary or substandard housing; and/or current rent/mortgage payment is unaffordable (over 30% of income)",
                "In stable housing that is safe but only marginally adequate"
            ]
        },
        need: {
            domainName: "HOUSING_ONLY",
            programTypeName: "HOUSING_ASSISTANCE",
            needOpportunity: "Needs help with Shelter."
        }
    },
    {
        condition: {
            "Food": [
                "No food or means to prepare it. Relies to a significant degree on other sources of free or low-cost food",
                "Household is on food stamps",
                "Can meet basic food needs, but requires occasional assistance"
            ]
        },
        need: {
            domainName: "NUTRITION_SECURITY",
            programTypeName: "NUTRITION",
            programSubTypeName: "FOOD_PANTRY",
            needOpportunity: "Needs help with food."
        }
    },
    {
        condition: {
            "Child Care": [
                "Needs childcare, but none is available/accessible and/or child is not eligible",
                "Childcare is unreliable or unaffordable, inadequate supervision is a problem for childcare that is available",
                "Affordable subsidized childcare is available, but limited"
            ]
        },
        need: {
            domainName: "OTHER",
            programTypeName: "CHILDREN_SERVICES",
            programSubTypeName: "CHILD_CARE_ASSISTANCE",
            needOpportunity: "Needs help with child care."
        }
    },
    {
        condition: {
            "Children's Education": [
                "One or more school-aged children not enrolled in school",
                "One or more school-aged children enrolled in school, but not attending classes",
                "Enrolled in school, but one or more children only occasionally attending classes"
            ]
        },
        need: {
            domainName: "EDUCATION_TASK",
            programTypeName: "CHILDREN_SERVICES",
            programSubTypeName: "EDUCATION",
            activationOrEducationTask: "Needs help with children's education.",
        }
    },
    {
        condition: {
            "Adult Education": [
                "Literacy problems and/or no high school diploma/GED are serious barriers to employment",
                "Needs additional education/training to improve employment situation and/or to resolve literacy problems to where they are able to function effectively in society"
            ]
        },
        need: {
            domainName: "EDUCATION_TASK",
            programTypeName: "DIY_WORKSHOPS_LEARNING_SESSIONS",
            activationOrEducationTask: "Needs help with education.",
        }
    },
    {
        condition: {
            "Legal": [
                "Current outstanding tickets or warrants",
                "Current charges/trial pending, noncompliance with probation/parole"
            ]
        },
        need: {
            domainName: "LEGAL",
            programTypeName: "LEGAL_AID",
            programSubTypeName: "LEGAL_AID_SERVICES",
            needOpportunity: "Needs help with legal assistance."
        }
    },
    {
        condition: {
            "Health Care Coverage": [
                "No medical coverage with immediate need",
                "No medical coverage and great difficulty accessing medical care when needed. Some household members may be in poor health",
                "Some members (e.g. Children) have medical coverage"
            ]
        },
        need: {
            domainName: "MEDICATION_MGMT_ASSISTANCE",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            needOpportunity: "Needs help with health care coverage."
        }
    },
    {
        condition: {
            "Life Skills": [
                "Unable to meet basic needs such as hygiene, food, activities of daily living",
                "Can meet a few but not all needs of daily living without assistance",
                "Can meet most but not all daily living needs without assistance"
            ]
        },
        need: {
            domainName: "SUPPORT",
            programTypeName: "ADULT_CARE_SERVICES",
            needOpportunity: "Basic needs / Personal care assistance."
        }
    },
    {
        condition: {
            "Mental Health": [
                "Danger to self or others; recurring suicidal ideation; experiencing severe difficulty in day-to-day life due to psychological problems",
                "Recurrent mental health symptoms that may affect behavior, but not a danger to self/others; persistent problems with functioning due to mental health symptoms",
                "Mild symptoms may be present but are transient; only moderate difficulty in functioning due to mental health symptoms"
            ]
        },
        need: {
            domainName: "MENTAL_WELLNESS",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            needOpportunity: "Needs help with treating mental health problems."
        }
    },
    {
        condition: {
            "Substance Abuse": [
                "Meets criteria for severe abuse/dependence; resulting problems so severe that institutional living or hospitalization may be necessary",
                "Meets criteria for dependence; preoccupation with use and/or obtaining drugs/alcohol; withdrawal or withdrawal avoidance behaviors evident; use results in avoidance or neglect of essential life activities",
                "Use within last 6 months; evidence of persistent or recurrent social, occupational, emotional or physical problems related to use (such as disruptive behavior or housing problems); problems have persisted for at least one month"
            ]
        },
        need: {
            domainName: "BEHAVIORAL",
            needOpportunity: "Substance Abuse Health Management."
        }
    },
    {
        condition: {
            "Family/Social Relations": [
                "Lack of necessary support form family or friends; abuse (DV, child) is present or there is child neglect",
                "Family/friends may be supportive, but lack ability or resources to help; family members do not relate well with one another; potential for abuse or neglect"
            ]
        },
        need: {
            domainName: "SOCIAL_WELLNESS",
            needOpportunity: "Needs help with family/social relations."
        }
    },
    {
        condition: {
            "Transportation": [
                "No access to transportation, public or private; may have car that is inoperable",
                "Transportation is available, but unreliable, unpredictable, unaffordable; may have care but no insurance, license, etc.",
                "Transportation is available and reliable, but limited and/or inconvenient; drivers are licensed and minimally insured"
            ]
        },
        need: {
            domainName: "TRANSPORTATION",
            programTypeName: "TRANSPORTATION_SERVICES",
            needOpportunity: "Needs help with transportation."
        }
    },
    {
        condition: {
            "Community Involvement": [
                "Socially isolated and/or no social skills and/or lacks motivation to become involved",
                "Lacks knowledge of ways to become involved",
                "Some community involvement (advisory group, support group), but has barriers such as transportation, childcare issues"
            ]
        },
        need: {
            domainName: "SOCIAL_WELLNESS",
            needOpportunity: "Needs help with community involvement."
        }
    },
    {
        condition: {
            "Safety": [
                "Home or residence is not safe; immediate level of lethality is extremely high; possible CPS involvement",
                "Safety is threatened/temporary protection is available; level of lethality is high",
                "Current level of safety is minimally adequate; ongoing safety planning is essential"
            ]
        },
        need: {
            domainName: "HOUSING",
            needOpportunity: "Needs help with safety."
        }
    },
    {
        condition: {
            "Parenting Skills": [
                "There are safety concerns regarding parenting skills",
                "Parenting skills are minimal",
                "Parenting skills are apparent but not adequate"
            ]
        },
        need: {
            domainName: "OTHER",
            programTypeName: "CHILDREN_SERVICES",
            needOpportunity: "Needs assistance with parenting skills."
        }
    },
    {
        condition: {
            "Disabilities": [
                "In crisis – acute or chronic symptoms affecting housing, employment, social interactions, etc.",
                "Vulnerable – sometimes or periodically has acute or chronic symptoms affecting housing, employment, social interactions, etc.",
                "Safe – rarely has acute or chronic symptoms affecting housing, employment, social interactions, etc."
            ]
        },
        need: {
            domainName: "PHYSICAL_WELLNESS",
            programTypeName: "HEALTH_MANAGEMENT_SERVICES",
            needOpportunity: "Needs assistance with disabilities."
        }
    },
    {
        condition: {
            "have_you_lived_in_subsidized_housing_before": "Yes"
        },
        need: {
            domainName: "HOUSING_ONLY",
            needOpportunity: "Lived in subsidized housing and may need it again."
        }
    },
    {
        condition: {
            "Do_you_owe_any_utilities_eviction_costs": "Yes"
        },
        need: {
            domainName: "FINANCES",
            needOpportunity: "Difficulty paying debts off (utilities/eviction costs/child support)."
        }
    },
    {
        condition: {
            "Do_you_have_any_of_the_following?": [
                { "Pending_Legal_Case": "Yes" },
                { "Criminal_Convictions": "Yes" },
                { "Open_Legal_Case": "Yes" },
                { "Registered_290": "Yes" }
            ]
        },
        need: {
            domainName: "LEGAL",
            programTypeName: "LEGAL_AID",
            needOpportunity: "Legal or Criminal Conviction troubles. "
        } 
    },
    {
        condition: {
            "Mobility_walking_inside_outside_home": [
                "Some assistance",
                "Total assistance"
            ]
        },
        need: {
            domainName: "HOME_HEALTH",
            programTypeName: "HOMECARE",
            programSubTypeName: "HOMECARE_ASSISTANCE",
            needOpportunity: "Help with mobility"
        }
    }
]

export const SURVEY_SECTIONS_TO_SERVICE_PLAN_NEEDS = {
    PHQ_9: {
        domainName: "BEHAVIORAL",
        needOpportunity: 'Need depression treatment, PHQ-9 score is '
    },
    GAD_7: {
        domainName: "BEHAVIORAL",
        needOpportunity: 'Anxiety identified, GAD-7 score is '
    },
    CAGE_AID: {
        domainName: "SUPPORT",
        needOpportunity: 'An indication of alcohol problems, CAGE score is ',
        programTypeName: 'COUNSELING_INTERVENTION_TREATMENT',
        programSubTypeName: 'SUPPORT_GROUP'
    },
    PATIENT_EDUCATION: {
        domainName: "EDUCATION_TASK",
        activationOrEducationTask: 'Follow up with patient healthcare provider for education needed.'
    },
    CAREGIVER: {
        domainName: "SUPPORT",
        needOpportunity: 'Experiencing distress (Caregiver Assessment - How are you?).'
    }
}