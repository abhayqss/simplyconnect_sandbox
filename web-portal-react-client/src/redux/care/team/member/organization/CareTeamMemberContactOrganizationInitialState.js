import List from './list/careTeamMemberContactOrganizationListInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    list: List()
})

export default InitialState
