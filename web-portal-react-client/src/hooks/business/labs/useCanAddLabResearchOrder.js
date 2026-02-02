import { isEqual } from 'underscore'

import { useQuery } from 'hooks/common/redux'

import actions from 'redux/lab/research/order/can/add/canAddLabResearchOrderActions'

import { isInteger } from 'lib/utils/Utils'

export default function useCanAddLabResearchOrder(params) {
    useQuery(actions, params , {
        condition: prevParams => (
            (isInteger(params.communityId) || isInteger(params.clientId))
            && !isEqual(params, prevParams)
        ),
    })
}