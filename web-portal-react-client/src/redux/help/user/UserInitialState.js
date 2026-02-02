import Manual from './manual/UserManualInitialState'

const { Record } = require('immutable')

export default Record({
    manual: Manual()
})