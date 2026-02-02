import { Reducer } from 'redux/utils/List'

import actionTypes from './labResearchIcdCodeListActionTypes'
import InitialState from './LabResearchIcdCodeListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})