import Add from './add/CanAddCareTeamMemberInitialState'
import View from './view/CanViewCareTeamMemberInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    add: new Add(),
    view: new View()
})

export default InitialState