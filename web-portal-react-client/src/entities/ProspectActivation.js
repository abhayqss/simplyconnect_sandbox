const { Record } = require('immutable')

const ProspectActivation = Record({
    activationDate: Date.now(),
    comment: null
})

export default ProspectActivation