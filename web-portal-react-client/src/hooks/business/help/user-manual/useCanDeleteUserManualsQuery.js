import { useQuery } from 'hooks/common/redux'

import actions from 'redux/help/user/manual/can/deletion/canDeleteUserManualActions'

export default function useCanDeleteUserManualsQuery() {
    useQuery(actions, null)
}