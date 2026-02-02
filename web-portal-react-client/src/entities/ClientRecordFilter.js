const { Record } = require('immutable')

const ClientRecordFilter = Record({
    lastName: null,
    firstName: null,
    birthDate: null,
    middleName: null,
    genderId: null,
    ssnLast4: null,
    street: null,
    city: null,
    zip: null,
    stateId: null,
    phone: null,
})

export default ClientRecordFilter
