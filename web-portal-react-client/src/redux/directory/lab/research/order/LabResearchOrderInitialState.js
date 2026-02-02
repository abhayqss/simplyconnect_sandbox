import Status from './status/LabResearchOrderStatusInitialState'
import SpecimenType from './specimen-type/LabResearchSpecimenTypeInitialState'

const { Record } = require('immutable')

export default Record({
    status: Status(),
    specimenType: SpecimenType(),
})
