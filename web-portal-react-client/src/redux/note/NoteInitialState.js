import Can from './can/CanNoteInitialState'
import List from './list/NoteListInitialState'
import Form from './form/NoteFormInitialState'
import Details from './details/NoteDetailsInitialState'
import History from './history/NoteHistoryInitialState'
import Page from './page/NotePageInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    page: Page(),
    list: List(),
    form: new Form(),
    details: Details(),
    history: History()
})