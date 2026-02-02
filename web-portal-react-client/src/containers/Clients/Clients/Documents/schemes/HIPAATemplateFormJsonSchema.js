const schema = {
    "type": "object",
    "properties": {
        "alert": {
            "default": "If Resident or Responsible Party fails to sign this Receipt of Notice of Privacy Practices, a Facility Representative shall complete the following by initialing/dating and providing additional information where appropriate"
        },
        "facilityId": {
            "type": "string",
            "title": "Facility provided its Notice of Privacy Practices to",
            "enum": [
                "resident",
                "responsibleParty",
                "other"
            ],
            "enumNames": [
                "Resident",
                "Resident's Responsible Party",
                "Other (describe)"
            ]
        },
        "otherFacility": {
            "type": "string",
            "title": "Other (describe)"
        },
        "personThatRefusedAcknowledgment": {
            "type": "string",
            "title": "The person to whom the Notice of Privacy Practices was given, as identified above, refused to sign and return the Acknowledgment after being requested to do so"
        },
        "refusedAcknowledgmentDate": {
            "title": "Date"
        },
        "notObtainedAcknowledgementReasons": {
            "type": "string",
            "title": "The written Acknowledgement of Receipt of Facility’s Notice of Privacy Practices was not obtained for the following other reasons"
        },
        "personThatDidNotObtainAcknowledgement": {
            "title": "Name",
            "type": "string"
        },
        "notObtainedAcknowledgementDate": {
            "title": "Date"
        },
        "contactName": {
            "type": "string",
            "title": "Name of Person to Contact"
        }
    }
}

const uiSchema = {
    "alert": {
        "ui:field": "AlertPanel"
    },
    "facilityId": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            "view": "column"
        }
    },
    "otherFacility": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "personThatRefusedAcknowledgment": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "refusedAcknowledgmentDate": {
        "ui:field": "DateField"
    },
    "notObtainedAcknowledgementReasons": {
        "ui:options": {
            "maxLength": 512
        }
    },
    "personThatDidNotObtainAcknowledgement": {
        "ui:options": {
            "maxLength": 128
        }
    },
    "notObtainedAcknowledgementDate": {
        "ui:field": "DateField"
    },
    "contactName": {
        "ui:options": {
            "maxLength": 256
        }
    },
    "ui:grid": [
        {
            "alert": { "md": 12 }
        },
        {
            "facilityId": { "md": 6 },
            "otherFacility": { "md": 6 }

        },
        {
            "personThatRefusedAcknowledgment": { "md": 6 },
            "refusedAcknowledgmentDate": { "md": 6 }
        },
        {
            "notObtainedAcknowledgementReasons": { "md": 6 },
            "personThatDidNotObtainAcknowledgement": { "md": 6 },
        },
        {
            "notObtainedAcknowledgementDate": { "md": 6 },
            "contactName": { "md": 6 }
        }
    ]
}

export default { schema, uiSchema }