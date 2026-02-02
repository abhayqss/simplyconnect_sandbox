const { Record, List } = require('immutable')

const Document = Record({
    title: '',
    document: Record({
        name: '',
        size: null,
        type: '',
    })(),
    folderId: null,
    description: '',
    categoryIds: List(),
    sharingOption: ''
})

export default Document
