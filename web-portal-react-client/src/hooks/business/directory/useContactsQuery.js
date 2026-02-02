import useQuery from 'hooks/common/redux/useQuery'

import { isInteger } from 'lib/utils/Utils'

import actions from 'redux/directory/contact/list/contactListActions'

export default function useContactsQuery(params, options) {
    useQuery(actions, params, {
        condition: prev => (
            isInteger(params.organizationId)
            && (
                prev.name !== params.name
                || prev.organizationId !== params.organizationId
            )
        ),
        ...options
    })
}