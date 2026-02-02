const schema = {
    "type": "object",
    "properties": {
        "resident2": {
            "type": "string",
            "title": "Resident 2"
        },
        "contractDateFrom": {
            "title": "Contract Start Date"
        },
        "numberOfApt": {
            "type": "string",
            "title": "Apartment #"
        },
        "livingOption": {
            "title": "Living Option",
            "enum": [
                "AL",
                "MC",
                "ILS",
                "IL",
                null
            ],
            "enumNames": [
                "AL",
                "MC",
                "ILS",
                "IL",
                "Other (describe)"
            ]
        },
        "otherLivingOption": {
            "type": "string",
            "title": "Other (describe)"
        },
        "contractTerm": {
            "type": "string",
            "title": "Contract Term"
        },
        "seniorLinkageLineVerification": {
            "type": "string",
            "title": "Senior Linkage Line Verification #"
        },
        "securityDeposit": {
            "type": "string",
            "title": "Security Deposit, $",
            "format": "number"
        },
        "rent": {
            "type": "string",
            "title": "Rent, $",
            "format": "number"
        },
        "servicePackage": {
            "type": "string",
            "title": "Service Package, $",
            "format": "number"
        },
        "mealPlan": {
            "type": "string",
            "title": "Meal Plan, $",
            "format": "number"
        },
        "2ndOccFee": {
            "type": "string",
            "title": "2nd Occ. Fee, $",
            "format": "number"
        },
        "other2ndOccFee": {
            "type": "string",
            "title": "Other"
        },
        "fee": {
            "type": "string",
            "title": "Fee, $",
            "format": "number"
        },
        "otherFee": {
            "type": "string",
            "title": "Other"
        },
        "fee2": {
            "type": "string",
            "title": "Fee, $",
            "format": "number"
        },
        "monthlyCharges": {
            "type": "string",
            "title": "Monthly Charges, $",
            "format": "number"
        },
        "oneTimeCharges": {
            "type": "string",
            "title": "One-Time Charges, $",
            "format": "number"
        },
        "hasLegalRepresentative": {
            "title": "Does resident have a legal representative?",
            "enum": [
                "yes",
                "no"
            ],
            "enumNames": [
                "Yes",
                "No"
            ]
        },
        "hasPersonAsDesignatedRepresentative": {
            "title": "Does resident has the person identified as his or her Designated Representative?",
            "enum": [
                "yes",
                "no",
                "declined"
            ],
            "enumNames": [
                "Yes",
                "No",
                "Resident declined to identify a Designated Representative"
            ]
        },
        "hasResponsiblePartyForBilling": {
            "title": "Does resident has responsible party for billing?",
            "enum": [
                "yes",
                "no"
            ],
            "enumNames": [
                "Yes",
                "No"
            ]
        },
        "willMaintainMotorVehicleOnPremises": {
            "title": "Will resident be maintaining a motor vehicle  on the premises?",
            "enum": [
                "yes",
                "no"
            ],
            "enumNames": [
                "Yes",
                "No"
            ]
        }
    },
    "required": [
        "contractDateFrom",
        "numberOfApt",
        "livingOption",
        "contractTerm",
        "securityDeposit",
        "rent",
        "servicePackage",
        "mealPlan",
        "2ndOccFee",
        "monthlyCharges",
        "oneTimeCharges",
        "hasLegalRepresentative",
        "hasPersonAsDesignatedRepresentative",
        "hasResponsiblePartyForBilling",
        "willMaintainMotorVehicleOnPremises"
    ]
}

const uiSchema = {
    "resident2": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "contractDateFrom": {
        "ui:field": "DateField"
    },
    "numberOfApt": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "livingOption": {
        "ui:field": "RadioGroupField"
    },
    "otherLivingOption": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "contractTerm": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "seniorLinkageLineVerification": {
        "ui:options": {
            "maxLength": 15
        }
    },
    "securityDeposit": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "rent": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "servicePackage": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "mealPlan": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "2ndOccFee": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "other2ndOccFee": {
        "ui:options": {
            "maxLength": 50
        }
    },
    "fee": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "otherFee": {
        "ui:options": {
            "maxLength": 50
        }
    },
    "fee2": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "monthlyCharges": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "oneTimeCharges": {
        "ui:options": {
            "maxLength": 8
        }
    },
    "hasLegalRepresentative": {
        "ui:field": "RadioGroupField"
    },
    "hasPersonAsDesignatedRepresentative": {
        "ui:field": "RadioGroupField"
    },
    "hasResponsiblePartyForBilling": {
        "ui:field": "RadioGroupField"
    },
    "willMaintainMotorVehicleOnPremises": {
        "ui:field": "RadioGroupField"
    },
    "ui:grid": [
        {
            "resident2": { "md": 4 },
            "contractDateFrom": { "md": 4 },
            "numberOfApt": { "md": 4 }
        },
        {
            "livingOption": { "md": 12 }
        },
        {
            "otherLivingOption": { "md": 12 }
        },
        {
            "contractTerm": { "md": 12 }
        },
        {
            "seniorLinkageLineVerification": { "md": 12 }
        },
        {
            "securityDeposit": { "md": 12 }
        },
        {
            "rent": { "md": 12 }
        },
        {
            "servicePackage": { "md": 12 }
        },
        {
            "mealPlan": { "md": 12 }
        },
        {
            "2ndOccFee": { "md": 12 }
        },
        {
            "other2ndOccFee": { "md": 12 }
        },
        {
            "fee": { "md": 12 }
        },
        {
            "otherFee": { "md": 12 }
        },
        {
            "fee2": { "md": 12 }
        },
        {
            "monthlyCharges": { "md": 12 }
        },
        {
            "oneTimeCharges": { "md": 12 }
        },
        {
            "hasLegalRepresentative": { "md": 12 }
        },
        {
            "hasPersonAsDesignatedRepresentative": { "md": 12 }
        },
        {
            "hasResponsiblePartyForBilling": { "md": 12 }
        },
        {
            "willMaintainMotorVehicleOnPremises": { "md": 12 }
        }
    ]
}

export default { schema, uiSchema }