import {
    bool,
    func,
    string,
    object,
    arrayOf
} from 'prop-types'

export default {
    data: arrayOf(object),
    pagination: object,
    isFetching: bool,
    noDataText: string,
    onSort: func,
    onRefresh: func,
    className: string
}