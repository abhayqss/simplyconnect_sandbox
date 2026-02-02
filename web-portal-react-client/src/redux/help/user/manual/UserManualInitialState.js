import Can from './can/CanUserManualInitialState'
import List from './list/UserManualListInitialState'
import Details from './details/UserManualDetailsInitialState'
import Deletion from './deletion/UserManualDeletionInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    details: Details(),
    deletion: Deletion()
})