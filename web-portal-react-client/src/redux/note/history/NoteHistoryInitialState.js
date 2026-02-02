import List from './list/NoteHistoryListInitialState'
import Details from './details/NoteHistoryDetailsInitialState'

const { Record } = require('immutable')

export default Record({
    list: List(),
    details: Details()
})