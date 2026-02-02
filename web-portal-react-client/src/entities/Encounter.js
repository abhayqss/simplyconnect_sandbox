const { Record } = require('immutable')

const Encounter = Record({
    typeId: null,
    toDate: null,
    fromDate: null,
    clinician: '',
})

export default Encounter
