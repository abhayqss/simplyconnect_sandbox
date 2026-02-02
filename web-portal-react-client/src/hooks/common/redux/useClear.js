import { useEffect } from 'react'

import useBoundActions from 'hooks/common/redux/useBoundActions'

export default function useQuery(actions) {
    const clear = useBoundActions(actions.clear)

    useEffect(() => { clear() }, [ clear ])
}