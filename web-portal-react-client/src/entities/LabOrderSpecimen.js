const { Record, Set } = require('immutable')

const LabOrderSpecimen = Record({
    site: '',
    date: null,
    types: Set(),
    collectorName: '',
})

export default LabOrderSpecimen
