import { State } from 'redux/utils/List'

const { Record } = require('immutable')

export default State({
    dataSource: Record({
        data: [],
        sorting: Record({
            field: null,
            order: null
        })(),
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0
        })(),
        filter: Record({
            organizationId: null,
            communityIds: [],
            clientId: null,
            requisitionNumber: null,
            reasons: [],
            statuses: []
        })(),
    })()
})
