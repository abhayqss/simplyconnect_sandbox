import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/directory/organization/list/organizationListActions'

export default function useOrganizationsQuery(params, options) {
    useQuery(actions, params, options)
}