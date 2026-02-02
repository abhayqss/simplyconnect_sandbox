import Goal from 'entities/Goal'

const { Record } = require('immutable')

export default Record({
    //by index and needIndex we can find a required Goal
    index: 0,
    needIndex: 0,
    error: null,
    isValid: true,
    fields: Goal()
})
