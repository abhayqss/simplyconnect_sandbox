import Encounter from './Encounter'

const { Record } = require('immutable')

const Note = Record({
    id: null,
    plan: '',
    eventId: null,
    noteDate: null,
    subTypeId: null,
    objective: '',
    assessment: '',
    subjective: '',
    encounter: Encounter(),
    admitDateId: null
})

export default Note
