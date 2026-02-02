import Count from './count/CareTeamMemberCountInitialState'
import Can from './can/CanCareTeamMemberInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    count: new Count(),
    can: new Can()
})

export default InitialState