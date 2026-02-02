import { useEffect } from 'react'

import { useDirectoryData } from 'hooks/common'

import useBoundActions from '../../common/redux/useBoundActions'

import noteEncounterTypeListActions from 'redux/directory/note/encounter/type/list/noteEncounterTypeListActions'

function useEncounterTypesQuery(shouldReload = false) {
    const { encounterTypes } = useDirectoryData({ encounterTypes: ['note', 'encounter', 'type'] })
    const load = useBoundActions(noteEncounterTypeListActions.load)

    useEffect(() => {
        if (!encounterTypes.length || shouldReload) {
            load()
        }
     }, [encounterTypes.length, load, shouldReload])
}

export default useEncounterTypesQuery
