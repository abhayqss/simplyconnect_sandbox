import Can from './can/CanAppointmentInitialState'
import List from './list/AppointmentListInitialState'
import Community from './community/CommunityInitialState'
import Export from './export/AppointmentExportInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    export: Export(),
    community: Community()
})