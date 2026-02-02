const schema = {
    "type": "object",
    "properties": {
        "type": {
            "title": "Type",
            "enumNames": [
                "Med Mgmt",
                "Independent Living"
            ],
            "enum": [
                "medMgmt",
                "independentLiving"
            ]
        },
        "room": {
            "title": "Room #",
            "type": "string"
        },
        "birthDate": {
            "title": "Date of Birth"
        },
        "ssn": {
            "type": "string",
            "title": "Medicare ID # or 4 last digits SSN#",
        },
        "representative": {
            "title": "Patient Representative(if any) (Print)",
            "type": "string"
        },
        "representativeRelation": {
            "title": "Patient Representative relationship to Patient",
            "type": "string"
        },
        "recipient": {
            "title": "Invoices will be sent to (check one and complete information):",
            "enumNames": [
                "Patient",
                "Patient Representative"
            ],
            "enum": [
                "patient",
                "patientRepresentative"
            ]
        },
        "address": {
            "title": "Address",
            "type": "string",
        },
        "city": {
            "title": "City",
            "type": "string"
        },
        "state": {
            "title": "State",
            "type": "string"
        },
        "zip": {
            "title": "Zip Code",
            "type": "string",
            "format": "number"
        },
        "phone": {
            "title": "Phone",
        },
        "altPhone": {
            "title": "Alt Phone",
        },
        "allergies": {
            "title": "Allergies",
            "type": "string"
        },
    },
    "required": []
}

const uiSchema = {
    "type": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "row"
        }
    },
    "room": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "birthDate": {
        "ui:field": "DateField",
        "ui:options": {
            "isFutureDisabled": true
        }
    },
    "ssn": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "representative": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "representativeRelation": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "recipient": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "row"
        }
    },
    "address": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "city": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "state": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "zip": {
        "ui:options": {
            "maxLength": 5
        }
    },
    "phone": {
        "ui:field": "PhoneField"
    },
    "altPhone": {
        "ui:field": "PhoneField"
    },
    "allergies": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "ui:grid": [
        {
            "type": { "md": 6 },
            "room": { "md": 6 },
        },
        {
            "birthDate": { "md": 4 },
            "ssn": { "md": 4 },
            "representative": { "md": 4 },
        },
        {
            "representativeRelation": { "md": 6 },
            "recipient": { "md": 6 },
        },
        {
            "address": { "md": 4 },
            "city": { "md": 4 },
            "state": { "md": 4 },
        },
        {
            "zip": { "md": 4 },
            "phone": { "md": 4 },
            "altPhone": { "md": 4 },
        },
        {
            "allergies": { "md": 4 },
        }
    ]
}

export default { schema, uiSchema }