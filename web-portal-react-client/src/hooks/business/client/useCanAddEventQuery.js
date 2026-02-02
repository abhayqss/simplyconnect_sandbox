import { useQuery } from 'hooks/common/redux'

import actions from 'redux/event/can/add/canAddEventActions'

export default function useCanAddEventQuery(params, options) {
    useQuery(actions, params, options)
}