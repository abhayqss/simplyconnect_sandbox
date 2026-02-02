import React from 'react'

import { Button } from 'reactstrap'

import { DynaForm } from 'components/DynaForm'

const schema = {
    "type": "object",
    "title": "A Test form",
    "required": ["firstName"],
    "properties": {
        "firstName": {
            "type": "string",
            "title": "First Name",
            "minLength": 2,
            "maxLength": 5
        },
        "birthDate": {
            "title": "Date of Birth"
        },
        "activeStatus": {
            "type": "boolean",
            "title": "Active Status"
        },
        'gender': {
            'title': 'Gender'
        },
        "uiFrameworks": {
            "title": "UI Frameworks"
        },
        "phone": {
            "title": "Home Phone"
        },
        "car": {
            "title": "Car"
        },
        "privateSection": {
            "type": "object",
            "title": "Private Info",
            "properties": {
                "lastName": {
                    "type": "string",
                    "title": "Last Name"
                },
                "ssn": {
                    "type": "string",
                    "title": "SSN"
                }
            }
        }
    }
}

const uiSchema = {
    "birthDate": {
        "ui:field": "DateField",
        "ui:options": {
            hasTimeSelect: true,
            dateFormat: "MM/dd/yyyy hh:mm a 'GMT'XXX"
        }
    },
    "gender": {
        "ui:field": 'SelectField',
        "ui:options": {
            options: [
                { text: 'Male', value: 1 },
                { text: 'Female', value: 2 },
                { text: 'Unknown', value: 3 },
            ]
        }
    },
    "uiFrameworks": {
        "ui:field": 'SelectField',
        "ui:options": {
            isMultiple: true,
            options: [
                { text: 'Vue', value: 1 },
                { text: 'React', value: 2 },
                { text: 'Angular', value: 3 },
                { text: 'Web Components', value: 4 },
                { text: 'Meteor', value: 5 },
                { text: 'Ember', value: 6 },
            ]
        }
    },
    "phone": {
        "ui:field": "PhoneField"
    },
    "car": {
        "ui:field": "RadioGroupField",
        "ui:options": {
            options: [
                { label: "BMW", value: 1 },
                { label: "Mercedes", value: 2 },
                { label: "Porsche", value: 3 },
                { label: "Rolls-Royce", value: 4 },
                { label: "Land Rover", value: 5 },
            ]
        }
    },
    "privateSection": {
        "ui:grid": [
            {
                lastName: { lg: 6, md: 12 },
                ssn: { lg: 6, md: 12 },
            }
        ]
    }
}

export default function TestDynaForm() {
    return (
        <DynaForm
            noHtml5Validate
            schema={schema}
            uiSchema={uiSchema}
        >
            <Button color="success">
                Submit
            </Button>
        </DynaForm>
    )
}