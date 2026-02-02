const { Record } = require('immutable')

const ESignDocumentTemplate = Record({
    name: '',
    communityIds: [],
    type: "ORGANIZATION"
})

export default ESignDocumentTemplate
