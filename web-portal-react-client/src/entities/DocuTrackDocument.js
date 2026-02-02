const { Record } = require('immutable')

const DocuTrackDocument = Record({
    pharmacyId: null,
    documentId: '',
    businessUnitCode: null,
    documentText: null,
})

export default DocuTrackDocument