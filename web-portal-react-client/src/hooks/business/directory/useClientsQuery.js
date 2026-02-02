import useQuery from 'hooks/common/redux/useQuery'

import actions from 'redux/directory/client/list/clientListActions'

export default function useClientsQuery(params, options) {
    return useQuery(actions, params, options)
}
