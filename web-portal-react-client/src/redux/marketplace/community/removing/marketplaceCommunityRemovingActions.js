import { Actions } from 'redux/utils/Send'

import service from 'services/PrivateMarketplaceCommunityService'

import actionTypes from './marketplaceCommunityRemovingActionTypes'

const {
    SEND_REQUEST,
    SEND_SUCCESS,
    SEND_FAILURE
} = actionTypes

export default {
    ...Actions({ actionTypes }),
    send: (data) => {
        return dispatch => {
            dispatch({ type: SEND_REQUEST, payload: { community: data } })
            return service.removeById(data.communityId).then(response => {
                dispatch({ type: SEND_SUCCESS, payload: { ...response.data, community: data } })
                return response
            }).catch(error => {
                dispatch({ type: SEND_FAILURE, payload: { error, community: data } })
                return error
            })
        }
    }
}