import {
    shape,
    string
} from 'prop-types'

const TEvent = shape({
    name: string,
    title: string,
    type: string,
    text: string
})

export default TEvent