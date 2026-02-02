import { Actions } from 'redux/utils/List'

import service from 'services/PrivateMarketplaceCommunityService'

import actionTypes from './marketplaceCommunityListActionTypes'

const {
    LOAD_REQUEST,
    LOAD_SUCCESS,
    LOAD_FAILURE
} = actionTypes

export default Actions({
    actionTypes,
    load: function (params) {
        return (dispatch, getState) => {
            const { page, size, shouldAccumulate } = params

            dispatch({
                type: LOAD_REQUEST,
                payload: { page, shouldAccumulate }
            })

            return service.find(params).then(response => {
                dispatch({
                    type: LOAD_SUCCESS,
                    payload: {
                        page,
                        size,
                        ...response,
                        shouldAccumulate,
                        ...shouldAccumulate && {
                            prevData: getState().marketplace.community.list.dataSource.data
                        }
                    }
                })

                return response
            }).catch(e => {
                dispatch({ type: LOAD_FAILURE, payload: e })
            })
        }
    }
})