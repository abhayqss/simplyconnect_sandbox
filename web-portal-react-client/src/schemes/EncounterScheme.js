import { number, object, string } from 'yup'

const EncounterValidation = object().shape({
    typeId: number().nullable().required(),
    toDate: number().nullable().required(),
    fromDate: number().nullable().required(),
    clinician: string().required(),
})

export default EncounterValidation
