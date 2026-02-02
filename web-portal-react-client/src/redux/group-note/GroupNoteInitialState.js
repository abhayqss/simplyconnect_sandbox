import Form from './form/GroupNoteFormInitialState'
import Details from './details/GroupNoteDetailsInitialState'

const { Record } = require('immutable')

export default Record({
    form: new Form(),
    details: Details(),
})