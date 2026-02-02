const schema = {
    "type": "object",
    "properties": {
        "day": {
            "type": "string",
            "title": "Day",
            "format": "number"
        },
        "month": {
            "title": "Month",
            "enum": [
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            ]
        },
        "year": {
            "type": "string",
            "title": "The last 2 digits of a year",
            "format": "number"
        },
        "facility": {
            "title": "Facility",
            "type": "string"
        },
        "resident": {
            "title": "Resident",
            "type": "string"
        },
        "arbitrationAgreementSignatures": {
            "type": "object",
            "title": "Notice Of Right To Rescind binding Arbitration Clause",
            "properties": {
                "rescissionStartDate": {
                    "title": "Date rescission period begins"
                },
                "facilityName": {
                    "title": "Facility name",
                    "type": "string"
                },
                "facilityAddress": {
                    "title": "Facility address",
                    "type": "string"
                },
                "facilityAddress2": {
                    "title": "Facility address 2",
                    "type": "string"
                },
                "rescissionEndDate": {
                    "title": "Last day for rescission"
                }
            }
        }
    }
}

const uiSchema = {
    "day": {
        "ui:options": {
            "maxLength": 2
        }
    },
    "month": {
        "ui:field": "SelectField",
        "ui:options": {
            "isMultiple": false
        }
    },
    "year": {
        "ui:options": {
            "maxLength": 2
        }
    },
    "facility": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "resident": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "arbitrationAgreementSignatures": {
        "rescissionStartDate": {
            "ui:field": "DateField"
        },
        "facilityName": {
            "ui:options": {
                "maxLength": 256
            }
        },
        "facilityAddress": {
            "ui:options": {
                "maxLength": 256
            }
        },
        "facilityAddress2": {
            "ui:options": {
                "maxLength": 256
            }
        },
        "rescissionEndDate": {
            "ui:field": "DateField"
        },
        "ui:grid": [
            {
                "facilityName": { "md": 4 },
                "facilityAddress": { "md": 4 },
                "facilityAddress2": { "md": 4 }
            },
            {
                "rescissionStartDate": { "md": 6 },
                "rescissionEndDate": { "md": 6 }

            }
        ]
    },
    "ui:grid": [
        {
            "day": { "md": 4 },
            "month": { "md": 4 },
            "year": { "md": 4 }
        },
        {
            "facility": { "md": 6 },
            "resident": { "md": 6 }
        },
        {
            "arbitrationAgreementSignatures": { "md": 12 }
        }
    ]
}

export default { schema, uiSchema }