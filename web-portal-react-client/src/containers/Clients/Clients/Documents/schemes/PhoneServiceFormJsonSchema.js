const schema = {
    "type": "object",
    "properties": {
        "room": {
            "type": "string",
            "title": "Room #"
        },
        "residentPhone": {
            "title": "Resident Phone #"
        },
        "dateActivated": {
            "title": "Date Activated"
        },
        "dateDeactivated": {
            "title": "Date Deactivated"
        }
    }
}

const uiSchema = {
    "room": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "residentPhone": {
        "ui:field": "PhoneField"
    },
    "dateActivated": {
        "ui:field": "DateField"
    },
    "dateDeactivated": {
        "ui:field": "DateField"
    },
    "ui:grid": [
        {
            "room": { "md": 6 },
            "residentPhone": { "md": 6 }
        },
        {
            "dateActivated": { "md": 6 },
            "dateDeactivated": { "md": 6 }
        }
    ]
}

export default { schema, uiSchema }