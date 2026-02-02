import { useEffect } from 'react'

import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/directory/primaryFocus/list/primaryFocusListActions'

import { isInteger } from 'lib/utils/Utils'

function usePrimaryFocusQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && params.organizationId !== prevParams.organizationId
        ),
        ...options
    })
}

export default usePrimaryFocusQuery
