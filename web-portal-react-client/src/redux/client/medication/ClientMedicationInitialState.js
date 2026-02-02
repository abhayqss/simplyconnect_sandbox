import Count from './count/ClientMedicationCountInitialState'

const { Record } = require('immutable')

export default Record({
    count: Count()
})