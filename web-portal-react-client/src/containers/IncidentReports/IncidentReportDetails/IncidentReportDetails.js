import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    map,
    compact,
    findWhere
} from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import {
    Link,
    useParams,
    useHistory
} from 'react-router-dom'

import { Button } from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    Footer,
    Loader,
    Dropdown,
    ScrollTop,
    Breadcrumbs,
    ErrorViewer,
    BodyDiagram,
} from 'components'

import {
    ErrorDialog,
    ConfirmDialog
} from 'components/dialogs'

import {
    Detail
} from 'components/business/common'

import { Conversation } from 'factories'

import {
    useResponse,
    useDirectoryData,
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useIncidentTypesQuery,
    useIncidentPlacesQuery,
    useIncidentWeatherConditionTypesQuery
} from 'hooks/business/directory'

import {
    useConversations
} from 'hooks/business/conversations'

import {
    useIncidentReportDetails,
    useIncidentReportDownload
} from 'hooks/business/incident-report'

import { UpdateSideBarAction } from 'actions/admin'

import {
    incidentReportDeletionActions,
    incidentReportConversationJoinActions
} from 'redux/index'

import {
    SERVER_ERROR_CODES,
    ALLOWED_FILE_FORMATS,
    HIE_CONSENT_POLICIES,
    INCIDENT_REPORT_STATUSES,
    INCIDENT_REPORT_STATUS_COLORS
} from 'lib/Constants'

import {
    ifElse,
    isEmpty,
    isNotEmpty,
    DateUtils as DU,
    isNotNullOrUndefined
} from 'lib/utils/Utils'

import { path } from 'lib/utils/ContextUtils'
import { isNotEmptyOrBlank } from 'lib/utils/ObjectUtils'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import IncidentPicture from '../IncidentPicture/IncidentPicture'
import EventNotifications from '../EventNotifications/EventNotifications'
import IncidentReportEditor from '../IncidentReportEditor/IncidentReportEditor'
import IncidentReportChangeHistory from '../IncidentReportChangeHistory/IncidentReportChangeHistory'
import CTMemberCommunicationParticipantPicker from '../CTMemberCommunicationParticipantPicker/CTMemberCommunicationParticipantPicker'

import './IncidentReportDetails.scss'

const { format, formats } = DU

const TIME_FORMAT = formats.time
const DATE_FORMAT = formats.americanMediumDate
const DATE_TIME_FORMAT = formats.longDateMediumTime12

const { DRAFT } = INCIDENT_REPORT_STATUSES

const { PDF } = ALLOWED_FILE_FORMATS

function SubDetail({ title, children }) {
    return isNotEmpty(children) && (
        <div className="IncidentReportSubDetail">
            <div className="IncidentReportSubDetail-Title">{title}</div>
            <div className="IncidentReportSubDetail-Value">{children}</div>
        </div>
    )
}

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return {
        deletion: state.incident.report.deletion,
        areConversationsReady: state.conversations.isReady
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            deletion: bindActionCreators(incidentReportDeletionActions, dispatch),
            conversation: {
                join: bindActionCreators(incidentReportConversationJoinActions, dispatch)
            }
        }
    }
}

