import { useEffect } from 'react'

import useBoundActions from '../../common/redux/useBoundActions'

import * as noteAdmittanceListActions from 'redux/directory/note/admittance/list/noteAdmittanceListActions'

function useAdmittancesQuery(clientId) {
    const load = useBoundActions(noteAdmittanceListActions.load)

    useEffect(() => { load(clientId) }, [load, clientId])
}

export default useAdmittancesQuery
