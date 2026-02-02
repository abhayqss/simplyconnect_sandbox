import Need from 'entities/Need'

const { Record } = require('immutable')

export default Record({
    //by this field we can find a required Need
    index: 0,
    error: null,
    isValid: true,
    fields: Need()
})