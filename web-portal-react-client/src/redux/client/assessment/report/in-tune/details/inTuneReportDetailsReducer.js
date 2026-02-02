import { Reducer } from 'redux/utils/Details'

import actionTypes from './inTuneReportDetailsActionTypes'
import InitialState from './InTuneReportDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})