function IncidentReportDetails(
    {
        actions,

        deletion,
        areConversationsReady,

        hasActions = true,
        hasBreadcrumbs = true,

        ...props
    }
) {
    const [error, setError] = useState(null)
    const [isLoading, setLoading] = useState(false)

    const [conversation, setConversation] = useState(null)

    const [isEditorOpen, toggleEditor] = useState(false)
    const [isChangeHistoryDialogOpen, setChangeHistoryDialogOpen] = useState(false)
    const [isDeleteConfirmDialogOpen, setDeleteConfirmDialogOpen] = useState(false)

    const [communicationType, setCommunicationType] = useState(null)
    const [isCommunicationParticipantPickerOpen, toggleCommunicationParticipantPicker] = useState(false)
    const [isVideoCallParticipantExceedLimitDialogOpen, toggleVideoCallParticipantExceedLimitDialog] = useState(false)

    const params = useParams()
    const history = useHistory()
    const authUser = useAuthUser()

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const reportId = (
        props.reportId
        || parseInt(params.reportId)
        || undefined
    )

    const {
        places,
        weatherConditions
    } = useDirectoryData({
        places: ['incident', 'place'],
        weatherConditions: ['incident', 'weather', 'condition', 'type']
    })

    const {
        emit,
        create,
        getBySid,
    } = useConversations()

    const {
        state: {
            data,
            isFetching,
            error: fetchError,
        },
        fetch,
        refresh,
        clearError
    } = useIncidentReportDetails(reportId)

    const {
        fetch: download
    } = useIncidentReportDownload(reportId, { format: PDF })

    function downloadReport() {
        withDownloadingStatusInfoToast(() => download())
    }

    function closeEditor() {
        toggleEditor(false)
    }

    function closeDeleteConfirmDialog() {
        setDeleteConfirmDialogOpen(false)
    }

    const fetchConversationBySid = useCallback((sid) => {
        return getBySid(sid).then(o => {
            const conversation = Conversation(o)

            setConversation(conversation)

            return conversation
        })
    }, [getBySid])

    const joinToConversation = useCallback(() => (
        actions.conversation.join.send(data.id)
    ), [data, actions])

    const onClearError = useCallback(() => {
        clearError()
        setError(null)
        actions.deletion.clearError()
    }, [actions, clearError])

    const onCloseEditor = useCallback(closeEditor, [])

    const onSaveSuccess = useCallback(id => {
        history.push(path(`/incident-reports/${id}`))
        //refresh()
    }, [history, refresh])

    const onEditDraft = useCallback(() => toggleEditor(true), [])

    const onDeleteResponse = useResponse({
        onSuccess: () => {
            history.replace(path('/incident-reports'))
        }
    })

    const onCloseConversationParticipantPicker = useCallback(() => {
        toggleCommunicationParticipantPicker(false)
    }, [])

    const startConversation = useCallback(() => {
        const {
            conversationSid,
            isConversationParticipant
        } = data

        function navigate() {
            history.push(path('/chats'), { conversationSid })
        }

        if (isConversationParticipant) navigate()
        else joinToConversation().then(navigate).catch(setError)
    }, [data, history, joinToConversation])

    const onConversation = useCallback(() => {
        if (data.conversationSid) startConversation()
        else {
            setCommunicationType('conversation')
            toggleCommunicationParticipantPicker(true)
        }
    }, [data, startConversation])

    const startVideoCall = useCallback(conversation => {
        emit('attemptCall', {
            isVideoCall: true,
            conversationSid: conversation.sid,
            conversationFriendlyName: conversation.friendlyName,
            incidentReport: {
                id: data?.id,
                clientId: data?.client.id,
                communityId: data?.client.communityId
            }
        })
    }, [emit, data])

    const closeVideoCallParticipantExceedLimitDialog = useCallback(
        () => toggleVideoCallParticipantExceedLimitDialog(false), []
    )

    const onVideoCall = useCallback(() => {
        if (data.conversationSid) {
            const isChatMember = !!conversation

            const tryCalling = (conversation) => {
                if (conversation.participantIdentities.length > 20) {
                    toggleVideoCallParticipantExceedLimitDialog(true)
                } else {
                    startVideoCall(conversation)
                }
            }

            const execute = ifElse(
                () => isChatMember,
                (call) => call(conversation),
                (call) => joinToConversation()
                    .then(() => fetchConversationBySid(data.conversationSid))
                    .then(call)
                    .catch(setError)
            )

            execute(tryCalling)
        } else {
            setCommunicationType('video')
            toggleCommunicationParticipantPicker(true)
        }
    }, [data, conversation, startVideoCall, joinToConversation, fetchConversationBySid])

    const onCompleteCommunicationParticipantPicker = useCallback(({ contactIds, groupName }) => {
        const conversation = {
            friendlyName: groupName,
            incidentReportId: data?.id,
            employeeIds: [authUser?.id, ...contactIds]
        }

        if (communicationType === 'conversation') {
            history.push(path('/chats'), conversation)
        } else {
            setLoading(true)
            create(conversation).then(({ data: sid }) => {
                fetch()
                getBySid(sid).then(o => {
                    const cv = Conversation(o)
                    setConversation(cv)
                    startVideoCall(cv)
                    setLoading(false)
                })
            }).catch(e => {
                setError(e)
                setLoading(false)
            })

            toggleCommunicationParticipantPicker(false)
        }
    }, [
        data,
        fetch,
        create,
        history,
        authUser,
        getBySid,
        startVideoCall,
        communicationType
    ])

    function onConfirmDelete() {
        closeDeleteConfirmDialog()
        actions.deletion.delete(reportId).then(onDeleteResponse)
    }

    let content

    if (isFetching) {
        content = <Loader hasBackdrop />
    } else if (isEmpty(data)) {
        content = <h4>No Data</h4>
    } else {
        content = (
            <>
                {(isLoading || deletion.isFetching) && (
                    <Loader hasBackdrop />
                )}
                <div className="IncidentReportDetails-Header">
                    <div className="IncidentReportDetails-Title">
                        <div className="IncidentReportDetails-TitleText">
                            Incident Report
                        </div>
                        <div
                            className="badge Badge Badge_place_top-right IncidentReportDetails-Status"
                            style={{ backgroundColor: INCIDENT_REPORT_STATUS_COLORS[data.statusName] }}
                        >
                            {data.statusTitle}
                        </div>
                    </div>
                    {hasActions && !data.isArchived && (
                        <Dropdown
                            items={compact([
                                {
                                    value: 'DOWNLOAD',
                                    text: 'Download Pdf File',
                                    onClick: downloadReport
                                },
                                {
                                    value: 'CHANGE_HISTORY',
                                    text: 'View change history',
                                    onClick: () => setChangeHistoryDialogOpen(true)
                                },
                                data.client?.hieConsentPolicyName !== HIE_CONSENT_POLICIES.OPT_OUT && {
                                    value: 'TEXT_CARE_TEAM_MEMBERS',
                                    text: 'Text care team members',
                                    onClick: onConversation,
                                    isVisible: (
                                        data.client?.isActive
                                        && (data.hasCommunityCareTeamMembersWithEnabledConversations
                                            || data.client.hasAssignedCareTeamMembersWithEnabledConversations)
                                    )
                                },
                                data.client?.hieConsentPolicyName !== HIE_CONSENT_POLICIES.OPT_OUT && {
                                    value: 'CALL_CARE_TEAM_MEMBERS',
                                    text: 'Call care team members',
                                    onClick: onVideoCall,
                                    isVisible:
                                        data.client?.isActive
                                        && (data.hasCommunityCareTeamMembersWithEnabledVideoConversations
                                            || data.client.hasAssignedCareTeamMembersWithEnabledVideoConversations
                                        ) && data.conversationSid
                                },
                                {
                                    value: 'DELETE',
                                    text: 'Delete',
                                    onClick: () => setDeleteConfirmDialogOpen(true),
                                    isVisible: data.statusName === DRAFT
                                }
                            ]).filter(o => o.isVisible !== false)}
                            toggleText="More Options"
                            className="IncidentReportDetails-ActionDropdown"
                        />
                    )}
                    {hasActions && !data.isArchived && data.client?.isActive && (
                        <Button
                            color='success'
                            onClick={() => toggleEditor(true)}
                            className="IncidentReportDetails-ActionBtn"
                        >
                            Edit
                        </Button>
                    )}
                </div>
                <div className="IncidentReportDetails-Body">
                    {isNotEmptyOrBlank(data.client) && (
                        <div className="IncidentReportDetails-Section">
                            <div className="IncidentReportDetails-SectionTitle">
                                Client
                            </div>
                            <Detail
                                title="Client name"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value"
                                className="IncidentReportDetail"
                            >
                                {data.client.canView ? (
                                    <Link
                                        className='IncidentReportDetails-Client'
                                        to={path(`/clients/${data.client.id}`)}
                                    >
                                        {data.client.fullName}
                                    </Link>
                                ) : data.client.fullName}
                            </Detail>
                            <Detail
                                title="Unit #"
                                titleClassName="ReferralDetail-Title"
                                valueClassName="ReferralDetail-Value"
                                className="ReferralDetail"
                            >
                                {data.client.unit}
                            </Detail>
                            <Detail
                                title="Phone #"
                                titleClassName="ReferralDetail-Title"
                                valueClassName="ReferralDetail-Value"
                                className="ReferralDetail"
                            >
                                {data.client.phone}
                            </Detail>
                            <Detail
                                title="Site name"
                                titleClassName="ReferralDetail-Title"
                                valueClassName="ReferralDetail-Value"
                                className="ReferralDetail"
                            >
                                {data.client.siteName}
                            </Detail>
                            <Detail
                                title="Address"
                                titleClassName="ReferralDetail-Title"
                                valueClassName="ReferralDetail-Value"
                                className="ReferralDetail"
                            >
                                {data.client.address}
                            </Detail>
                        </div>
                    )}
                    <div className="IncidentReportDetails-Section">
                        <div className="IncidentReportDetails-SectionTitle">
                            Incident Information
                        </div>
                        <Detail
                            title="Date of Incident"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {format(data.incidentDate, DATE_FORMAT)}
                        </Detail>
                        <Detail
                            title="Time of Incident"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {format(data.incidentDate, TIME_FORMAT)}
                        </Detail>
                        <Detail
                            title="Date discovered by agency staff"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.incidentDiscoveredDate}
                        </Detail>
                        {isNotNullOrUndefined(data.wasProviderPresentOrScheduled) && (
                            <Detail
                                title="Did the incident occur when a provider was present or was scheduled to be present?"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value"
                                className="IncidentReportDetail"
                            >
                                {data.wasProviderPresentOrScheduled ? 'Yes' : 'No'}
                            </Detail>
                        )}
                        {isNotEmptyOrBlank(data.places) && (
                            <Detail
                                title="Where did the incident take place?"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {map(data.places, o => {
                                    const place = findWhere(places, { id: o.id })

                                    return place && (
                                        <div key={o.id} className="IncidentReportDetail-ValueItem">
                                            {place.name}{o.text ? `: ${o.text}` : ''}
                                        </div>
                                    )
                                })}
                            </Detail>
                        )}
                        {isNotEmptyOrBlank(data.weatherConditions) && (
                            <Detail
                                title="Weather conditions?"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {map(data.weatherConditions, o => {
                                    const condition = findWhere(weatherConditions, { id: o.id })

                                    return condition && (
                                        <div key={o.id} className="IncidentReportDetail-ValueItem">
                                            {condition.name}{o.text ? `: ${o.text}` : ''}
                                        </div>
                                    )
                                })}
                            </Detail>
                        )}
                        {isNotEmpty(data.incidentDetails) && (
                            <Detail
                                title="Details of the alleged incident"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {map(data.incidentDetails?.split('\n'), o => (
                                    <div key={o} className="IncidentReportDetail-ValueItem">
                                        {o}
                                    </div>
                                ))}
                            </Detail>
                        )}
                        {isNotNullOrUndefined(data.wasIncidentParticipantTakenToHospital) && (
                            <Detail
                                title="Was the participant taken to the hospital?"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.wasIncidentParticipantTakenToHospital ? 'Yes' : 'No'}
                            </Detail>
                        )}
                        <Detail
                            title="Hospital name"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                            className="IncidentReportDetail"
                        >
                            {data.incidentParticipantHospitalName}
                        </Detail>
                        {isNotNullOrUndefined(data.wereApparentInjuries) && (
                            <>
                                <Detail
                                    title="Where there apparent injuries?"
                                    titleClassName="IncidentReportDetail-Title"
                                    valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                    className="IncidentReportDetail"
                                >
                                    <span style={{ marginBottom: 20 }}>
                                        {data.wereApparentInjuries ? 'Yes' : 'No'}
                                    </span>
                                    {isNotEmpty(data.injuries) && (
                                        <BodyDiagram
                                            isDisabled
                                            value={data.injuries}
                                            className="IncidentReportDetail-BodyDiagram"
                                        />
                                    )}
                                </Detail>
                            </>
                        )}
                        <Detail
                            title="If injury, the current condition of the injured participant/resident"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                            className="IncidentReportDetail"
                        >
                            {data.currentInjuredClientCondition}
                        </Detail>
                    </div>
                    {isNotEmptyOrBlank(data.vitalSigns) && (
                        <div className="IncidentReportDetails-Section">
                            <div className="IncidentReportDetails-SectionTitle">
                                Vital Signs
                            </div>
                            <Detail
                                title="Blood pressure"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.vitalSigns.bloodPressure}
                            </Detail>
                            <Detail
                                title="Pulse"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.vitalSigns.pulse}
                            </Detail>
                            <Detail
                                title="Respiration rate"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.vitalSigns.respirationRate}
                            </Detail>
                            <Detail
                                title="Temperature"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.vitalSigns.temperature}
                            </Detail>
                            <Detail
                                title="O2 Saturation"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.vitalSigns.o2Saturation}
                            </Detail>
                            <Detail
                                title="Blood sugar"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.vitalSigns.bloodSugar}
                            </Detail>
                        </div>
                    )}
                    {isNotEmptyOrBlank(data.witnesses) && (
                        <div className="IncidentReportDetails-Section">
                            <div className="IncidentReportDetails-SectionTitle">
                                Witness
                            </div>
                            {map(data.witnesses, (o, i) => (
                                <Detail
                                    title={`Witness ${i + 1}`}
                                    titleClassName="IncidentReportDetail-Title"
                                    valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                    className="IncidentReportDetail"
                                >
                                    <div className="IncidentReportDetail-ValueItem">
                                        {o.name}, {o.relationship}, {o.phone}
                                    </div>
                                    <div className="IncidentReportDetail-ValueItem">
                                        {o.report}
                                    </div>
                                </Detail>
                            ))}
                        </div>
                    )}
                    {isNotNullOrUndefined(data.wereOtherIndividualsInvolved) && (
                        <div className="IncidentReportDetails-Section">
                            <div className="IncidentReportDetails-SectionTitle">
                                Individuals involved in the incident
                            </div>
                            <Detail
                                title="Were other individuals involved in the incident?"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                className="IncidentReportDetail"
                            >
                                {data.wereOtherIndividualsInvolved ? 'Yes' : 'No'}
                            </Detail>
                            {map(data.involvedIndividuals, (o, i) => (
                                <Detail
                                    key={o.id}
                                    title={`Individual ${i + 1}`}
                                    titleClassName="IncidentReportDetail-Title"
                                    valueClassName="IncidentReportDetail-Value"
                                    className="IncidentReportDetail"
                                >
                                    {o.name}, {o.relationship}, {o.phone}
                                </Detail>
                            ))}
                        </div>
                    )}
                    {isNotEmptyOrBlank(data.incidentPictures) && (
                        <div className="IncidentReportDetails-Section">
                            <div className="IncidentReportDetails-SectionTitle">
                                Pictures of the scene
                            </div>
                            {map(data.incidentPictures, o => (
                                <IncidentPicture
                                    {...o}
                                    style={{
                                        width: 100,
                                        height: 100
                                    }}
                                />
                            ))}
                        </div>
                    )}
                    {(data.eventNotificationCount > 0 || isNotEmptyOrBlank(data.notification)) && (
                        <div className="IncidentReportDetails-Section">
                            <div className="IncidentReportDetails-SectionTitle">
                                Notified
                            </div>
                            {data.eventNotificationCount > 0 && (
                                <>
                                    <div className="IncidentReportDetails-SubSectionTitle">
                                        Notified about Event
                                    </div>
                                    <EventNotifications
                                        eventId={data.eventId}
                                        clientId={data.client.id}
                                    />
                                </>
                            )}
                            {isNotEmptyOrBlank(data.notification) && (
                                <>
                                    <div className="IncidentReportDetails-SubSectionTitle">
                                        Notified about Incident
                                    </div>
                                    {isNotEmptyOrBlank(data.notification.family) && (
                                        <Detail
                                            title="Family"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.family.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.family.byWhom}
                                            </SubDetail>
                                            <SubDetail title="First and last name">
                                                {data.notification.family.fullName}
                                            </SubDetail>
                                            <SubDetail title="Phone #">
                                                {data.notification.family.phone}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.friend) && (
                                        <Detail
                                            title="Friend"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.friend.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.friend.byWhom}
                                            </SubDetail>
                                            <SubDetail title="First and last name">
                                                {data.notification.friend.fullName}
                                            </SubDetail>
                                            <SubDetail title="Phone #">
                                                {data.notification.friend.phone}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.physician) && (
                                        <Detail
                                            title="Physician"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.physician.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.physician.byWhom}
                                            </SubDetail>
                                            <SubDetail title="First and last name">
                                                {data.notification.physician.fullName}
                                            </SubDetail>
                                            <SubDetail title="Phone #">
                                                {data.notification.physician.phone}
                                            </SubDetail>
                                            <SubDetail title="Physician’s response">
                                                {data.notification.physician.response}
                                            </SubDetail>
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.physician.responseDate, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.adultProtectiveServices) && (
                                        <Detail
                                            title="Adult Protective Services"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.adultProtectiveServices.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.adultProtectiveServices.byWhom}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.careManager) && (
                                        <Detail
                                            title="Care Manager"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.careManager.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.careManager.byWhom}
                                            </SubDetail>
                                            <SubDetail title="First and last name">
                                                {data.notification.careManager.fullName}
                                            </SubDetail>
                                            <SubDetail title="Phone #">
                                                {data.notification.careManager.phone}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.ohioHealthDepartment) && (
                                        <Detail
                                            title="Ohio Department of Health"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.ohioHealthDepartment.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.ohioHealthDepartment.byWhom}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.emergency) && (
                                        <Detail
                                            title="9-1-1"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.emergency.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.emergency.byWhom}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.police) && (
                                        <Detail
                                            title="Police"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.police.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.police.byWhom}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                    {isNotEmptyOrBlank(data.notification.other) && (
                                        <Detail
                                            title="Other"
                                            titleClassName="IncidentReportDetail-Title"
                                            valueClassName="IncidentReportDetail-Value d-flex flex-column"
                                            className="IncidentReportDetail"
                                        >
                                            <SubDetail title="Date & Time">
                                                {format(data.notification.other.date, DATE_TIME_FORMAT)}
                                            </SubDetail>
                                            <SubDetail title="By Whom">
                                                {data.notification.other.byWhom}
                                            </SubDetail>
                                            <SubDetail title="Comment">
                                                {data.notification.other.comment}
                                            </SubDetail>
                                        </Detail>
                                    )}
                                </>
                            )}
                            <Detail
                                title="Immediate Intervention"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value"
                                className="IncidentReportDetail"
                            >
                                {data.immediateIntervention}
                            </Detail>
                            <Detail
                                title="Follow Up Information"
                                titleClassName="IncidentReportDetail-Title"
                                valueClassName="IncidentReportDetail-Value"
                                className="IncidentReportDetail"
                            >
                                {data.followUpInformation}
                            </Detail>
                        </div>
                    )}
                    <div className="IncidentReportDetails-Section">
                        <div className="IncidentReportDetails-SectionTitle">
                            Reporting / Other
                        </div>
                        <Detail
                            title="Completed By Whom"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.completedBy}
                        </Detail>
                        <Detail
                            title="Position"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.completedByPosition}
                        </Detail>
                        <Detail
                            title="Phone number"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.completedByPhone}
                        </Detail>
                        <Detail
                            title="Date Completed"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {format(data.completedDate, DATE_FORMAT)}
                        </Detail>
                        <Detail
                            title="Reported By Whom"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.reportedBy}
                        </Detail>
                        <Detail
                            title="Position"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.reportedByPosition}
                        </Detail>
                        <Detail
                            title="Phone #"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {data.reportedByPhone}
                        </Detail>
                        <Detail
                            title="Date & Time of report"
                            titleClassName="IncidentReportDetail-Title"
                            valueClassName="IncidentReportDetail-Value"
                            className="IncidentReportDetail"
                        >
                            {format(data.reportDate, DATE_TIME_FORMAT)}
                        </Detail>
                    </div>
                </div>
            </>
        )
    }

    useIncidentTypesQuery()
    useIncidentPlacesQuery()
    useIncidentWeatherConditionTypesQuery()

    useEffect(() => {
        if (areConversationsReady && data?.conversationSid) {
            fetchConversationBySid(data.conversationSid)
        }
    }, [data, areConversationsReady, fetchConversationBySid])

    useEffect(() => { fetch() }, [fetch])

    return (
        <DocumentTitle title="Simply Connect | Admin | Incident Report Details">
            <>
                <UpdateSideBarAction />
                <div className="IncidentReportDetails">
                    {hasBreadcrumbs && (
                        <Breadcrumbs
                            items={[
                                { title: 'Incident Reports', href: '/incident-reports', isEnabled: true },
                                { title: 'Incident Report Details', isActive: true }
                            ]}
                            className="margin-bottom-40"
                        />
                    )}
                    {content}
                    <ScrollTop
                        scrollable=".App-Content, .SideBar-Content"
                        scrollTopBtnClass="IncidentReportDetails-ScrollTopBtn"
                    />
                </div>
                {isDeleteConfirmDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText="Confirm"
                        title="The incident report will be deleted"
                        onConfirm={onConfirmDelete}
                        onCancel={closeDeleteConfirmDialog}
                    />
                )}
                <IncidentReportEditor
                    reportId={reportId}
                    eventId={data?.eventId}
                    clientId={data?.client?.id}
                    isOpen={isEditorOpen}
                    onClose={onCloseEditor}
                    onSaveSuccess={onSaveSuccess}
                    onEditDraft={onEditDraft}
                />
                {isChangeHistoryDialogOpen && (
                    <IncidentReportChangeHistory
                        isOpen={true}
                        reportId={reportId}
                        onClose={() => setChangeHistoryDialogOpen(false)}
                    />
                )}
                {isCommunicationParticipantPickerOpen && (
                    <CTMemberCommunicationParticipantPicker
                        isOpen
                        clientId={data.client.id}
                        communityId={data.client.communityId}
                        communicationType={communicationType}
                        onClose={onCloseConversationParticipantPicker}
                        onComplete={onCompleteCommunicationParticipantPicker}
                    />
                )}
                {isVideoCallParticipantExceedLimitDialogOpen && (
                    <ErrorDialog
                        isOpen
                        title="You can add up to 20 participants"
                        buttons={[
                            {
                                text: 'Close',
                                onClick: closeVideoCallParticipantExceedLimitDialog
                            }
                        ]}
                    />
                )}
                {(
                    error
                    || deletion.error
                    || (fetchError && !isIgnoredError(fetchError))
                ) && (
                        <ErrorViewer
                            isOpen
                            error={(
                                error
                                || fetchError
                                || deletion.error
                            )}
                            onClose={onClearError}
                        />
                    )}
                <Footer theme='gray'/>
            </>
        </DocumentTitle>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(IncidentReportDetails)