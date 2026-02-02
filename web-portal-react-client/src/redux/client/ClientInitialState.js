import Can from './can/CanClientInitialState'
import List from './list/ClientListInitialState'
import Form from './form/ClientFormInitialState'
import Count from './count/ClientCountInitialState'
import ClientRecord from './record/ClientRecordInitialState'
import Details from './details/ClientDetailsInitialState'
import History from './history/ClientHistoryInitialState'
import LastViewed from './lastViewed/ClientLastViewedInitialState'
import Unassociated from './unassociated/UnassociatedClientInitialState'

import Caseload from './caseload/CaseloadInitialState'
import Billing from './billing/ClientBillingInitialState'
import Emergency from './emergency/ClientEmergencyInitialState'

import Dashboard from './dashboard/ClientDashboardInitialState'

import Event from './event/EventInitialState'
import Allergy from './allergy/ClientAllergyInitialState'
import Problem from './problem/ClientProblemInitialState'
import Community from './community/CommunityInitialState'
import Document from './document/ClientDocumentInitialState'
import Assessment from './assessment/AssessmentInitialState'
import ServicePlan from './servicePlan/ServicePlanInitialState'
import Medication from './medication/ClientMedicationInitialState'
import CareTeamMember from './careTeamMember/CareTeamMemberInitialState'

import Route from './route/ClientRouteInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    form: Form(),
    count: Count(),
    record: ClientRecord(),
    details: Details(),
    history: History(),
    lastViewed: LastViewed(),
    unassociated: Unassociated(),

    billing: Billing(),
    caseload: Caseload(),
    emergency: Emergency(),

    dashboard: Dashboard(),

    event: Event(),
    allergy: Allergy(),
    problem: Problem(),
    document: Document(),
    community: Community(),
    medication: Medication(),
    assessment: Assessment(),
    servicePlan: ServicePlan(),
    careTeamMember: CareTeamMember(),

    route: Route(),
})