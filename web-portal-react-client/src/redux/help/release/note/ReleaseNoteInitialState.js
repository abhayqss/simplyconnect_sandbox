import Can from './can/CanReleaseNoteInitialState'
import List from './list/ReleaseNoteListInitialState'
import Details from './details/ReleaseNoteDetailsInitialState'
import Deletion from './deletion/ReleaseNoteDeletionInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    details: Details(),
    deletion: Deletion()
})