import DocumentFilter from './DocumentFilter'

const { Record } = require('immutable')

const DocumentCombinedFilter = Record({
    organizationId: null,
    communityId: null,
    folderId: null,
    folderName: null,
    isSecurityEnabled: false,
    ...DocumentFilter().toJS()
})

export default DocumentCombinedFilter