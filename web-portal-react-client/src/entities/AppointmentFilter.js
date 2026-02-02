const { Record } = require('immutable')

const AppointmentFilter = Record({
    creatorIds: [],
    clientIds: [],
    clientStatuses: [],
    serviceProviderIds: [],
    types: [],
    statuses: [],
    hasNoServiceProviders: false,
    isExternalProviderServiceProvider: false,
    clientsWithAccessibleAppointments: true,
})

export default AppointmentFilter
