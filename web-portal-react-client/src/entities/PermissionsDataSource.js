const { Record, List } = require('immutable')

const PermissionList = Record({
    data: List(),
    pagination: Record({
        page: 1,
        size: 5,
        totalCount: 0
    })(),
    filter: {
        name: '',
    },
    getFilteredData() {
        return this.data.filter(o => {
            const name = this.filter.name.toLowerCase()

            return !name || o.contactFullName
                .toLowerCase()
                .includes(name)
        })
    },
    getData() {
        const { page, size } = this.pagination

        const from = page * size - size
        const to = from + size

        return this.getFilteredData().slice(from, to)
    },
})

export default PermissionList
