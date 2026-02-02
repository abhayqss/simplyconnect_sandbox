import { State } from 'redux/utils/List'

const { Record } = require('immutable')

export default State({
    dataSource: Record({
        data: [],
        filter: Record({
            organizationId: null,
        })()
    })()
}, {
    isPageable: false,
    isSortable: false
})

