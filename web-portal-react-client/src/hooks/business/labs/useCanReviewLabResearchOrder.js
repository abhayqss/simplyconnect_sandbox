import { isEqual } from 'underscore'
import { useQuery } from 'hooks/common/redux'

import actions from 'redux/lab/research/order/can/review/canReviewLabResearchOrderActions'

import { isInteger } from 'lib/utils/Utils'

export default function useCanReviewLabResearchOrder(params) {
    useQuery(actions, params, {
        condition: prev => isInteger(params.organizationId) && !isEqual(params, prev)
    })
}