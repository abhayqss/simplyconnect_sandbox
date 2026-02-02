import { State } from 'redux/utils/List'

import { CLIENT_STATUSES } from 'lib/Constants'

const { Record } = require('immutable')

const { ACTIVE } = CLIENT_STATUSES

export default State({
    dataSource: Record({
        data: [],
        sorting: Record({
            field: 'fullName',
            order: 'asc'
        })(),
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0
        })(),
        filter: Record({
            communityIds: [],
            organizationId: null,

            unit: null,
            ssnLast4: null,
            genderId: null,
            lastName: null,
            firstName: null,
            birthDate: null,
            isAdmitted: false,
            pharmacyNames: [],
            programStatuses: [],
            medicareNumber: null,
            medicaidNumber: null,
            recordStatuses: [ACTIVE],
            hasNoPharmacies: false,
            primaryCarePhysician: null,
            insuranceNetworkAggregatedName: null
        })()
    })()
})

