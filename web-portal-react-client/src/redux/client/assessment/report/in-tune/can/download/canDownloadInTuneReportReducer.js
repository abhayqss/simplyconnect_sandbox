import { Reducer } from 'redux/utils/Value'

import actionTypes from './canDownloadInTuneReportActionTypes'
import InitialState from './CanDownloadInTuneReportInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })