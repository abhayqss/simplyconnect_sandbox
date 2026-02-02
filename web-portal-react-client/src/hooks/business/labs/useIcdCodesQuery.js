import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/icd-code/list/labResearchIcdCodeListActions'

export default function useIcdCodesQuery() {
    useQuery(actions, null)
}
