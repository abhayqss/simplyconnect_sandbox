import { useEffect } from 'react'

import { noop } from 'underscore'

import useRefCurrent from 'hooks/common/useRefCurrent'
import useBoundActions from 'hooks/common/redux/useBoundActions'

import { isInteger } from 'lib/utils/Utils'
import { Response } from 'lib/utils/AjaxUtils'

import * as actions from 'redux/client/community/list/communityListActions'

export default function useCommunitiesQuery(
    { organizationId }, { enabled = true, onSuccess = noop } = {}
) {
    const options = useRefCurrent({ onSuccess })

    const load = useBoundActions(actions.load)

    useEffect(() => {
        if (enabled && isInteger(organizationId)) {
            load({ organizationId }).then(Response(
                options.onSuccess
            ))
        }
    }, [load, enabled, organizationId, options])
}