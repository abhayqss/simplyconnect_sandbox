import ContactFilter from './ContactFilter'

const { Record } = require('immutable')

const ContactCombinedFilter = Record({
    organizationId: null,
    communityIds: [],
    includeWithoutCommunity: null,
    ...ContactFilter().toJS()
})

export default ContactCombinedFilter
