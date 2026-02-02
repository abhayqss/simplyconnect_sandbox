const { Record } = require('immutable')

const Inquiry = Record({
    email: null,
    phone: null,
    notes: null,
    status: null,
	lastName: null,
	firstName: null,
	serviceId: null,
	createdDate: null,
    communityId: null
})

export default Inquiry