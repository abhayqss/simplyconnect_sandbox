import { Reducer } from 'redux/utils/List'

import actionTypes from './organizationListActionTypes'
import InitialState from './OrganizationListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})