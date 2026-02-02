import Add from './add/CanAddNoteInitialState'
import View from './view/CanViewNotesInitialState'

const { Record } = require('immutable')

export default Record({
	add: Add(),
	view: View()
})