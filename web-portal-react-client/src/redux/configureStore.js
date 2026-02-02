import thunkMiddleware from 'redux-thunk'
import { routerMiddleware } from 'connected-react-router'
import { applyMiddleware, compose, createStore } from 'redux'

import rootReducer from './rootReducer'

import errorHandlerMiddleware from './middlewares/errorHadlerMiddlerware'

import Lab from './lab/LabInitialState'
import Note from './note/NoteInitialState'
import Auth from './auth/AuthInitialState'
import Help from './help/HelpInitialState'
import Audit from './audit/AuditInitialState'
import Login from './login/LoginInitialState'
import Event from './event/EventInitialState'
import Client from './client/ClientInitialState'
import Report from './report/ReportInitialState'
import Notify from './notify/NotifyInitialState'
import SideBar from './sidebar/SideBarInitialState'
import Contact from './contact/ContactInitialState'
import Document from './document/DocumentInitialState'
import Referral from './referral/ReferralInitialState'
import Incident from './incident/IncidentInitialState'
import Prospect from './prospect/ProspectInitialState'
import Insurance from './insurance/InsuranceInitialState'
import Directory from './directory/DirectoryInitialState'
import Community from './community/CommunityInitialState'
import GroupNote from './group-note/GroupNoteInitialState'
import VideoChat from './video-chat/VideoChatInitialState'
import Marketplace from './marketplace/MarketplaceInitialState'
import Appointment from './appointment/AppointmentInitialState'
import Organization from './organization/OrganizationInitialState'
import Transportation from './transportation/TransportationInitialState'

import Errors from './error/errorInitialState'
import Conversations from './conversations/ConversationsInitialState'

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ &&
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({ trace: true, traceLimit: 25 }) || compose

function getInitialState() {
    return {
        lab: Lab(),
        note: Note(),
        auth: Auth(),
        help: Help(),
        audit: Audit(),
        login: Login(),
        event: Event(),
        error: Errors(),
        client: Client(),
        report: Report(),
        notify: Notify(),
        sidebar: SideBar(),
        contact: Contact(),
        document: Document(),
        referral: Referral(),
        incident: Incident(),
        prospect: Prospect(),
        groupNote: GroupNote(),
        videoChat: VideoChat(),
        insurance: Insurance(),
        directory: Directory(),
        community: Community(),
        marketplace: Marketplace(),
        appointment: Appointment(),
        organization: Organization(),
        conversations: Conversations(),
        transportation: Transportation()
    }
}

export default function configureStore(history) {
    let store = createStore(
        rootReducer(history),
        getInitialState(),
        composeEnhancers(applyMiddleware(
            routerMiddleware(history),
            errorHandlerMiddleware,
            thunkMiddleware
        ))
    )

    if (process.env.NODE_ENV !== "production" && module.hot) {
        module.hot.accept("./rootReducer", () => {
            const newRootReducer = require("./rootReducer").default

            store.replaceReducer(newRootReducer)
        })
    }

    return store
}
