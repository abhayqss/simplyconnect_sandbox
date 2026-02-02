const { Set, Record } = require('immutable')

const ESignRequest = Record({ 
    recipientId: null,
    recipientType: null,
    recipientFullName: null,
    notificationMethod: null,
    expirationDate: null,
    phone: null,
    email: null,
    message: null,
    templateIds: Set()
})

export default ESignRequest
