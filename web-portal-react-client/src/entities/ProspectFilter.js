const { Record } = require('immutable')

const ProspectFilter = Record({
    genderId: null,
    lastName: null,
    firstName: null,
    birthDate: null,
    prospectStatus: null,
})

export default ProspectFilter