import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/client/can/edit/canEditClientActions'

export default function useCanEditClientQuery(params, options) {
    useQuery(actions, params, options)
}