const schema = {
    "type": "object",
    "properties": {
        "physicianServices": {
            "type": "object",
            "title": "Bluestone Physician Services Consent for Services",
            "properties": {
                "dob": {
                    "title": "Date of birth"
                },
                "communityRoom": {
                    "type": "string",
                    "title": "Community and room #"
                },
                "cityState": {
                    "type": "string",
                    "title": "City/State"
                }
            }
        },
        "patientEnrollment": {
            "type": "object",
            "title": "Patient Enrollment Form",
            "properties": {
                "patientInformation": {
                    "type": "object",
                    "title": "Patient Information",
                    "properties": {
                        "mi": {
                            "type": "string",
                            "title": "M.I."
                        },
                        "gender": {
                            "title": "Gender",
                            "enum": [
                                "Female",
                                "Male"
                            ],
                            "enumNames": [
                                "Female",
                                "Male"
                            ]
                        },
                        "communityType": {
                            "title": "Community type",
                            "enum": [
                                "Memory Care",
                                "Assisted Living",
                                "Group Home",
                                "Independent Living"
                            ]
                        }
                    }
                },
                "healthcareInformation": {
                    "type": "object",
                    "title": "Healthcare Information",
                    "properties": {
                        "drugAllergies": {
                            "type": "string",
                            "title": "Drug allergies and specific reactions"
                        },
                        "currentDiagnoses": {
                            "type": "string",
                            "title": "Current diagnoses"
                        }
                    }
                }
            }
        }
    }
}

const uiSchema = {
    "physicianServices": {
        "dob": {
            "ui:field": "DateField",
            "ui:options": {
                "isFutureDisabled": true
            }
        },
        "communityRoom": {
            "ui:options": {
                "maxLength": 384
            }
        },
        "cityState": {
            "ui:options": {
                "maxLength": 384
            }
        },
        "ui:grid": [
            {
                "dob": {
                    "md": 4
                },
                "communityRoom": {
                    "md": 4
                },
                "cityState": {
                    "md": 4
                }
            }
        ]
    },
    "patientEnrollment": {
        "patientInformation": {
            "mi": {
                "ui:options": {
                    "maxLength": 5
                }
            },
            "gender": {
                "ui:field": "RadioGroupField",
                "ui:options": {
                    "view": "row"
                }
            },
            "communityType": {
                "ui:field": "SelectField",
                "ui:options": {
                    "isMultiple": false
                }
            },
            "ui:grid": [
                {
                    "mi": {
                        "md": 4
                    },
                    "gender": {
                        "md": 4
                    },
                    "communityType": {
                        "md": 4
                    }
                }
            ]
        },
        "healthcareInformation": {
            "drugAllergies": {
                "ui:options": {
                    "maxLength": 256
                }
            },
            "currentDiagnoses": {
                "ui:options": {
                    "maxLength": 256
                }
            },
            "ui:grid": [
                {
                    "drugAllergies": {
                        "md": 12
                    }
                },
                {
                    "currentDiagnoses": {
                        "md": 12
                    }
                }
            ]
        },
        "ui:grid": [
            {
                "patientInformation": {
                    "md": 12
                }
            },
            {
                "healthcareInformation": {
                    "md": 12
                }
            }
        ]
    },
    "ui:grid": [
        {
            "physicianServices": {
                "md": 12
            }
        },
        {
            "patientEnrollment": {
                "md": 12
            }
        }
    ]
}

export default { schema, uiSchema }