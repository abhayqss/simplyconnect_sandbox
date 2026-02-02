const schema = {
    "type": "object",
    "properties": {
        "numberOfApt": {
            "type": "string",
            "title": "Resident Apt #"
        },
        "verificationCode": {
            "type": "string",
            "title": "Senior Linkage Information/Verification Code #"
        }
    },
    "required": [
        "numberOfApt",
        "verificationCode"
    ]
}

const uiSchema = {
    "numberOfApt": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "verificationCode": {
        "ui:options": {
            "maxLength": 10
        }
    },
    "ui:grid": [
        {
            "numberOfApt": { "md": 4 },
            "verificationCode": { "md": 8 }
        }
    ]
}

export default { schema, uiSchema }