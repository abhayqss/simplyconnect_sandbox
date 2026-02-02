import { State } from 'redux/utils/List'

const { Record } = require('immutable')

export default State({
    dataSource: Record({
        data: [],
        pagination: Record({
            page: 1,
            size: 15,
            totalCount: 0
        })(),
        filter: Record({
            email: '',
            status: '',
            lastName: '',
            firstName: '',
            systemRole: '',
        })()
    })()
})
