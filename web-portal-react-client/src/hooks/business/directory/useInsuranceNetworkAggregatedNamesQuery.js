import actions from 'redux/directory/insurance/network/aggregated/list/insuranceNetworkAggregatedListActions'

import useQuery from 'hooks/common/redux/useQuery'

import { isInteger } from 'lib/utils/Utils'

export default function useInsuranceNetworkAggregatedNamesQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => {
            return (
                isInteger(params.organizationId)
                && (
                    params.text !== prevParams.text
                    || params.organizationId !== prevParams.organizationId
                )
            )
        },
        ...options
    })
}