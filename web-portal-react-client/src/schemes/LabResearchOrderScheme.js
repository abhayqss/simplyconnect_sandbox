import {
    Shape,
    integer,
    string,
    ListOf,
    phoneNumber
} from './types'

import AddressScheme from './AddressScheme'

const trimAndUpperCase = value => value?.toString().toUpperCase().trim() || ''

const policyHolderRelationNameCondition = string().when(
    ['policyHolderRelationName', '$included'],
    (policyHolderRelationName, included, schema) => (
        included.excludedRelations.includes(policyHolderRelationName) || !policyHolderRelationName
            ? schema.nullable()
            : schema.required()
    )
)

const customIcdCode = string().test({
    name: 'customIcdCode',
    message: 'There are duplicate ICD-10 codes',
    test(value) {
        value = trimAndUpperCase(value)

        if (!value) {
            return true
        } else {
            const siblingValues = this.parent
                .toSeq()
                .filter((_, key) => !this.path.includes(key))
                .map(trimAndUpperCase)

            let { icd10Codes } = this.options.context.included.selected

            return (
                !icd10Codes.includes(value) &&
                !siblingValues.includes(value)
            )
        }
    }
})

const ClientScheme = Shape({
    id: integer().required(),
    raceId: integer().required(),
    genderId: integer().required(),
    birthDate: string().required(),
    phone: phoneNumber().required(),
    address: AddressScheme,
    insuranceNetwork: string().required(),
    policyNumber: string().required(),
    policyHolderRelationName: string().nullable().required(),
    policyHolderName: policyHolderRelationNameCondition,
    policyHolderDOB: policyHolderRelationNameCondition,
})

const SpecimenScheme = Shape({
    collectorName: string().required(),
    types: ListOf(string()).required(),
    date: integer().required(),
    site: string().required(),
})

/*const CustomIcdCodes = Shape({
    first: customIcdCode,
    second: customIcdCode,
    third: customIcdCode,
})*/

const LabOrderScheme = Shape({
    reason: string().required(),
    providerLastName: string().required(),
    providerFirstName: string().required(),
    icd10Codes: ListOf(string()).required(),

    client: ClientScheme,
    specimen: SpecimenScheme,

    // customIcdCodes: CustomIcdCodes, --// CCN-4398. Uncomment when it's needed
})

export default LabOrderScheme
