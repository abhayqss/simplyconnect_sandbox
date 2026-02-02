import { Reducer } from 'redux/utils/List'

import actionTypes from './labResearchReasonListActionTypes'
import InitialState from './LabResearchReasonListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})