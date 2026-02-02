import { Actions } from 'redux/utils/Value'

import actionTypes from './clientRouteActionTypes'

const {
    CLEAR,
    CHANGE
} = actionTypes

export default {
    clear: () => dispatch => {
        dispatch({ type: CLEAR })
    },
    change: value => dispatch => {
        dispatch({ type: CHANGE, payload: value })
    },
}
