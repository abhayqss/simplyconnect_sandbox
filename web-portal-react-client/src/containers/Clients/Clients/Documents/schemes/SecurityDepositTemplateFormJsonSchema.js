const schema = {
    "type": "object",
    "properties": {
        "numberOfApt": {
            "type": "string",
            "title": "Resident Apt #"
        },
        "responsiblePerson": {
            "type": "string",
            "title": "Responsible person"
        },
        "address": {
            "type": "string",
            "title": "Address"
        },
        "city": {
            "type": "string",
            "title": "City"
        },
        "state": {
            "title": "State",
            "type": "string"
        },
        "zip": {
            "title": "ZIP",
            "type": "string",
            "format": "number"
        },
        "securityDeposit": {
            "title": "Refundable Security Deposit",
            "type": "string",
        },
        "makePayable": {
            "title": "Please make check payable to",
            "type": "string",
        }
    }
}

const uiSchema = {
    "numberOfApt": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "responsiblePerson": {
        "ui:options": {
            "maxLength": 256
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
            "maxLength": 5,
        }
    },
    "securityDeposit": {
        "ui:field": "CurrencyField",
        "ui:options": {
            "maxLength": 10,
        }
    },
    "makePayable": {
        "ui:options": {
            "maxLength": 256,
        }
    },
    "ui:grid": [
        {
            "numberOfApt": { "md": 4 },
            "responsiblePerson": { "md": 4 },
            "address": { "md": 4 },
        },
        {
            "city": { "md": 4 },
            "state": { "md": 4 },
            "zip": { "md": 4 },
        },
        {
            "securityDeposit": { "md": 4 },
            "makePayable": { "md": 4 }
        }
    ]
}

export default { schema, uiSchema }