import actionTypes from './prospectRouteActionTypes'

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
