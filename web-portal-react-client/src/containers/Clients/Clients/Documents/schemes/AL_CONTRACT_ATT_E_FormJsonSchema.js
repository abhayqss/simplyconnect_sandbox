export const schema = {
    "type": "object",
    "properties": {
        "numberOfApt": {
            "type": "string",
            "title": "Apartment #"
        },
        "date": {
            "title": "Today's Date"
        },
        "careDateFrom": {
            "title": "Start of Care"
        },
        "nursingAssessment": {
            "title": "Date of Nursing Assessment"
        },
        "codeStatus": {
            "title": "Code Status",
            "enumNames": [
                "DNR/DNI",
                "Full Resuscitation"
            ],
            "enum": [
                "dnrOrDni",
                "fullResuscitation"
            ]
        },
        "advanceDirective": {
            "title": "Advance Directive",
            "enum": [
                true,
                false
            ],
            "enumNames": [
                "Yes",
                "No"
            ]
        },
        "POADocument": {
            "title": "POA Document",
            "enum": [
                true,
                false
            ],
            "enumNames": [
                "Yes",
                "No"
            ]
        },
        "emergencyContact": {
            "type": "string",
            "title": "Emergency Contact"
        },
        "emergencyContactRelationship": {
            "type": "string",
            "title": "Emergency Contact, Relationship"
        },
        "emergencyContactPhone": {
            "title": "Emergency Contact, Phone #"
        },
        "physicianName": {
            "type": "string",
            "title": "Physician Name"
        },
        "physicianPhone": {
            "title": "Physician Phone #"
        },
        "hospital": {
            "type": "string",
            "title": "Hospital"
        },
        "physicianFax": {
            "title": "Physician Fax #"
        },
        "circumstances": {
            "title": "Circumstances in Which Emergency Medical Services are NOT to be summoned",
            "enumNames": [
                "Identified in Resident’s advance directive (refer to advance directive document)",
                "Resident has no advance directive (refer to Section 10 below)"
            ],
            "enum": [
                "identifiedInAdvanceDirective",
                "hasNoAdvanceDirective"
            ]
        },
        "payorSource": {
            "title": "Payor Source",
            "enumNames": [
                "Private Pay",
                "Elderly Waiver with monthly resource obligation",
                "Elderly Waiver without monthly resource obligation",
                "CADI  with monthly resource obligation",
                "CADI without monthly resource obligation"
            ],
            "enum": [
                "privatePay",
                "elderlyWaiverWithMonthlyResourceObligation",
                "elderlyWaiverWithoutMonthlyResourceObligation",
                "cadiWithMonthlyResourceObligation",
                "cadiWithoutMonthlyResourceObligation"
            ]
        },
        "countyCaseWorkerName": {
            "type": "string",
            "title": "Resident’s County Case Worker, Name"
        },
        "countyCaseWorkerPhone": {
            "title": "Resident’s County Case Worker, Telephone Number"
        },
        "servicesDescription1": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency1": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency1": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName1": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees1": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription2": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency2": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency2": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName2": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees2": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription3": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency3": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency3": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName3": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees3": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription4": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency4": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency4": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName4": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees4": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription5": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency5": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency5": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName5": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees5": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription6": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency6": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency6": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName6": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees6": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription7": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency7": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency7": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName7": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees7": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription8": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency8": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency8": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName8": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees8": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription9": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency9": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency9": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName9": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees9": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "servicesDescription10": {
            "type": "string",
            "title": "Description of Services"
        },
        "frequency10": {
            "title": "Frequency",
            "enum": [
                "Daily",
                "Weekly",
                "Bi-weekly",
                "Monthly",
                "Other"
            ]
        },
        "otherFrequency10": {
            "type": "string",
            "title": "Other (describe)"
        },
        "staffName10": {
            "type": "string",
            "title": "Staff Name / Title"
        },
        "serviceFees10": {
            "type": "string",
            "title": "Fees for Services, $",
            "format": "number"
        },
        "totalServicesFees": {
            "type": "string",
            "title": "The total charges to be billed to Resident for the assisted living services identified in Individualized Services Section per month, $",
            "format": "number"
        }
    },
    "required": [
        "numberOfApt",
        "date",
        "careDateFrom",
        "nursingAssessment",
        "codeStatus",
        "advanceDirective",
        "POADocument",
        "emergencyContact",
        "emergencyContactRelationship",
        "emergencyContactPhone",
        "physicianName",
        "physicianPhone",
        "physicianFax",
        "circumstances",
        "payorSource",
        "servicesDescription",
        "frequency",
        "staffName",
        "serviceFees",
        "totalServicesFees"
    ]
}

