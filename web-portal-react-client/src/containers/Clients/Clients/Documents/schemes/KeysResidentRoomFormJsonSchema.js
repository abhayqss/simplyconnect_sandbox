const schema = {
    "type": "object",
    "properties": {
        "apartmentNumber": {
            "type": "string",
            "title": "Apartment Number (Key No)"
        },
        "dateIssued": {
            "title": "Date Issued"
        }
    }
}

const uiSchema = {
    "apartmentNumber": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "dateIssued": {
        "ui:field": "DateField"
    },
    "ui:grid": [
        {
            "apartmentNumber": { "md": 6 },
            "dateIssued": { "md": 6 },
        }
    ]
}

export default { schema, uiSchema }