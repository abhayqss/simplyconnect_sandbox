import { useQuery } from 'hooks/common/redux'

import actions from 'redux/referral/request/contact/list/referralRequestContactListActions'

import { isInteger } from 'lib/utils/Utils'

export default function useRequestContactsQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.requestId)
            && params.requestId !== prevParams.requestId
        ),
        ...options
    })
}