const { Record } = require('immutable')

const ProspectClientDeactivation = Record({
    deactivationDate: Date.now(),
    deactivationReason: null,
    comment: null
})

export default ProspectClientDeactivation