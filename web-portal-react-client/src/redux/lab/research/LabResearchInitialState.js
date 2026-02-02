import Order from './order/LabOrderInitialState'

const { Record } = require('immutable')

export default Record({
    order: Order()
})