import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/note/type/list/noteTypeListActions'

function useNoteTypesQuery(params = null, options) {
    useQuery(actions, params, options)
}

export default useNoteTypesQuery