export const uiSchema = {
    "numberOfApt": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "date": {
        "ui:field": "DateField"
    },
    "careDateFrom": {
        "ui:field": "DateField"
    },
    "nursingAssessment": {
        "ui:field": "DateField"
    },
    "codeStatus": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "row"
        }
    },
    "advanceDirective": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "row"
        }
    },
    "POADocument": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "row"
        }
    },
    "emergencyContact": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "emergencyContactRelationship": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "emergencyContactPhone": {
        "ui:field": "PhoneField",
        "ui:options": {
            "autoFormat": true,
            "alwaysDefaultMask": true,
            "defaultMask": "...-...-....",
            "placeholder": "XXX-XXX-XXXX"
        }
    },
    "physicianName": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "physicianPhone": {
        "ui:field": "PhoneField",
        "ui:options": {
            "autoFormat": true,
            "alwaysDefaultMask": true,
            "defaultMask": "...-...-....",
            "placeholder": "XXX-XXX-XXXX"
        }
    },
    "hospital": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "physicianFax": {
        "ui:field": "PhoneField",
        "ui:options": {
            "autoFormat": true,
            "alwaysDefaultMask": true,
            "defaultMask": "...-...-....",
            "placeholder": "XXX-XXX-XXXX"
        }
    },
    "circumstances": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "column"
        }
    },
    "payorSource": {
        "ui:field": "SelectField",
        "ui:options": {
            "isMultiple": true
        }
    },
    "countyCaseWorkerName": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "countyCaseWorkerPhone": {
        "ui:field": "PhoneField",
        "ui:options": {
            "autoFormat": true,
            "alwaysDefaultMask": true,
            "defaultMask": "...-...-....",
            "placeholder": "XXX-XXX-XXXX"
        }
    },
    "servicesDescription1": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency1": {
        "ui:field": "SelectField"
    },
    "otherFrequency1": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName1": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees1": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription2": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency2": {
        "ui:field": "SelectField"
    },
    "otherFrequency2": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName2": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees2": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription3": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency3": {
        "ui:field": "SelectField"
    },
    "otherFrequency3": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName3": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees3": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription4": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency4": {
        "ui:field": "SelectField"
    },
    "otherFrequency4": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName4": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees4": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription5": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency5": {
        "ui:field": "SelectField"
    },
    "otherFrequency5": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName5": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees5": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription6": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency6": {
        "ui:field": "SelectField"
    },
    "otherFrequency6": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName6": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees6": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription7": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency7": {
        "ui:field": "SelectField"
    },
    "otherFrequency7": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName7": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees7": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription8": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency8": {
        "ui:field": "SelectField"
    },
    "otherFrequency8": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName8": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees8": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription9": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency9": {
        "ui:field": "SelectField"
    },
    "otherFrequency9": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName9": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees9": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "servicesDescription10": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "frequency10": {
        "ui:field": "SelectField"
    },
    "otherFrequency10": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "staffName10": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "serviceFees10": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "totalServicesFees": {
        "ui:options": {
            "maxLength": 6
        }
    },
    "ui:grid": [
        {
            "numberOfApt": { "md": 4 },
            "date": { "md": 4 },
            "careDateFrom": { "md": 4 },
        },
        {
            "nursingAssessment": { "md": 4 }
        },
        {
            "codeStatus": { "md": 4 },
            "advanceDirective": { "md": 4 },
            "POADocument": { "md": 4 }
        },
        {
            "emergencyContact": { "md": 4 },
            "emergencyContactRelationship": { "md": 4 },
            "emergencyContactPhone": { "md": 4 }
        },
        {
            "physicianName": { "md": 4 },
            "physicianPhone": { "md": 4 },
            "hospital": {"md": 4 },
        },
        {
            "physicianFax": {"md": 4 }
        },
        {
            "circumstances": { "md": 12 }
        },
        {
            "payorSource": { "md": 4 },
            "countyCaseWorkerName": { "md": 4 },
            "countyCaseWorkerPhone": { "md": 4 },
        },
        {
            "servicesDescription1": {"md": 4},
            "frequency1": {"md": 4},
            "otherFrequency1": {"md": 4}
        },
        {
            "staffName1": { "md": 4 },
            "serviceFees1": { "md": 4 }
        },
        {
            "servicesDescription2": {"md": 4},
            "frequency2": {"md": 4},
            "otherFrequency2": {"md": 4}
        },
        {
            "staffName2": {"md": 4},
            "serviceFees2": {"md": 4}
        },
        {
            "servicesDescription3": {"md": 4},
            "frequency3": {"md": 4},
            "otherFrequency3": {"md": 4}
        },
        {
            "staffName3": {"md": 4},
            "serviceFees3": {"md": 4}
        },
        {
            "servicesDescription4": {"md": 4},
            "frequency4": {"md": 4},
            "otherFrequency4": {"md": 4}
        },
        {
            "staffName4": {"md": 4},
            "serviceFees4": {"md": 4}
        },
        {
            "servicesDescription5": {"md": 4},
            "frequency5": {"md": 4},
            "otherFrequency5": {"md": 4}
        },
        {
            "staffName5": {"md": 4},
            "serviceFees5": {"md": 4}
        },
        {
            "servicesDescription6": {"md": 4},
            "frequency6": {"md": 4},
            "otherFrequency6": {"md": 4}
        },
        {
            "staffName6": {"md": 4},
            "serviceFees6": {"md": 4}
        },
        {
            "servicesDescription7": {"md": 4},
            "frequency7": {"md": 4},
            "otherFrequency7": {"md": 4}
        },
        {
            "staffName7": {"md": 4},
            "serviceFees7": {"md": 4}
        },
        {
            "servicesDescription8": {"md": 4},
            "frequency8": {"md": 4},
            "otherFrequency8": {"md": 4}
        },
        {
            "staffName8": {"md": 4},
            "serviceFees8": {"md": 4}
        },
        {
            "servicesDescription9": {"md": 4},
            "frequency9": {"md": 4},
            "otherFrequency9": {"md": 4}
        },
        {
            "staffName9": {"md": 4},
            "serviceFees9": {"md": 4}
        },
        {
            "servicesDescription10": {"md": 4},
            "frequency10": {"md": 4},
            "otherFrequency10": {"md": 4}
        },
        {
            "staffName10": {"md": 4},
            "serviceFees10": {"md": 4}
        },
        {
            "totalServicesFees": {"md": 4}
        }
    ]
}

export default { schema, uiSchema }