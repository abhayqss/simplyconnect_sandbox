import React from 'react'

import Factory from '../ActionFactory'

import * as actions from 'redux/directory/community/type/list/communityTypeListActions'

const LoadAction = Factory(actions, {
    action: (params, actions) => actions.load(params)
})

const CleanAction = Factory(actions, {
    action: (params, actions) => actions.clear()
})

export default function(props) {
    return (
        <>
            <LoadAction {...props}/>
            <CleanAction {...props} performingPhase="unmounting"/>
        </>
    )
}