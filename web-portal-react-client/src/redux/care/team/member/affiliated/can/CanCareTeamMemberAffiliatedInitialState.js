import Add from './add/CanAddCareTeamMemberAffiliatedInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    add: new Add(),
})

export default InitialState