export const schema = {
    "type": "object",
    "properties": {
        "agreementEffectiveDate": {
            "title": "Agreement effective date"
        },
        "dailyRate": {
            "type": "string",
            "title": "Daily rate, $"
        },
        "alternativeDailyRate": {
            "type": "string",
            "title": "Alternative daily rate, $"
        },
        "careLevel": {
            "type": "string",
            "title": "Care level"
        },
        "facilityBy": {
            "type": "string",
            "title": "Facility, by"
        },
        "facilityTitle": {
            "type": "string",
            "title": "Facility, title"
        },
        "residentResponsibleParty": {
            "type": "string",
            "title": "Resident's responsible party"
        },
        "financialGuarantor": {
            "type": "string",
            "title": "Financial guarantor"
        },
    }
}

export const uiSchema = {
    "agreementEffectiveDate": {
        "ui:field": "DateField"
    },
    "dailyRate": {
        "ui:options": {
            "options": {
                "maxLength": 10
            }
        }
    },
    "alternativeDailyRate": {
        "ui:options": {
            "options": {
                "maxLength": 10
            }
        }
    },
    "careLevel": {
        "ui:options": {
            "options": {
                "maxLength": 256
            }
        }
    },
    "facilityBy": {
        "ui:options": {
            "options": {
                "maxLength": 256
            }
        }
    },
    "facilityTitle": {
        "ui:options": {
            "options": {
                "maxLength": 256
            }
        }
    },
    "residentResponsibleParty": {
        "ui:options": {
            "options": {
                "maxLength": 256
            }
        }
    },
    "financialGuarantor": {
        "ui:options": {
            "options": {
                "maxLength": 256
            }
        }
    }
}

export default { schema, uiSchema }