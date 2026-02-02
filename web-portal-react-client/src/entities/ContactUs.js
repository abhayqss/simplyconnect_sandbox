const { Record } = require('immutable')

const ContactUs = Record({
    phone: '',
    typeId: null,
    messageText: '',
    attachmentFiles: [],
})

export default ContactUs
