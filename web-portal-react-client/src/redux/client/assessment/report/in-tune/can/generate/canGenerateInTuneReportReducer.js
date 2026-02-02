import { Reducer } from 'redux/utils/Value'

import actionTypes from './canGenerateInTuneReportActionTypes'
import InitialState from './CanGenerateInTuneReportInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })