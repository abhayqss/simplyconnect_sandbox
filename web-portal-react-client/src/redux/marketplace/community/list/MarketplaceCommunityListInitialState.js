import { State } from 'redux/utils/List'

const { Record } = require('immutable')

export default State({
    dataSource: Record({
        data: [],
        isFetching: false,
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0,
            isFetching: false
        })(),
        filter: Record({
            searchText: '',
            primaryFocusIds: [],
            communityTypeIds: [],
            servicesTreatmentApproachesIds: [],
            insuranceNetworkId: null,
            insurancePaymentPlanId: null
        })()
    })()
})