import React from 'react'

import * as actions from 'redux/directory/treatment/service/list/treatmentServiceListActions'

import Factory from '../ActionFactory'

const LoadAction = Factory(actions, {
    action: ({ shouldDispatch, ...params } = {}, actions) => actions.load(params, shouldDispatch)
})

const CleanAction = Factory(actions, {
    action: (params, actions) => actions.clear()
})

export default function(props) {
    return (
        <>
            <LoadAction {...props}/>
            <CleanAction performingPhase="unmounting"/>
        </>
    )
}