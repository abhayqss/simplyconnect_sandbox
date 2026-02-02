const schema = {
    "type": "object",
    "properties": {
        "contactPerson": {
            "title": "Contact person",
            "type": "string"
        }
    }
}

const uiSchema = {
    "contactPerson": {
        "ui:options": {
            "maxLength": 254
        }
    },
    "ui:grid": [
        {
            "contactPerson": { "md": 12 }
        }
    ]
}

export default { schema, uiSchema }