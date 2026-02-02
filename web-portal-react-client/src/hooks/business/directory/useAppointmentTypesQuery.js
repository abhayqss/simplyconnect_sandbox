import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/appointment/type/list/appointmentTypeListActions'

export default function useAppointmentTypesQuery(options) {
    useQuery(actions, null, options)
}
