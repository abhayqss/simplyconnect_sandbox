import { Reducer } from 'redux/utils/List'

import actionTypes from './communityListActionTypes'
import InitialState from './CommunityListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})