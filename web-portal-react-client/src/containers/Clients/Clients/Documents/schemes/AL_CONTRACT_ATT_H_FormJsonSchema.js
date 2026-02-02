export const schema = {
    "type": "object",
    "properties": {
        "name": {
            "type": "string",
            "title": "Name"
        },
        "position": {
            "type": "string",
            "title": "Position"
        },
        "required": [
            "name",
            "position"
        ]
    }
}

export const uiSchema = {
    "name": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "position": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "ui:grid": [
        {
            "name": { "md": 6 },
            "position": { "md": 6 }
        }
    ]
}

export default { schema, uiSchema }