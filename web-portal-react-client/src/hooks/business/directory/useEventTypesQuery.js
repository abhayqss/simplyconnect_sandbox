import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/event/type/list/eventTypeListActions'

function useEventTypesQuery(params, options) {
    useQuery(actions, null, options)
}

export default useEventTypesQuery
