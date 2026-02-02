import { State } from 'redux/utils/Form'

const { Record } = require('immutable')

const Address = Record({
    street: null,
    city: null,
    stateId: null,
    zip: null
})

const AddressErrors = Record({
    street: false,
    city: false,
    stateId: false,
    zip: false
})

export default State({
    validation: Record({
        isSuccess: true,
        errors: Record({
            essentials: Record({
                authorRole: false,
                date: false,
                typeId: false,
                isEmergencyDepartmentVisit: false,
                isOvernightInpatient: false
            })(),
            description: Record({
                location: false,
                situation: false,
                background: false,
                assessment: false,
                hasInjury: false,
                followUpDetails: false
            })(),
            treatment: Record({
                hasPhysician: false,
                physician: Record({
                    firstName: false,
                    lastName: false,
                    phone: false,
                    hasAddress: false,
                    address: AddressErrors()
                })(),
                hasHospital: false,
                hospital: Record({
                    name: false,
                    phone: false,
                    hasAddress: false,
                    address: AddressErrors()
                })()
            })(),
            hasResponsibleManager: false,
            responsibleManager: Record({
                firstName: false,
                lastName: false,
                phone: false,
                email: false
            })(),
            hasRegisteredNurse: false,
            registeredNurse: Record({
                firstName: false,
                lastName: false,
                hasAddress: false,
                address: AddressErrors()
            })()
        })()
    })(),
    fields: Record({
        id: null,

        /*Client*/
        client: Record({
            communityId: '',
            communityTitle: '',
            organizationId: '',
            organizationTitle: '',
            firstName: '',
            lastName: '',
            ssn: ''
        })(),

        /*Event Essentials*/
        essentials: Record({
            author: null,
            authorRole: null,
            date: null,
            typeId: null,
            typeTitle: null,
            isEmergencyDepartmentVisit: false,
            isOvernightInpatient: false,
        })(),

        /*Event Description*/
        description: Record({
            location: null,
            situation: null,
            background: null,
            assessment: null,
            hasInjury: false,
            isFollowUpExpected: false,
            followUpDetails: null,
        })(),

        /*Treatment Details*/
        treatment: Record({
            hasPhysician: false,

            physician: Record({
                firstName: null,
                lastName: null,
                phone: null,
                hasAddress: false,
                address: Address(),
            })(),

            hasHospital: false,

            hospital: Record({
                name: null,
                phone: null,
                hasAddress: false,
                address: Address(),
            })(),
        })(),

        /*Responsible Manager*/
        hasResponsibleManager: false,

        responsibleManager: Record({
            firstName: null,
            lastName: null,
            phone: null,
            email: null
        })(),

        /*Registered Nurse*/
        hasRegisteredNurse: false,

        registeredNurse: Record({
            firstName: null,
            lastName: null,
            hasAddress: false,
            address: Address(),
        })()
    })()
})