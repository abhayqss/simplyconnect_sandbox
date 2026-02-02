const { Record, List } = require('immutable')

const IncidentNotifiedPerson = Record({
    id: null,
    name: '',
    phone: '',
    email: '',
    relationship: '',
    notifiedDate: null,
    notificationChannels: List(),
})

export default IncidentNotifiedPerson
