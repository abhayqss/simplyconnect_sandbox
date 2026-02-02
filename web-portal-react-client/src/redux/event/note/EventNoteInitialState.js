import Can from './can/CanEventNoteInitialState'
import List from './list/EventNoteListInitialState'
import Composed from './composed/EventNoteComposedInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    composed: Composed()
})