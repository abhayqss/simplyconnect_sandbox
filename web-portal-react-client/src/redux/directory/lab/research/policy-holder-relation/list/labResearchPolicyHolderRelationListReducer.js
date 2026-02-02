import { Reducer } from 'redux/utils/List'

import actionTypes from './labResearchPolicyHolderRelationListActionTypes'
import InitialState from './LabResearchPolicyHolderRelationListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})