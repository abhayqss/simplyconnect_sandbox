import { Actions } from 'redux/utils/List'

import service from 'services/PrivateMarketplaceCommunityService'

import actionTypes from './savedMarketplaceCommunityListActionTypes'

const {
    LOAD_REQUEST,
    LOAD_SUCCESS,
    LOAD_FAILURE
} = actionTypes

export default Actions({
    actionTypes,
    load: function (params) {
        return dispatch => {
            dispatch({ type: LOAD_REQUEST })

            return service.findSaved(params).then(({ data }) => {
                dispatch({ type: LOAD_SUCCESS, payload: data })
            }).catch(e => {
                dispatch({ type: LOAD_FAILURE, payload: e })
            })
        }
    }
})