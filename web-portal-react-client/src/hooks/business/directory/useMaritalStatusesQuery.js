import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import * as actions from 'redux/directory/marital/status/list/maritalStatusListActions'

export default function useMaritalStatusesQuery(shouldReload = false) {
    const { maritalStatuses } = useDirectoryData({
        maritalStatuses: ['marital', 'status']
    })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (!maritalStatuses.length || shouldReload) {
            load()
        }
    }, [load, maritalStatuses.length, shouldReload])
}
