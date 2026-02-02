import { Reducer } from 'redux/utils/List'

import actionTypes from './careTeamMemberContactOrganizationListActionTypes'
import InitialState from './careTeamMemberContactOrganizationListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})