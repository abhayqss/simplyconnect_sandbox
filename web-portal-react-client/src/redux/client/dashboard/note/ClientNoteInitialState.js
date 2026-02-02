import List from './list/ClientNoteListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})