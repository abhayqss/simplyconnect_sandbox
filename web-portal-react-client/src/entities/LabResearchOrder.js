import Client from './Client'
import LabOrderSpecimen from './LabOrderSpecimen'

import { DateUtils as DU } from 'lib/utils/Utils'

const { Record, Set } = require('immutable')

const LabResearchOrder = Record({
    id: null,
    statusName: '',
    statusTitle: '',
    reason: '',
    clinic: '',
    clinicAddress: '',
    notes: '',
    providerFirstName: '',
    providerLastName: '',
    orderDate: Date.now(),

    icd10Codes: Set(['Z03.818']),

    client: Client(),
    specimen: LabOrderSpecimen(),

    // Not a part of the dto
    customIcdCodes: Record({
        // first: '', --// CCN-4398. Uncomment when it's needed
        second: '',
        third: '',

        secondIsDisabled: false, // CCN-4398. Remove it when it's needed
        thirdIsDisabled: false, // CCN-4398. Remove it when it's needed
    })(),
})

export default LabResearchOrder
