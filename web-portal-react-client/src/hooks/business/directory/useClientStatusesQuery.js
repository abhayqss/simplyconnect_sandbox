import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/directory/client/status/list/clientStatusListActions'

export default function useClientStatusesQuery(options) {
    useQuery(actions, null, options)
}
