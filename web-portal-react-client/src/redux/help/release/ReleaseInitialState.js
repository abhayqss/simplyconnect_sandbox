import Note from './note/ReleaseNoteInitialState'
import Notification from './notification/ReleaseNotificationInitialState'

const { Record } = require('immutable')

export default Record({
    note: Note(),
    notification: Notification()

})