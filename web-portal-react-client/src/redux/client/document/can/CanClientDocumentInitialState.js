import Add from './add/CanAddClientDocumentInitialState'

const { Record } = require('immutable')

export default Record({
    add: Add()
})

