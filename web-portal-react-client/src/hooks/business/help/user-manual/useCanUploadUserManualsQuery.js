import { useQuery } from 'hooks/common/redux'

import actions from 'redux/help/user/manual/can/upload/canUploadUserManualActions'

export default function useCanUploadUserManualsQuery() {
    useQuery(actions, null)
}