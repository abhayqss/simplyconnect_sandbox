import { useQuery } from 'hooks/common/redux'

import actions from 'redux/lab/research/order/can/add/canAddLabResearchOrderActions'

export default function useCanAddLabResearchOrderQuery(params, options) {
    useQuery(actions, params, options)
}