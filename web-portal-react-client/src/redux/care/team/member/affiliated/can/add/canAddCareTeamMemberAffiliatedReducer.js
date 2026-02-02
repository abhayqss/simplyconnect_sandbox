import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddCareTeamMemberAffiliatedActionsTypes'
import InitialState from './CanAddCareTeamMemberAffiliatedInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })