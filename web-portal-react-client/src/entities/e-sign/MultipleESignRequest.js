const {
    Set,
    Record
} = require('immutable')

const MultipleESignBulkRequest = Record({
    templateId: null,
    communities: Set(),
    organizationId: null,
    clientIds:Set(),
    submitters:Set(),
    signOrderEmails:Set(),
})

export default MultipleESignBulkRequest
