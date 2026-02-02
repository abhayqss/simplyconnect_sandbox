import { State } from 'redux/utils/List'

const { Record } = require('immutable')

export default State({
    error: null,
    isFetching: false,
    shouldReload: false,
    dataSource: Record({
        data: [],
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0
        })(),
        filter: Record({
            clientId: null,
            communityIds: [],
            organizationId: null,
            eventTypeId: null,
            noteTypeId: null,
            fromDate: null,
            toDate: null,
            onlyEventsWithIR: false
        })()
    })()
})