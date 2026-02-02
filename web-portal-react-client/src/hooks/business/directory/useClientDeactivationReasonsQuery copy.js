import { useQuery } from 'hooks/common/redux'

import * as actions from 'redux/directory/gender/list/genderListActions'

//todo remove
export default function useGendersQuery(params = null, options) {
    useQuery(actions, params, options)
}
