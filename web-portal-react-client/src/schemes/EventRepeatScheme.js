import { Shape, ListOf, string, integer, bool } from './types'

const EventRepeatScheme = Shape({
    periodUnitName: string().required(),
    periodFrequency: integer().required(),
    startDate: integer().required(),
    noEndDate: bool(),
    until: integer().when(
        ['noEndDate'],
        (noEndDate, schema) => noEndDate ? integer() : schema.required()
    ),
    weekdays: ListOf(string()).when(
        ['$included'],
        (included, schema) => included.shouldValidateWeekdays
            ? schema.required()
            : ListOf(string())
    ),
})

export default EventRepeatScheme
