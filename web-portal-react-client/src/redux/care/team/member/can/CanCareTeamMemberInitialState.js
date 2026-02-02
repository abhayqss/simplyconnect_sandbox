import Add from './add/CanAddCareTeamMemberInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    add: new Add(),
})

export default InitialState