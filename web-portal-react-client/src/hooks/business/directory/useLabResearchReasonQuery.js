import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/reason/list/labResearchReasonListActions'

export default function useLabResearchReasonQuery() {
    useQuery(actions, null)
}
