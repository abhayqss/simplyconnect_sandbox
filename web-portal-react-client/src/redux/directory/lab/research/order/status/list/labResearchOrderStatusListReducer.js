import { Reducer } from 'redux/utils/List'

import actionTypes from './labResearchOrderStatusListActionTypes'
import InitialState from './LabResearchOrderStatusListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})