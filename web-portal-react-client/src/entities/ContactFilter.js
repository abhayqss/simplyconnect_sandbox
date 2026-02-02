import { CONTACT_STATUSES } from 'lib/Constants'

const { Record } = require('immutable')

const { ACTIVE, PENDING } = CONTACT_STATUSES

const ContactFilter = Record({
	lastName: null,
	firstName: null,
	email: null,
	systemRoleIds: [],
	statuses: [ACTIVE, PENDING],
	includeWithoutSystemRole: true
})

export default ContactFilter
