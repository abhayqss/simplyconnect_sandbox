const schema = {
    "type": "object",
    "properties": {
        "verbalConsent": {
            "type": "string",
            "title": "Verbal consent given by"
        },
    }
}

const uiSchema = {
    "verbalConsent": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "ui:grid": [
        {
            "verbalConsent": { "md": 12 },
        }
    ]
}

export default { schema, uiSchema }