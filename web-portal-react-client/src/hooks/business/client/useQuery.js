import { useEffect } from 'react'

import useBoundActions from 'hooks/common/redux/useBoundActions'

export default function useQuery(actions, params, { shouldRetry = true } = {}) {
    const { clientId } = params

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (shouldRetry) {
            load({ clientId })
        }
    }, [clientId, shouldRetry, load])
}