export const schema = {
    "type": "object",
    "properties": {
        "patientInfo": {
            "type": "object",
            "title": "Patient information",
            "properties": {
                "firstName": {
                    "title": "First Name",
                    "type": "string"
                },
                "middleName": {
                    "title": "Middle Name",
                    "type": "string"
                },
                "lastName": {
                    "title": "Last Name",
                    "type": "string"
                },
                "birthDate": {
                    "title": "Date of birth"
                },
                "homeAddress": {
                    "title": "Home address",
                    "type": "string"
                },
                "city": {
                    "title": "City",
                    "type": "string"
                },
                "state": {
                    "title": "State",
                    "type": "string"
                },
                "zip": {
                    "title": "Zip code",
                    "type": "string"
                },
                "id": {
                    "title": "Medical Record/patient ID number",
                    "type": "string"
                }
            }
        }
    }
}

export const uiSchema = {
    "patientInfo": {
        "firstName": {
            "ui:options": {
                "maxLength": 256,
                "isDisabled": true
            }
        },
        "middleName": {
            "ui:options": {
                "maxLength": 256
            }
        },
        "lastName": {
            "ui:options": {
                "maxLength": 256,
                "isDisabled": true
            }
        },
        "birthDate": {
            "ui:field": "DateField",
            "ui:options": {
                "isFutureDisabled": true
            }
        },
        "homeAddress": {
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
                "maxLength": 128
            }
        },
        "zip": {
            "ui:options": {
                "maxLength": 5,
                "type": "number"
            }
        },
        "id": {
            "ui:options": {
                "maxLength": 56
            }
        },
        "ui:grid": [
            {
                "firstName": {
                    "md": 4
                },
                "middleName": {
                    "md": 4
                },
                "lastName": {
                    "md": 4
                }
            },
            {
                "birthDate": {
                    "md": 4
                },
                "homeAddress": {
                    "md": 8
                }
            },
            {
                "city": {
                    "md": 4
                },
                "state": {
                    "md": 4
                },
                "zip": {
                    "md": 4
                }
            },
            {
                "id": {
                    "md": 8
                }
            }
        ]
    },
    "ui:grid": [
        {
            "patientInfo": {
                "md": 12
            }
        }
    ]
}

export default { schema, uiSchema }