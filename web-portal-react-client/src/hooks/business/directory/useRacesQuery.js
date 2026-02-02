import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import actions from 'redux/directory/race/list/raceListActions'

export default function useRacesQuery(shouldReload = false) {
    const { races } = useDirectoryData({
        races: ['race']
    })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (!races.length || shouldReload) {
            load()
        }
    }, [load, races.length, shouldReload])
}
