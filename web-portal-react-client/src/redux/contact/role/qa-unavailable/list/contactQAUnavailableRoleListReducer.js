import { Reducer } from 'redux/utils/List'

import actionTypes from './contactQAUnavailableRoleListActionTypes'
import InitialState from './СontactQAUnavailableRoleListInitialState'

export default Reducer({
	actionTypes,
	stateClass: InitialState,
	isMinimal: true
})