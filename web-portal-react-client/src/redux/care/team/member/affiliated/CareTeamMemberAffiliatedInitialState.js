import Can from './can/CanCareTeamMemberAffiliatedInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    can: new Can()
})

export default InitialState