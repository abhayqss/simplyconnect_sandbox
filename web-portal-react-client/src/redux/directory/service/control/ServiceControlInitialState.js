import Request from './request/ServiceControlRequestInitialState'

const { Record } = require('immutable')

export default Record({
    request: Request()
})