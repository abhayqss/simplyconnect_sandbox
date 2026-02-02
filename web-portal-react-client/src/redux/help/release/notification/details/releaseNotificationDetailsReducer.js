import { Reducer } from 'redux/utils/Details'

import actionTypes from './releaseNotificationDetailsActionTypes'
import InitialState from './ReleaseNotificationDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})