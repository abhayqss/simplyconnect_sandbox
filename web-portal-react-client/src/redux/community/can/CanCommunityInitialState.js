import Add from './add/CanAddCommunityCountInitialState'
import Configure from './configure/CanConfigureCommunityInitialState'

const { Record } = require('immutable');

export default Record({
    add: Add(),
    configure: Configure(),
})
