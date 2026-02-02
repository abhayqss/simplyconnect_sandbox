import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/system/role/list/systemRoleListActions'

export default function useSystemRolesQuery(
    {
        isEditable = false,
        includeExternal = false
    } = {},
    options
) {
    useQuery(actions, { isEditable, includeExternal }, options)
}
