import { useQuery } from 'hooks/common/redux'

import actions from 'redux/help/release/note/can/deletion/canDeleteReleaseNoteActions'

export default function useCanDeleteReleaseNotesQuery() {
    useQuery(actions, null)
}