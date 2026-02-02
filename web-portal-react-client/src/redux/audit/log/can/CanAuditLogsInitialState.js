import View from './view/CanViewAuditLogsInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    view: View()
})

export default InitialState