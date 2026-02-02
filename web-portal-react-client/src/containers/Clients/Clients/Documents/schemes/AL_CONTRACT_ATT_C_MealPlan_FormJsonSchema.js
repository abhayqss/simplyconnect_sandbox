export const schema = {
    "type": "object",
    "properties": {
        "numberOfApt": {
            "type": "string",
            "title": "Apt #"
        },
        "provider": {
            "type": "string",
            "title": "Provider"
        },
        "effectiveDate": {
            "title": "Effective date"
        }
    },
    "required": [
        "numberOfApt",
        "provider",
        "effectiveDate"
    ]
}

export const uiSchema = {
    "numberOfApt": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "provider": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "effectiveDate": {
        "ui:field": "DateField"
    },
    "ui:grid": [
        {
            "numberOfApt": {
                "lg": 4,
                "md": 12
            },
            "provider": {
                "lg": 4,
                "md": 12
            },
            "effectiveDate": {
                "lg": 4,
                "md": 12
            }
        }
    ]
}

export default { schema, uiSchema }