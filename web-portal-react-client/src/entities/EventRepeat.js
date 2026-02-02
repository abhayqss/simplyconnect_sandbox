const { Record, Set } = require('immutable')

const EventRepeat = Record({
    startDate: null,
    until: null,
    weekdays: Set(),
    periodFrequency: null,
    periodUnitName: null,

    // Not a part of the DTO
    noEndDate: false,
})

export default EventRepeat
