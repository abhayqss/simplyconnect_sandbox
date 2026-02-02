import { useQuery } from 'hooks/common/redux'

import actions from 'redux/report/sdoh/can/view/canViewSDoHReportsActions'

export default function useCanViewSDoHReportsQuery() {
    useQuery(actions, null)
}