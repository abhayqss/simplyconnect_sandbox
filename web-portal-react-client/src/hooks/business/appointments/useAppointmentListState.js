import { useListState } from 'hooks/common'

import Entity from 'entities/AppointmentCombinedFilter'

export default function useAppointmentListState() {
    return useListState({ filterEntity: Entity })
}