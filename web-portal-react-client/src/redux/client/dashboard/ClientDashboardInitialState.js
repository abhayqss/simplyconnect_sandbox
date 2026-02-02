import Note from './note/ClientNoteInitialState'
import Event from './event/ClientEventInitialState'
import Assessment from './assessment/ClientAssessmentInitialState'
import ServicePlan from './servicePlan/ClientServicePlanInitialState'

const { Record } = require('immutable')

export default Record({
    note: Note(),
    event: Event(),
    assessment: Assessment(),
    servicePlan: ServicePlan()
})