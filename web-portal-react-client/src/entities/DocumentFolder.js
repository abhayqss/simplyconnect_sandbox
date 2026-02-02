import PermissionsDataSource from './PermissionsDataSource'

const { Record, List } = require('immutable')

const DocumentFolder = Record({
    id: null,
    name: '',
    parentId: null,
    communityId: null,
    categoryIds: List(),
    isSecurityEnabled: false,
    permissions: PermissionsDataSource(),
})

export default DocumentFolder