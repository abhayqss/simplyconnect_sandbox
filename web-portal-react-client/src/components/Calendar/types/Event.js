import {
    shape,
    string,
    number,
    oneOfType
} from 'prop-types'

const TEvent = shape({
    id: oneOfType([number, string]),
    name: string,
    type: string,
    title: string,
    startDate: number,
    endDate: number
})

export default TEvent