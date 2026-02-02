import { Reducer } from 'redux/utils/List'

import actionTypes from './labResearchSpecimenTypeListActionTypes'
import InitialState from './LabResearchSpecimenTypeListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})