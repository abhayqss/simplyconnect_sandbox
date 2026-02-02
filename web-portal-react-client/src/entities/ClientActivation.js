const { Record } = require('immutable')

const ClientActivation = Record({
    intakeDate: Date.now(),
    programType: null,
    comment: ""
})

export default ClientActivation