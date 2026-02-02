import Can from './can/CanLabInitialState'
import Research from './research/LabResearchInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    research: Research()
})