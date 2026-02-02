import { useQuery } from 'hooks/common/redux'

import actions from 'redux/lab/can/view/canViewLabActions'

export default function useCanViewLabQuery() {
    useQuery(actions, null)
}
