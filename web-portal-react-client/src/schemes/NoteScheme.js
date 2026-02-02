
import { number, object, string } from 'yup'

import EncounterScheme from './EncounterScheme'

const NoteScheme = object().shape({
    noteDate: number().nullable().required(),
    subTypeId: number().nullable().required(),
    subjective: string().min(10).required(),
    admitDateId: number().nullable().required(),
    encounter: EncounterScheme.when(['subTypeId', '$included'], (subTypeId, included, schema) => {
        const { EncounterSubTypes = [] } = included

        return EncounterSubTypes.includes(subTypeId) ? schema : object()
    }),
})

export default NoteScheme
