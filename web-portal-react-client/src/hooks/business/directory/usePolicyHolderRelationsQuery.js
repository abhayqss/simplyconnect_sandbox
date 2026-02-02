import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/policy-holder-relation/list/labResearchPolicyHolderRelationListActions'

export default function usePolicyHolderRelationsQuery() {
    useQuery(actions, null)
}
