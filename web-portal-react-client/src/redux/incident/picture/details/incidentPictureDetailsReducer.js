import { Reducer } from 'redux/utils/Details'

import actionTypes from './incidentPictureDetailsActionTypes'
import InitialState from './IncidentPictureDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})