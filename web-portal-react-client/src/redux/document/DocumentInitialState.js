import Details from './details/DocumentDetailsInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    details: Details()
})

export default InitialState