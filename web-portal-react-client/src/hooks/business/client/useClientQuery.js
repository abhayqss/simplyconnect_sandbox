import { useEffect } from 'react'

import { noop } from 'underscore'

import useResponse from 'hooks/common/useResponse'
import useBoundActions from 'hooks/common/redux/useBoundActions'

import * as actions from 'redux/client/details/clientDetailsActions'

import { isInteger } from 'lib/utils/Utils'

export default function useClientQuery(
    { clientId },
    {
        onSuccess = noop,
        onFailure = noop
    } = {}
) {
    const load = useBoundActions(actions.load)

    const onResponse = useResponse({
        onSuccess, onFailure
    })

    useEffect(() => {
        if (isInteger(clientId)) {
            load(clientId).then(onResponse)
        }
    }, [clientId, load, onResponse])
}