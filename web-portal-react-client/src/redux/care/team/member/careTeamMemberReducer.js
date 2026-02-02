import InitialState from './CareTeamMemberInitialState'

import careTeamMemberListReducer from './list/careTeamMemberListReducer'
import careTeamMemberFormReducer from './form/careTeamMemberFormReducer'
import careTeamMemberCountReducer from './count/careTeamMemberCountReducer'
import careTeamMemberDetailsReducer from './details/careTeamMemberDetailsReducer'
import careTeamMemberHistoryReducer from './history/careTeamMemberHistoryReducer'
import careTeamMemberAffiliatedReducer from './affiliated/careTeamMemberAffiliatedReducer'
import canCareTeamMemberReducer from './can/canCareTeamMemberReducer'
import organizationReducer from './organization/careTeamMemberContactOrganizationReducer'

const initialState = new InitialState()

export default function careTeamMemberReducer(state = initialState, action) {
    let nextState = state

    const list = careTeamMemberListReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const form = careTeamMemberFormReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const count = careTeamMemberCountReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const details = careTeamMemberDetailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const history = careTeamMemberHistoryReducer(state.history, action)
    if (history !== state.history) nextState = nextState.setIn(['history'], history)

    const affiliated = careTeamMemberAffiliatedReducer(state.affiliated, action)
    if (affiliated !== state.affiliated) nextState = nextState.setIn(['affiliated'], affiliated)

    const can = canCareTeamMemberReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const organization = organizationReducer(state.organization, action)
    if (organization !== state.organization) nextState = nextState.setIn(['organization'], organization)

    return nextState
}