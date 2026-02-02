import { Store } from 'lib/stores'

import actionTypes from './clientLastViewedActionTypes'

const { SET, CLEAR } = actionTypes

const store = new Store()

export default {
    clear: () => ({ type: CLEAR }),
    setId: id => dispatch => {
        store.save('lastViewedClientId', id)

        dispatch({ type: SET, payload: id })
    }
}