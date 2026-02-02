import Status from './status/ServiceControlRequestStatusInitialState'

const { Record } = require('immutable')

export default Record({
    status: Status()
})