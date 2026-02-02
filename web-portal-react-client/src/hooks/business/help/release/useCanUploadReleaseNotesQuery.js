import { useQuery } from 'hooks/common/redux'

import actions from 'redux/help/release/note/can/upload/canUploadReleaseNoteActions'

export default function useCanUploadReleaseNotesQuery() {
    useQuery(actions, null)
}