import List from './list/NoteTypeListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: List()
})

export default InitialState