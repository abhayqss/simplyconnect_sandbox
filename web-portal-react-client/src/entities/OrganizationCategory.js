const { Record } = require('immutable')

const OrganizationCategory = Record({
    id: null,
    name: '',
    color: '',
    organizationId: null,
})

export default OrganizationCategory
