const { Record } = require('immutable')

const DocumentFilter = Record({
    title: null,
    description: null,
    categoryIds: [],
    signatureStatusNames: [],
    fromDate: null,
    toDate: null,
    includeDeleted: false,
    includeNotCategorized: null,
    includeWithoutSignature: null
})

export default DocumentFilter
