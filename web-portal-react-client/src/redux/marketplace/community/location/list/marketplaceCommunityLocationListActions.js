import { Actions } from 'redux/utils/List'

import service from 'services/PrivateMarketplaceCommunityService'

import actionTypes from './marketplaceCommunityLocationListActionTypes'

const {
    LOAD_REQUEST,
    LOAD_SUCCESS,
    LOAD_FAILURE
} = actionTypes

export default Actions({
    actionTypes,
    load: function (params) {
        return (dispatch) => {
            dispatch({ type: LOAD_REQUEST })

            return service.findLocations(params).then(response => {
                dispatch({
                    type: LOAD_SUCCESS,
                    payload: response
                })

                return response
            }).catch(e => {
                dispatch({ type: LOAD_FAILURE, payload: e })
            })
        }
    }
})
