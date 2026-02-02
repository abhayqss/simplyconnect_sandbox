const schema = {
  "type": "object",
  "properties": {
    "patientConsent": {
      "title": "Patient consent to treat and authorizations form",
      "type": "object",
      "properties": {
        "dob": {
          "title": "Patient DOB",
        }
      }
    },
    "newPatientInfo": {
      "title": "New patient information form",
      "type": "object",
      "properties": {
        "patientInfo": {
          "title": "Patient information",
          "type": "object",
          "properties": {
            "dob": {
              "title": "DOB",
            },
            "gender": {
              "title": "Gender",
              "enum": [
                "Female",
                "Male",
                "Declined to answer"
              ]
            },
            "livingLevel": {
              "title": "Level of Living",
              "enum": [
                "Assisted living",
                "Memory care",
                "Hearth"
              ]
            },
            "patientPhone": {
              "title": "Patient Phone"
            },
            "cell": {
              "title": "Cell"
            },
            "pharmacy": {
              "title": "Pharmacy",
              "type": "string"
            },
            "phone": {
              "title": "Phone"
            },
            "fax": {
              "title": "Fax"
            }
          }
        }
      },
    },
    "authorization": {
      "title": "Authorization for Release of Information",
      "type": "object",
      "properties": {
        "patientInfo": {
          "title": "Patient information",
          "type": "object",
          "properties": {
            "dob": {
              "title": "DOB"
            }
          }
        }
      },
    }
  }
}

const uiSchema = {
  "patientConsent": {
    "dob": {
      "ui:field": "DateField",
      "ui:options": {
        "isFutureDisabled": true
      }
    },
    "ui:grid": [
      {
        "dob": { "md": 6 }
      }
    ]
  },
  "newPatientInfo": {
    "patientInfo": {
      "dob": {
        "ui:field": "DateField",
        "ui:options": {
          "isFutureDisabled": true
        }
      },
      "gender": {
        "ui:field": "SelectField"
      },
      "livingLevel": {
        "ui:field": "SelectField"
      },
      "patientPhone": {
        "ui:field": "PhoneField"
      },
      "cell": {
        "ui:field": "PhoneField"
      },
      "pharmacy": {
        "ui:options": {
          "maxLength": 256
        }
      },
      "phone": {
        "ui:field": "PhoneField"
      },
      "fax": {
        "ui:field": "PhoneField"
      },
      "ui:grid": [
        {
          "dob": { "md": 6 },
          "gender": { "md": 6 }
        },
        {
          "livingLevel": { "md": 6 },
          "patientPhone": { "md": 6 }
        },
        {
          "cell": { "md": 6 },
          "pharmacy": { "md": 6 }
        },
        {
          "phone": { "md": 6 },
          "fax": { "md": 6 }
        },
      ]
    },
    "ui:grid": [
      {
        "patientInfo": { "md": 12 }
      }
    ]
  },
  "authorization": {
    "patientInfo": {
      "dob": {
        "ui:field": "DateField",
        "ui:options": {
          "isFutureDisabled": true
        }
      },
      "ui:grid": [
        {
          "dob": { "md": 6 }
        }
      ]
    },
    "ui:grid": [
      {
        "patientInfo": { "md": 12 }
      }
    ]
  },
  "ui:grid": [
    {
      "patientConsent": { "md": 12 }
    },
    {
      "newPatientInfo": { "md": 12 }
    },
    {
      "authorization": { "md": 12 }
    }
  ]
}

export default { schema, uiSchema }