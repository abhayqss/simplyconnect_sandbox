import actions from 'redux/event/note/composed/count/eventNoteComposedCountActions'

import useQuery from './useQuery'
import useClear from '../../common/redux/useClear'

export default function useDocumentCountQuery(params, options) {
    useClear(actions)
    useQuery(actions, params, options)
}