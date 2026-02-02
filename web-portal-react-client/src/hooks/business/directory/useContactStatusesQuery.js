import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/directory/contact/status/list/contactStatusListActions'

export default function useContactStatusesQuery(options) {
    useQuery(actions, null, options)
}
