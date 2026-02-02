import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/directory/treatment/service/list/treatmentServiceListActions'

import { isInteger } from 'lib/utils/Utils'

export default function useTreatmentServicesQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && (
                params.organizationId !== prevParams.organizationId
                || params.communityIds !== prevParams.communityIds
            )
        ),
        ...options
    })
}
