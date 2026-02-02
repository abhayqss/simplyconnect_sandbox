import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/lab/research/order/specimen-type/list/labResearchSpecimenTypeListActions'

export default function useSpecimenTypesQuery() {
    useQuery(actions, null)
}
