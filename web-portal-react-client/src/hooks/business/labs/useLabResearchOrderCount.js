import { isEqual } from 'underscore'
import { useQuery } from 'hooks/common/redux'

import actions from 'redux/lab/research/order/count/labResearchOrderCountActions'

import { isInteger } from 'lib/utils/Utils'

export default function useLabResearchOrderCount(params) {
    useQuery(actions, params, {
        condition: prev => isInteger(params.organizationId) && !isEqual(params, prev)
    })
}