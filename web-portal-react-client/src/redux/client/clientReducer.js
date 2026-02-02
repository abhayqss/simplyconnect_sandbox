import ClientInitialState from './ClientInitialState'

import listReducer from './list/clientListReducer'
import formReducer from './form/clientFormReducer'
import countReducer from './count/clientCountReducer'
import canClientReducer from './can/canClientReducer'
import recordReducer from './record/clientRecordReducer'
import detailsReducer from './details/clientDetailsReducer'
import historyReducer from './history/clientHistoryReducer'
import lastViewedReducer from './lastViewed/clientLastViewedReducer'
import unassociatedReducer from './unassociated/unassociatedClientReducer'

import caseloadReducer from './caseload/caseloadReducer'
import billingReducer from './billing/clientBillingReducer'
import emergencyReducer from './emergency/clientEmergencyReducer'

import dashboardReducer from './dashboard/clientDashboardReducer'

import eventReducer from './event/EventReducer'
import allergyReducer from './allergy/clientAllergyReducer'
import problemReducer from './problem/clientProblemReducer'
import communityReducer from './community/communityReducer'
import documentReducer from './document/clientDocumentReducer'
import assessmentReducer from './assessment/assessmentReducer'
import servicePlanReducer from './servicePlan/servicePlanReducer'
import medicationReducer from './medication/clientMedicationReducer'
import careTeamMemberReducer from './careTeamMember/careTeamMemberReducer'

import routeReducer from './route/clientRouteReducer'

const initialState = new ClientInitialState()

export default function clientReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const form = formReducer(state.form, action)
    if (form !== state.form) nextState = nextState.setIn(['form'], form)

    const count = countReducer(state.count, action)
    if (count !== state.count) nextState = nextState.setIn(['count'], count)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    const record = recordReducer(state.record, action)
    if (record !== state.record) nextState = nextState.setIn(['record'], record)

    const history = historyReducer(state.history, action)
    if (history !== state.history) nextState = nextState.setIn(['history'], history)

    const lastViewed = lastViewedReducer(state.lastViewed, action)
    if (lastViewed !== state.lastViewed) nextState = nextState.setIn(['lastViewed'], lastViewed)

    const unassociated = unassociatedReducer(state.unassociated, action)
    if (unassociated !== state.unassociated) nextState = nextState.setIn(['unassociated'], unassociated)

    const billing = billingReducer(state.billing, action)
    if (billing !== state.billing) nextState = nextState.setIn(['billing'], billing)

    const emergency = emergencyReducer(state.emergency, action)
    if (emergency !== state.emergency) nextState = nextState.setIn(['emergency'], emergency)
    
    const dashboard = dashboardReducer(state.dashboard, action)
    if (dashboard !== state.dashboard) nextState = nextState.setIn(['dashboard'], dashboard)

    const caseload = caseloadReducer(state.caseload, action)
    if (caseload !== state.caseload) nextState = nextState.setIn(['caseload'], caseload)

    const event = eventReducer(state.event, action)
    if (event !== state.event) nextState = nextState.setIn(['event'], event)

    const allergy = allergyReducer(state.allergy, action)
    if (allergy !== state.allergy) nextState = nextState.setIn(['allergy'], allergy)

    const problem = problemReducer(state.problem, action)
    if (problem !== state.problem) nextState = nextState.setIn(['problem'], problem)
    
    const document = documentReducer(state.document, action)
    if (document !== state.document) nextState = nextState.setIn(['document'], document)

    const community = communityReducer(state.community, action)
    if (community !== state.community) nextState = nextState.setIn(['community'], community)

    const medication = medicationReducer(state.medication, action)
    if (medication !== state.medication) nextState = nextState.setIn(['medication'], medication)
    
    const assessment = assessmentReducer(state.assessment, action)
    if (assessment !== state.assessment) nextState = nextState.setIn(['assessment'], assessment)

    const servicePlan = servicePlanReducer(state.servicePlan, action)
    if (servicePlan !== state.servicePlan) nextState = nextState.setIn(['servicePlan'], servicePlan)

    const careTeamMember = careTeamMemberReducer(state.careTeamMember, action)
    if (careTeamMember !== state.careTeamMember) nextState = nextState.setIn(['careTeamMember'], careTeamMember)

    const can = canClientReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    const route = routeReducer(state.route, action)
    if (route !== state.route) nextState = nextState.setIn(['route'], route)

    return nextState
}