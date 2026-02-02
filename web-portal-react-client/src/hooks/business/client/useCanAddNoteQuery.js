import { useQuery } from 'hooks/common/redux'

import actions from 'redux/note/can/add/canAddNoteActions'

export default function useCanAddNoteQuery(params, options) {
    useQuery(actions, params, options)
}