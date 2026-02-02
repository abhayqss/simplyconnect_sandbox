const { Record } = require('immutable')

const ClientDeactivation = Record({
    exitDate: Date.now(),
    deactivationReason: null,
    comment: ""
})

export default ClientDeactivation