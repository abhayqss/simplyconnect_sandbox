import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/order/status/list/labResearchOrderStatusListActions'

export default function useLabResearchStatusesQuery() {
    useQuery(actions, null)
}
