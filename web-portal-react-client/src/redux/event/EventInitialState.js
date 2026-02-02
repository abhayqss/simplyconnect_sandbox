import Ir from './ir/IrInitialState'
import Can from './can/CanEventInitialState'
import List from './list/EventListInitialState'
import Form from './form/EventFormInitialState'
import Page from './page/EventPageInitialState'
import Note from './note/EventNoteInitialState'
import Details from './details/EventDetailsInitialState'
import Community from './community/CommunityInitialState'
import Notification from './notification/EventNotificationInitialState'

const { Record } = require('immutable')

export default Record({
    ir: Ir(),
    can: Can(),
    list: List(),
    form: Form(),
    page: Page(),
    note: Note(),
    details: Details(),
    community: Community(),
    notification: Notification()
})