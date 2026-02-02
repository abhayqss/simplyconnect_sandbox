const {
    Set,
    Record
} = require('immutable')

const ESignBulkRequest = Record({
    message: null,
    templateIds: Set(),
    communities: Set(),
    organizationId: null,
    expirationDate: null,
    whetherMultiplePeopleNeedToSign: null,
    selectedClientsOptions: Set(),

})

export default ESignBulkRequest
