import React, {
	useRef,
	useState,
	useEffect,
	useCallback
} from 'react'

import $ from 'jquery'

import {
	useParams,
	useHistory
} from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import { Button } from 'reactstrap'

import {
	Loader,
	Dropdown,
	Breadcrumbs,
	DataLoadable
} from 'components'

import {
	SuccessDialog,
	WarningDialog
} from 'components/dialogs'

import {
	useQueryInvalidation
} from 'hooks/common'

import {
	useAuthUser
} from 'hooks/common/redux'

import {
	useTransportationRideHistoryQuery,
	useTransportationRideRequestQuery
} from 'hooks/business/transportaion'

import {
	useConversations
} from 'hooks/business/conversations'

import {
	useCanAddAppointmentsQuery
} from 'hooks/business/appointments'

import {
	useSideBarUpdate,
	useProspectQuery,
	useCanEditProspectQuery
} from 'hooks/business/Prospects'

import {
	useCanAddProspectEventQuery
} from 'hooks/business/Prospects/Events'

import {
	TRANSPORTATION_ACTION,
	TRANSPORTATION_ACTION_DESC
} from 'lib/Constants'

import { path } from 'lib/utils/ContextUtils'

import {
	isInteger,
	isNotEmpty,
	toNumberExcept,
	allAreNotEmpty
} from 'lib/utils/Utils'

import ProspectEditor from '../ProspectEditor/ProspectEditor'
import ProspectActivationEditor from '../ProspectActivationEditor/ProspectActivationEditor'
import ProspectDeactivationEditor from '../ProspectDeactivationEditor/ProspectDeactivationEditor'

import {
	ProspectDetails,
	ProspectDocumentsDevicesSummary
} from './'

import './ProspectDashboard.scss'

const EDIT = 'EDIT'
const CREATE_EVENT = 'CREATE_EVENT'
const TRANSPORTATION_REQUEST_A_NEW_RIDE = 'TRANSPORTATION_REQUEST_A_NEW_RIDE'
const TRANSPORTATION_RIDE_HISTORY = 'TRANSPORTATION_RIDE_HISTORY'
const CARE_TEAM = 'CARE_TEAM'
const ACTIVATE_CLIENT = 'ACTIVATE_CLIENT'
const DEACTIVATE_CLIENT = 'DEACTIVATE_CLIENT'
const CHAT = 'CHAT'
const VIDEO_CHAT = 'VIDEO_CHAT'
const CREATE_APPOINTMENT = 'CREATE_APPOINTMENT'
const CONVERT_TO_CLIENT = 'CONVERT_TO_CLIENT'

const OPTIONS = [
	{ name: TRANSPORTATION_REQUEST_A_NEW_RIDE, title: 'Request a New Ride', value: 3 },
	{ name: TRANSPORTATION_RIDE_HISTORY, title: 'Ride History', value: 4 },
	{ name: CHAT, title: 'Chat' },
	{ name: VIDEO_CHAT, title: 'Video', hasSeparator: true },
	{ name: EDIT, title: 'Edit Prospect Record' },
	{ name: ACTIVATE_CLIENT, title: 'Activate' },
	{ name: DEACTIVATE_CLIENT, title: 'Deactivate' },
	//{ name: CREATE_APPOINTMENT, title: 'Create Appointment' },
	//{ name: CONVERT_TO_CLIENT, title: 'Convert to Client' }
]

export function isDataValid(data = {}) {
	const {
		lastName,
		firstName,
		address,
		genderId,
		birthDate
	} = data

	const {
		zip,
		city,
		street,
		stateId
	} = address || {}

	const phone = (
		data?.cellPhone
		|| data?.phone
		|| data?.communityPhone
		|| data?.organizationPhone
	)

	return allAreNotEmpty(
		lastName,
		firstName,
		phone,
		genderId,
		birthDate,
		zip,
		city,
		street,
		stateId
	)
}

function ProspectDashboard() {
	const [saved, setSaved] = useState(null)

	const [isEditorOpen, setEditorOpen] = useState(false)

	const [isInvalidDataWarningDialogOpen, setInvalidDataWarningDialogOpen] = useState(false)

	const [transportationAction, setTransportationAction] = useState(null)

	const [isEventEditorOpen, setEventEditorOpen] = useState(false)
	const [isAppointmentEditorOpen, setAppointmentEditorOpen] = useState(false)
	const [isEventSaveSuccessDialogOpen, setEventSaveSuccessDialogOpen] = useState(false)

	const [isActivationEditorOpen, setActivationEditorOpen] = useState(false)
	const [isDeactivationEditorOpen, setDeactivationEditorOpen] = useState(false)

	const [isAppointmentSaveSuccessDialogOpen, setAppointmentSaveSuccessDialogOpen] = useState(false)

	const transportationFormRef = useRef()

	const params = useParams()
	const history = useHistory()

	const prospectId = toNumberExcept(
		params.prospectId, [null, undefined]
	)

	const user = useAuthUser()

	const invalidate = useQueryInvalidation()

	const {
		isFetching,
		data: prospect
	} = useProspectQuery({ prospectId }, {
		staleTime: 0,
		enabled: isInteger(prospectId)
	})

	const updateSideBar = useSideBarUpdate({ prospectId })

	const isActive = prospect?.isActive

	const contact = prospect?.associatedContact

	const {
		data: canEdit
	} = useCanEditProspectQuery({ prospectId }, {
		staleTime: 0, enabled: isInteger(prospectId)
	})

	const {
		data: canAddEvent
	} = useCanAddProspectEventQuery({ prospectId }, {
		staleTime: 0, enabled: isInteger(prospectId)
	})

	const { emit } = useConversations()

	const {
		data: canAddAppointment
	} = useCanAddAppointmentsQuery({
		organizationId: prospect?.organizationId
	}, {
		staleTime: 0,
		enabled: isInteger(prospect?.organizationId)
	})

	const {
		mutateAsync: fetchTransportationRideHistory
	} = useTransportationRideHistoryQuery({ prospectId })

	const {
		mutateAsync: fetchTransportationRideRequest
	} = useTransportationRideRequestQuery({ prospectId })

	function submitTransportationForm(url, token, action = '') {
		if (allAreNotEmpty(url, token)) {
			const form = (
				transportationFormRef.current
			)

			$(form).attr('action', url)

			$(form).find('[name="payload"]').val(token)
			$(form).find('[name="action"]').val(action)

			form.submit()
		}
	}

	const createTransportationRideRequest = useCallback(() => {
		fetchTransportationRideRequest().then(data => {
			submitTransportationForm(data.url, data.token, 'create')
		})
	}, [fetchTransportationRideRequest])

	const openTransportationRideHistory = useCallback(() => {
		fetchTransportationRideHistory().then(data => {
			submitTransportationForm(data.url, data.token)
		})
	}, [fetchTransportationRideHistory])

	const onSelectOption = useCallback(async name => {
		switch (name) {
			case EDIT: {
				setEditorOpen(true)
				break
			}
			case CHAT:
				history.push(
					path(`/chats`),
					{
						employeeIds: [user?.id, contact?.id],
						conversationSid: contact?.conversationSid
					}
				)
				break
			case VIDEO_CHAT: {
				emit('attemptCall', {
					companionAvatarId: contact.avatarId,
					employeeIds: [user.id, contact?.id],
					conversationSid: contact?.conversationSid
				})
				break
			}
			case TRANSPORTATION_REQUEST_A_NEW_RIDE: {
				setTransportationAction(TRANSPORTATION_ACTION.RIDE)

				if (isDataValid(prospect) && (prospect.email || user?.email)) {
					createTransportationRideRequest()
				} else setInvalidDataWarningDialogOpen(true)
				break
			}
			case TRANSPORTATION_RIDE_HISTORY: {
				setTransportationAction(TRANSPORTATION_ACTION.HISTORY)

				if (isDataValid(prospect) && (prospect.email || user?.email)) {
					openTransportationRideHistory()
				} else setInvalidDataWarningDialogOpen(true)
				break
			}
			case CARE_TEAM: {
				history.push(path(`prospects/${prospectId}/care-team`))
				break
			}
			case ACTIVATE_CLIENT: {
				setActivationEditorOpen(true)
				break
			}
			case DEACTIVATE_CLIENT: {
				setDeactivationEditorOpen(true)
				break
			}
			case CREATE_APPOINTMENT: {
				setAppointmentEditorOpen(true)
				break
			}
		}
	}, [
		user,
		emit,
		contact,
		history,
		prospect,
		prospectId,
		openTransportationRideHistory,
		createTransportationRideRequest
	])

	const onSaveSuccess = useCallback(() => {
		setEditorOpen(false)
		setTransportationAction(null)
		setDeactivationEditorOpen(false)
		setActivationEditorOpen(false)

		invalidate('Prospect', { prospectId })

		invalidate('DocumentTemplateScheme',
			{ prospectId },
			{ isPartialKeyMatch: true }
		)
	}, [prospectId, invalidate])

	const onCloseEditor = useCallback(() => {
		setEditorOpen(false)
		setTransportationAction(null)
		setDeactivationEditorOpen(false)
		setActivationEditorOpen(false)
	}, [])

	const onEventSaveSuccess = useCallback(id => {
		setSaved({ id })
		setEventEditorOpen(false)
		setEventSaveSuccessDialogOpen(true)

		invalidate('EventNoteComposedCount', {}, {
			isPartialKeyMatch: true
		})
	}, [invalidate])

	const onAppointmentSaveSuccess = useCallback(appointmentId => {
		invalidate('Appointments')
		invalidate('Events', { prospectId, limit: 4 })

		setAppointmentEditorOpen(false)
		setAppointmentSaveSuccessDialogOpen(true)		
	}, [
		invalidate,
		prospectId,
		setAppointmentEditorOpen,
		setAppointmentSaveSuccessDialogOpen
	])

	useEffect(() => {
		updateSideBar()
	}, [updateSideBar])

	return (
		<DocumentTitle title="Simply Connect | Prospect Dashboard">
			<div className="ClientDashboard">
				<DataLoadable
					data={prospect}
					isLoading={isFetching}
				>
					{data => {
						const canStartConversation = (
							isInteger(contact?.id) && (
								contact?.conversationSid
								|| contact?.canStartConversation
							)
						)

						const canStartVideoCall = (
							isInteger(contact?.id)
							&& contact?.canStartVideoCall
						)

						const permissions = {
							[EDIT]: canEdit && isActive,
							[CREATE_EVENT]: canAddEvent && isActive,
							[CREATE_APPOINTMENT]: canAddAppointment && isActive,
							[CHAT]: canStartConversation && isActive,
							[VIDEO_CHAT]: canStartVideoCall && isActive,
							[ACTIVATE_CLIENT]: canEdit && !isActive,
							[DEACTIVATE_CLIENT]: canEdit && isActive,
							[TRANSPORTATION_REQUEST_A_NEW_RIDE]: data.canRequestRide && isNotEmpty(user?.email) && isActive,
							[TRANSPORTATION_RIDE_HISTORY]: data.canViewRideHistory && isNotEmpty(user?.email)
						}

						const options = OPTIONS
							.filter(option => permissions[option.name])
							.map(o => ({
								text: o.title,
								value: o.name,
								hasSeparator: o.hasSeparator,
								onClick: () => onSelectOption(o.name)
							}))

						return (
							<>
								<div className="ClientDashboard-Body">
									<Breadcrumbs
										className="ClientDashboard-Breadcrumbs"
										items={[
											{
												title: 'Prospects',
												href: '/prospects',
												isEnabled: true
											},
											{
												title: data.fullName,
												href: `/prospects/${prospectId}`,
												isActive: true
											},
											{
												title: 'Dashboard',
												href: '#',
												isActive: true
											}
										]}
									/>
									<div className="ClientDashboard-Header">
										<div className="ClientDashboard-Title">
											Prospect Details
											<span className="ClientDashboard-ClientName">
												&nbsp;/&nbsp;{data.fullName}
											</span>
										</div>
										<div className="ClientDashboard-ControlPanel">
											{options.length > 0 && (
												<Dropdown
													items={options}
													toggleText="More Options"
													className="ClientDashboard-MoreOptionsDropdown"
												/>
											)}
											{/*{canAddEvent && isActive && (
												<Button
													color="success"
													className="ClientDashboard-AddEventBtn"
													onClick={() => setEventEditorOpen(true)}
												>
													Create Event
												</Button>
											)}*/}
										</div>
									</div>

									<div className="ClientDashboard-Section">
										<ProspectDetails
											prospectId={prospectId}
										/>
										<div className="flex-1"></div>
										{/*<ProspectDocumentsDevicesSummary
											prospectId={prospectId}
											onViewAllDocuments={() => {
												history.push(path(`prospects/${prospectId}/documents`))
											}}
										/>*/}
									</div>

									<ProspectEditor
										isOpen={isEditorOpen}
										prospectId={prospectId}
										onClose={onCloseEditor}
										onSaveSuccess={onSaveSuccess}
									/>

									{isInvalidDataWarningDialogOpen && (
										<WarningDialog
											isOpen
											title={`Please fill in the required fields to ${TRANSPORTATION_ACTION_DESC[transportationAction]}`}
											buttons={[
												{
													text: 'Cancel',
													outline: true,
													onClick: () => {
														setTransportationAction(null)
														setInvalidDataWarningDialogOpen(false)
													}
												},
												{
													text: 'Edit Record',
													onClick: () => {
														setEditorOpen(true)
														setInvalidDataWarningDialogOpen(false)
													}
												}
											]}
										/>
									)}

									{/*{isEventEditorOpen && (
										<EventEditor
											isOpen
											clientId={clientId}
											onClose={() => setEventEditorOpen(false)}
											onSaveSuccess={onEventSaveSuccess}
										/>
									)}
									*/}
									{/*<AppointmentEditor
										prospectId={prospectId}
										isOpen={isAppointmentEditorOpen}
										onClose={() => setAppointmentEditorOpen(false)}
										onSaveSuccess={onAppointmentSaveSuccess}
									/>*/}

									{isAppointmentSaveSuccessDialogOpen && (
										<SuccessDialog
											isOpen
											title="The appointment has been created"
											buttons={[
												{
													text: 'Close',
													onClick: () => setAppointmentSaveSuccessDialogOpen(false)
												}
											]}
										/>
									)}

									{isEventSaveSuccessDialogOpen && (
										<SuccessDialog
											isOpen
											title="The event has been submitted"
											buttons={[
												{
													text: 'Close',
													outline: true,
													onClick: () => setEventSaveSuccessDialogOpen(false)
												},
												{
													text: 'View details',
													onClick: () => {
														history.push(path(`prospects/${prospectId}/events`), { selected: saved })
													}
												}
											]}
										/>
									)}

									<ProspectActivationEditor
										isOpen={isActivationEditorOpen}
										prospectId={prospectId}
										onClose={onCloseEditor}
										onSaveSuccess={onSaveSuccess}
									/>

									<ProspectDeactivationEditor
										isOpen={isDeactivationEditorOpen}
										prospectId={prospectId}
										onClose={onCloseEditor}
										onSaveSuccess={onSaveSuccess}
									/>
								</div>
							</>
						)
					}}
				</DataLoadable>
				<form
					method="POST"
					target="_blank"
					className="d-none"
					ref={transportationFormRef}
				>
					<input name="action"/>
					<input name="payload"/>
				</form>
			</div>
		</DocumentTitle>
	)
}

export default ProspectDashboard