import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import {
	all,
	map,
	some,
	chain,
	where,
	filter,
	reject,
	compact,
	findWhere
} from 'underscore'

import {
	Col,
	Row,
	Form,
	Button
} from 'reactstrap'

import moment from 'moment'

import {
	Loader,
	AlertPanel,
	ErrorViewer
} from 'components'

import {
	WarningDialog
} from 'components/dialogs'

import {
	TextField,
	DateField,
	PhoneField,
	SelectField,
	SwitchField
} from 'components/Form'

import {
	useForm,
	useToggle,
	useScrollable,
	useQueryInvalidation,
	useScrollToFormError,
	useCustomFormFieldChange
} from 'hooks/common'

import {
	useAuthUser
} from 'hooks/common/redux'

import {
	useCommunitiesQuery,
	useOrganizationsQuery,
	useAppointmentTypesQuery,
	useAppointmentStatusesQuery,
	useClientsWithBirthdaysQuery,
	useAppointmentClientRemindersQuery,
	useAppointmentServiceCategoriesQuery,
	useAppointmentNotificationMethodsQuery
} from 'hooks/business/directory/query'

import {
	useAppointmentQuery,
	useAppointmentSubmit,
	useAppointmentContacts,
	useAvailableTimeSlotValidation
} from 'hooks/business/appointments'

import {
	useClientQuery
} from 'hooks/business/client/queries'

import AppointmentEntity from 'entities/Appointment'
import AppointmentValidator from 'validators/AppointmentFormValidator'

import {
	SYSTEM_ROLES,
	APPOINTMENT_STATUSES
} from 'lib/Constants'

import {
	isEmpty,
	isInteger,
	isNotEmpty
} from 'lib/utils/Utils'

import {
	isPast,
	setTime,
	isToday,
	parseTime,
	isPastDate,
	getStartOfDayTime
} from 'lib/utils/DateUtils'

import {
	getFormattedTimeRange
} from '../lib/utils/RepeatTimeUtils'

import './AppointmentForm.scss'

import { ReactComponent as Info } from 'images/info-2.svg'

const {
	NURSE,
	PHARMACIST,
	CASE_MANAGER,
	PARENT_GUARDIAN,
	SERVICE_PROVIDER,
	CARE_COORDINATOR,
	COMMUNITY_MEMBERS,
	PRIMARY_PHYSICIAN,
	EXTERNAL_PROVIDER,
	BEHAVIORAL_HEALTH,
	SUPER_ADMINISTRATOR,
	COMMUNITY_ADMINISTRATOR,
	PERSON_RECEIVING_SERVICES
} = SYSTEM_ROLES

const {
	PLANNED,
	COMPLETED,
	CANCELLED,
	ENTERED_IN_ERROR
} = APPOINTMENT_STATUSES

function formatTime(date) {
	return moment(date).format('LT')
}

export function convertDateToTime(date, time) {
	return setTime(date, parseTime(time)).valueOf()
}

function valueTextMapper({ id, name, value, label, title }) {
	return { value: id || value || name, text: label || title || name }
}

function filterCommunities(data) {
    return where(data, { canViewOrHasAccessibleClient: true })
}

const scrollableStyles = { flex: 1 }

function AppointmentForm(
	{
		clientId,
		appointmentId,
		appointmentDate,
		isDuplicating,
		organizationId: defaultOrganizationId,

		onClose,
		onSubmitSuccess
	}
) {
	const [error, setError] = useState(null)
	const [isFetching, setFetching] = useState(false)
	const [needValidation, setNeedValidation] = useState(false)
	const [isBusySlotWarningDialogOpen, toggleBusySlotWarningDialog] = useToggle(false)

	const isClient = isInteger(clientId)
	const isEditing = isInteger(appointmentId) && !isDuplicating

	const user = useAuthUser()
	const invalidateQuery = useQueryInvalidation()

	const {
		fields,
		errors,
		isValid,
		isChanged,

		validate,
		clearField,
		changeField,
		changeFields
	} = useForm('Appointment', AppointmentEntity, AppointmentValidator)

	const data = useMemo(() => fields.toJS(), [fields])

	const {
		changeSelectField
	} = useCustomFormFieldChange(changeField)

	const { Scrollable, scroll } = useScrollable()

	const onScroll = useScrollToFormError('.AppointmentForm', scroll)

	const {
		data: appointment,
		isFetching: isFetchingAppointment
	} = useAppointmentQuery({ id: appointmentId }, {
		enabled: (isEditing || isDuplicating) && isInteger(appointmentId),
		staleTime: 0
	})

	const isDateChanged = (
		fields.date === getStartOfDayTime(appointment?.dateFrom)
		&& fields.from === formatTime(appointment?.dateFrom)
		&& fields.to === formatTime(appointment?.dateTo)
	)

	const isInvalidDateTime = (
		appointment && isDuplicating
		&& (isDateChanged || isPastDate(fields.date))
	)

	const {
		data: statuses = []
	} = useAppointmentStatusesQuery({}, { staleTime: 0 })

	const mappedStatuses = useMemo(
		() => chain(statuses)
			//cancelled status is unable to be selected (-> cancel appointment button)
			.reject(o => o.name === CANCELLED)
			//completed, entered in error statuses are available only for editing
			.reject(o => !isEditing && [COMPLETED, ENTERED_IN_ERROR].includes(o.name))
			//completed status is not shown for some roles
			.reject(o => isEditing && !appointment?.canComplete && o.name === COMPLETED)
			.map(valueTextMapper)
			//completed status is disabled if update date/time is earlier than the appointment start date/time
			.map(o => {
				if (o.value !== COMPLETED) return o

				return {
					...o,
					...!isPast(convertDateToTime(fields.date, fields.from)) && {
						isDisabled: true,
						tooltip: 'The appointment can\'t be completed before the appointment occurs'
					}
				}
			})
			.value(),
		[
			fields,
			statuses,
			isEditing,
			appointment
		]
	)

	const {
		data: organizations = [],
		isFetching: isFetchingOrganizations
	} = useOrganizationsQuery({
		areAppointmentsEnabled: true,
		checkCommunitiesExist: true
	}, {
		staleTime: 0
	})

	const mappedOrganizations = useMemo(
		() => map(organizations, valueTextMapper), [organizations]
	)

	const {
		data: communities = []
	} = useCommunitiesQuery(
		{ organizationId: fields.organizationId },
		{
			enabled: isInteger(fields.organizationId),
			staleTime: 0
		}
	)

	const filteredCommunities = useMemo(
		() => filterCommunities(communities), [communities]
	)

	const mappedCommunities = useMemo(
		() => map(filteredCommunities, valueTextMapper), [filteredCommunities]
	)

	const {
		data: types = []
	} = useAppointmentTypesQuery({}, {
		staleTime: 0
	})

	const mappedTypes = useMemo(
		() => map(types, valueTextMapper), [types]
	)

	const {
		data: serviceCategories = []
	} = useAppointmentServiceCategoriesQuery(
		{}, { staleTime: 0 }
	)

	const mappedServiceCategories = useMemo(
		() => map(serviceCategories, valueTextMapper), [serviceCategories]
	)

	const {
		data: clientReminders = []
	} = useAppointmentClientRemindersQuery({}, {
		staleTime: 0
	})

	const isNeverReminderSelected = useMemo(
		() => fields.reminders.includes('NEVER'),
		[fields]
	)

	const mappedClientReminders = useMemo(
		() => map(clientReminders, valueTextMapper), [clientReminders]
	)

	const {
		data: serviceProviders = [],
		isFetching: isFetchingServiceProviders
	} = useAppointmentContacts(
		{
			statuses: ['ACTIVE'],
			organizationId: user?.roleName !== SUPER_ADMINISTRATOR ? user?.organizationId : fields.organizationId,
			roles: [
				NURSE,
				PHARMACIST,
				CASE_MANAGER,
				PARENT_GUARDIAN,
				SERVICE_PROVIDER,
				CARE_COORDINATOR,
				COMMUNITY_MEMBERS,
				PRIMARY_PHYSICIAN,
				BEHAVIORAL_HEALTH,
				COMMUNITY_ADMINISTRATOR,
				PERSON_RECEIVING_SERVICES
			],
			accessibleClientId: fields.clientId
		},
		{
			staleTime: 0,
			enabled: isInteger(fields.organizationId) && isInteger(fields.clientId)
		})

	const mappedServiceProviders = useMemo(() => map(
		[{ id: 'EXTERNAL_PROVIDER', name: 'External Provider' }, ...serviceProviders],
		({ id, name, role }) => ({ value: id, text: `${name}${role ? ` - ${role}` : ''}` })
	), [serviceProviders])

	const {
		data: clients = []
	} = useClientsWithBirthdaysQuery(
		{
			recordStatuses: ['ACTIVE'],
			organizationIds: [fields.organizationId],
			communityIds: [fields.communityId]
		},
		{
			staleTime: 0,
			enabled: (
				isInteger(fields.communityId)
				&& isInteger(fields.organizationId)
			)
		}
	)

	const mappedClients = useMemo(() => map(
		clients, ({ id, fullName, birthDate }) => (
			{ text: `${fullName}${birthDate ? ` - ${birthDate}` : ''}`, value: id })
	), [clients])

	const {
		data: notificationMethods
	} = useAppointmentNotificationMethodsQuery(
		{}, { staleTime: 0 }
	)

	const mappedNotificationMethods = useMemo(
		() => map(notificationMethods, valueTextMapper), [notificationMethods]
	)

	const {
		data: client
	} = useClientQuery({
		clientId: clientId ?? fields.clientId
	}, {
		enabled: isInteger(clientId ?? fields.clientId),
		staleTime: 0
	})

	const [
		validateAvailableTimeSlot,
		timeSlotErrors,
		setTimeSlotErrors,
	] = useAvailableTimeSlotValidation({
		appointmentId,
		clientId: fields.clientId,
		dateTo: convertDateToTime(fields.date, fields.to),
		dateFrom: convertDateToTime(fields.date, fields.from),
		serviceProviderIds: data.serviceProviderIds ?? [],
		isExternalProviderServiceProvider: fields.isExternalProviderServiceProvider
	})

	const { mutateAsync: submit } = useAppointmentSubmit({
		onError: setError,
		onSuccess: ({ data }) => {
			onSubmitSuccess(data)

			if (isEditing) invalidateQuery(
				'Appointment', { id: appointmentId }
			)
		}
	})

	const isClientFromAffiliatedOrganization = useMemo(() => {
		// ToDO: check if client belongs to an Affiliated organization
		return false
	}, [fields.clientId, fields.organizationId])

	const isEmailNotificationSelected = useMemo(
		() => fields.notificationMethods.includes('EMAIL'), [fields]
	)

	const isPhoneNotificationSelected = useMemo(
		() => fields.notificationMethods.some(o => ['PHONE', 'SMS'].includes(o)), [fields]
	)

	const isExternalProviderSelected = useMemo(() => {
			const externalServiceProvider = findWhere(serviceProviders, { role: EXTERNAL_PROVIDER })
			return fields.serviceProviderIds?.includes(externalServiceProvider?.id)
		}, [fields, serviceProviders]
	)

	const validationOptions = useMemo(() => ({
		included: {
			isEditing,
			isDateFromInPast: isPast(convertDateToTime(fields.date, fields.from)),
			isNeverReminderSelected,
			isExternalProviderSelected,
			isEmailNotificationSelected,
			isPhoneNotificationSelected
		}
	}), [
		fields,
		isEditing,
		isNeverReminderSelected,
		isExternalProviderSelected,
		isEmailNotificationSelected,
		isPhoneNotificationSelected
	])

	function cancel() {
		onClose(isChanged)
	}

	function tryToSubmit() {
		setFetching(true)

		validate(validationOptions)
			.then(async (...args) => {
				if (data.status === COMPLETED) return 
				
				return await validateAvailableTimeSlot(...args)
			})
			.then(async () => {
				const isNew = !appointmentId || isDuplicating

				await submit({
					...data,
					serviceProviderIds: filter(data.serviceProviderIds, id => some(mappedServiceProviders, { value: id })),
					...!isNew && { id: appointmentId },
					dateTo: convertDateToTime(data.date, data.to),
					dateFrom: convertDateToTime(data.date, data.from)
				})

				setNeedValidation(false)
			})
			.catch(() => {
				onScroll()
				setNeedValidation(true)
			})
			.finally(() => {
				setFetching(false)
			})
	}

	function validateIf() {
		if (needValidation) {
			validate(validationOptions)
				.then(() => setNeedValidation(false))
				.catch(() => setNeedValidation(true))
		}
	}

	function init() {
		if (appointment) {
			changeFields({
				...appointment,
				status: isDuplicating && (appointment.status === COMPLETED || appointment.status === ENTERED_IN_ERROR)
					? PLANNED
					: appointment.status,
				creator: user?.fullName,
				date: getStartOfDayTime(appointment.dateFrom),
				from: formatTime(appointment.dateFrom),
				to: formatTime(appointment.dateTo)
			}, true)
		}
	}

	function setDefaultData() {
		if (user && !isEditing && !isDuplicating) {
			const changes = {
				status: 'PLANNED',
				date: appointmentDate,
				creator: user?.fullName,
				reminders: ['NEVER'],
				...getFormattedTimeRange(appointmentDate, {
					offset: appointmentDate ? 0 : 30
				})
			}

			if (!isClient) {
				changes.organizationId = (
					defaultOrganizationId ?? user.organizationId
				)

				changes.communityId = (
					changes.organizationId === user.organizationId ? user.communityId : null
				)
			}

			changeFields(changes, true)
		}
	}

	function setDefaultIsPublic() {
		if (
			!isEditing
			&& !isDuplicating
			&& isClientFromAffiliatedOrganization
		) {
			changeField('isPublic', true)
		}
	}

	function setDefaultOrganization() {
		const organizationId = (
			defaultOrganizationId ?? user?.organizationId
		)

		if (
			!isClient
			&& !isEditing
			&& !isDuplicating
			&& isNotEmpty(organizations)
			&& all(organizations, o => o.id !== organizationId)
		) {
			changeField('organizationId', organizations[0].id, true)
		}
	}

	function setDefaultCommunity() {
		if (!isClient && !isEditing && !isDuplicating && isNotEmpty(filteredCommunities)) {
			if (all(filteredCommunities, o => o.id !== user?.communityId)) {
				changeField('communityId', null, true)
			}

			if (filteredCommunities.length === 1) {
				changeField('communityId', filteredCommunities[0].id, true)
			}
		}
	}

	function setDefaultClient() {
		if (!isEditing && !isDuplicating) {
			if (isInteger(clientId)) {
				changeField('clientId', clientId, true)
			} else if (clients.length === 1) {
				changeField('clientId', clients[0].id, true)
			}
		}
	}

	function setDefaultDataFromClient() {
		if (!isEditing && !isDuplicating && client) {
			const {
				email,
				cellPhone,
				communityId,
				organizationId,
				primaryContact: contact
			} = client

			if (isClient) {
				changeField('communityId', communityId, true)
				changeField('organizationId', organizationId, true)
			}

			if (contact?.typeName === 'SELF') {
				changeField('email', email, isClient)
				changeField('phone', cellPhone, isClient)
				changeField('notificationMethods', ['EMAIL'], isClient)
				changeField('reminders', ['HOUR_2_BEFORE', 'DAY_1_BEFORE'], isClient)

				if (contact?.notificationMethodName === 'PHONE') {
					changeField('notificationMethods', ['SMS'], isClient)
				}
			}
		}
	}

	const onSubmit = useCallback(tryToSubmit, [
		data,
		submit,
		onScroll,
		validate,
		isDuplicating,
		appointmentId,
		validationOptions,
		mappedServiceProviders,
		validateAvailableTimeSlot
	])

	const onCancel = useCallback(cancel, [onClose, isChanged])

	const onChangeDateField = useCallback((name, date) => {
		const value = date ? date.getTime() : null

		if (value && isToday(value)) {
			const defaultRange = getFormattedTimeRange(Date.now(), { offset: 30 })

			const from = setTime(value, parseTime(fields.from))
			if (from.isBefore(Date.now())) changeField('from', defaultRange.from)

			const to = setTime(value, parseTime(fields.to))
			if (to.isBefore(moment().add(30, 'minutes'))) changeField('to', defaultRange.to)
		}

		changeField(name, value)
	}, [fields, changeField])

	const onChangeFromDate = useCallback((name, value) => {
		value = formatTime(value)
		changeField(name, value)

		if (parseTime(value).isSameOrAfter(parseTime(fields.to))) {
			const to = formatTime(parseTime(value).add(30, 'minutes'))
			changeField('to', to)
		}
	}, [fields, changeField])

	const onChangeToDate = useCallback((name, value) => {
		value = formatTime(value)
		changeField(name, value)

		if (parseTime(fields.from).isSameOrAfter(parseTime(value))) {
			const from = formatTime(parseTime(value).subtract(30, 'minutes'))
			changeField('from', from)
		}
	}, [fields, changeField])

	const onChangeServiceProvider = useCallback((name, value) => {
		changeField(name, reject(value, o => o === 'EXTERNAL_PROVIDER'))
		changeField('isExternalProviderServiceProvider', value.includes('EXTERNAL_PROVIDER'))
	}, [changeField])

	const onChangeReminder = useCallback((name, value) => {
		if (value.includes('NEVER') && !isNeverReminderSelected) {
			changeField(name, ['NEVER'])
			clearField('email')
			clearField('phone')
			clearField('notificationMethods')
		} else if (isNeverReminderSelected) {
			changeField(name, reject(value, o => o === 'NEVER'))
			changeField('notificationMethods', ['EMAIL'])

			if (client) {
				const {
					email,
					cellPhone,
					primaryContact: contact
				} = client

				changeField('email', email)
				changeField('phone', cellPhone)

				if (contact?.typeName === 'SELF'
					&& contact?.notificationMethodName === 'PHONE') {
					changeField('notificationMethods', ['SMS'])
				}
			}
		} else changeField(name, value)
	}, [
		client,
		clearField,
		changeField,
		isNeverReminderSelected
	])

	useEffect(validateIf, [
		validate,
		onScroll,
		needValidation,
		validationOptions
	])

	useEffect(init, [
		user,
		appointment,
		changeFields,
		isDuplicating
	])

	useEffect(setDefaultData, [
		user,
		isClient,
		isEditing,
		changeFields,
		isDuplicating,
		appointmentDate,
		defaultOrganizationId
	])

	useEffect(setDefaultIsPublic, [
		isEditing,
		changeField,
		isDuplicating,
		isClientFromAffiliatedOrganization
	])

	useEffect(setDefaultOrganization, [
		isClient,
		isEditing,
		changeField,
		organizations,
		isDuplicating,
		user?.organizationId,
		defaultOrganizationId
	])

	useEffect(setDefaultCommunity, [
		isClient,
		isEditing,
		changeField,
		isDuplicating,
		user?.communityId,
		filteredCommunities
	])

	useEffect(setDefaultClient, [
		clients,
		clientId,
		isEditing,
		changeField,
		isDuplicating
	])

	useEffect(setDefaultDataFromClient, [
		client,
		isClient,
		isEditing,
		changeField,
		isDuplicating
	])

	useEffect(() => {
		if (!isEmpty(timeSlotErrors)) {
			toggleBusySlotWarningDialog(true)
		}
	}, [timeSlotErrors, toggleBusySlotWarningDialog])

	return (
		<>
			<Form
				className="AppointmentForm"
				data-testid="appointmentForm"
			>
				{(isFetching || isFetchingAppointment) && (
					<Loader style={{ position: 'fixed' }} hasBackdrop/>
				)}

				<Scrollable style={scrollableStyles}>
					<div className="AppointmentForm-Section">
						<div className="AppointmentForm-SectionTitle">
							Appointment
						</div>
						<Row className="justify-content-md-end Public-Calendar">
							<Col className="d-flex align-self-end">
								<SwitchField
									name="isPublic"
									isChecked={fields.isPublic}
									label="Public Calendar"
									isDisabled={isClientFromAffiliatedOrganization}
									onChange={changeField}
									className="AppointmentForm-SwitchField"
								/>
							</Col>
						</Row>
						<Row>
							<Col md="6">
								<TextField
									type="text"
									name="title"
									value={fields.title}
									maxLength={256}
									errorText={errors.title}
									label="Appointment Title*"
									className="AppointmentForm-TextField"
									onChange={changeField}
								/>
							</Col>

							<Col md="6">
								<SelectField
									name="status"
									value={fields.status}
									options={mappedStatuses}
									label="Appointment Status*"
									className="AppointmentForm-SelectField"
									errorText={errors.status}
									onChange={changeSelectField}
								/>
							</Col>
						</Row>

						<Row>
							<Col md="6">
								<SelectField
									name="organizationId"
									value={fields.organizationId}
									options={mappedOrganizations}
									hasValueTooltip
									hasAllOption={false}
									hasNoneOption={false}
									hasKeyboardSearch
									hasKeyboardSearchText
									label="Organization Name*"
									isDisabled={
										mappedOrganizations.length === 1
										|| isClient
										|| isEditing
										|| isFetchingOrganizations
									}
									className="AppointmentForm-SelectField"
									errorText={errors.organizationId}
									onChange={changeSelectField}
								/>
							</Col>

							<Col md="6">
								<SelectField
									name="communityId"
									value={fields.communityId}
									options={mappedCommunities}
									hasKeyboardSearch
									label="Community Name*"
									isDisabled={
										isClient
										|| isEditing
										|| mappedCommunities.length === 1
										|| fields.organizationId === null
									}
									className="AppointmentForm-SelectField"
									errorText={errors.communityId}
									onChange={changeSelectField}
								/>
							</Col>
						</Row>
						<Row>
							<Col md="6">
								<TextField
									type="text"
									name="location"
									value={fields.location}
									maxLength={256}
									label="Location*"
									errorText={errors.location}
									className="AppointmentForm-TextField"
									onChange={changeField}
								/>
							</Col>

							<Col md="6">
								<SelectField
									name="type"
									value={fields.type}
									options={mappedTypes}
									label="Appointment Type*"
									className="AppointmentForm-SelectField"
									errorText={errors.type}
									onChange={changeSelectField}
								/>
							</Col>
						</Row>

						<Row>
							<Col md="6">
								<SelectField
									name="serviceCategory"
									value={fields.serviceCategory}
									options={mappedServiceCategories}
									label="Service Category"
									className="AppointmentForm-SelectField"
									errorText={errors.serviceCategory}
									onChange={changeSelectField}
								/>
							</Col>

							<Col md="6">
								<TextField
									type="text"
									name="referralSource"
									value={fields.referralSource}
									maxLength={256}
									label="Referral Source"
									className="AppointmentForm-TextField"
									errorText={errors.referralSource}
									onChange={changeField}
								/>
							</Col>
						</Row>
						<Row>
							<Col md="6">
								<TextField
									type="textarea"
									name="reasonForVisit"
									value={fields.reasonForVisit}
									maxLength={5000}
									label="Reason for Visit"
									className="AppointmentForm-TextArea"
									errorText={errors.reasonForVisit}
									onChange={changeField}
								/>
							</Col>
							<Col md="6">
								<TextField
									type="textarea"
									name="directionsInstructions"
									value={fields.directionsInstructions}
									maxLength={5000}
									label="Appointment Direction & Instructions"
									className="AppointmentForm-TextArea"
									errorText={errors.directionsInstructions}
									onChange={changeField}
								/>
							</Col>
						</Row>
						<Row>
							<Col md="6">
								<TextField
									type="textarea"
									name="notes"
									value={fields.notes}
									maxLength={5000}
									label={"Notes" + (isExternalProviderSelected ? "*" : "")}
									className="AppointmentForm-TextArea"
									errorText={errors.notes}
									onChange={changeField}
								/>
							</Col>
						</Row>
					</div>

					<div className="AppointmentForm-Section">
						<div className="AppointmentForm-SectionTitle">
							Schedule &amp; Resources
						</div>

						<Row>
							<Col md="6">
								<SelectField
									name="clientId"
									value={fields.clientId}
									options={mappedClients}
									label="Client*"
									isDisabled={
										fields.communityId === null
										|| isClient
										|| isEditing
									}
									hasKeyboardSearch
									hasKeyboardSearchText
									hasSearchBox
									hasValueTooltip
									className="AppointmentForm-SelectField"
									errorText={errors.clientId}
									onChange={changeSelectField}
								/>
							</Col>

							<Col md="6">
								<TextField
									type="text"
									name="creator"
									value={fields.creator}
									maxLength={256}
									isDisabled
									label="Creator*"
									className="AppointmentForm-TextField"
									onChange={changeField}
								/>
							</Col>
						</Row>

						<Row>
							<Col md="6">
								<DateField
									name="date"
									value={fields.date}
									isPastDisabled
									dateFormat="MM/dd/yyyy"
									label="Appointment date*"
									placeholder="Select date"
									onChange={onChangeDateField}
									errorText={errors.date}
								/>
							</Col>
							<Col md="6">
								<Row className="d-flex justify-content-between">
									<Col md="6">
										<DateField
											name="from"
											value={convertDateToTime(fields.date ?? Date.now(), fields.from)}
											label="From*"
											hasTimeSelect
											hasTimeSelectOnly
											dateFormat="h:mm aa"
											timeFormat="h:mm aa"
											timeIntervals={30}
											minDate={isToday(fields.date) ? getStartOfDayTime(Date.now()) : undefined}
											minTime={isToday(fields.date) ? Date.now() : undefined}
											timeSelectViewMode="dropdown"
											className="AppointmentForm-SelectField"
											errorText={errors.from}
											onChange={onChangeFromDate}
										/>
									</Col>

									<Col md="6">
										<DateField
											name="to"
											value={convertDateToTime(fields.date ?? Date.now(), fields.to)}
											label="To*"
											hasTimeSelect
											hasTimeSelectOnly
											dateFormat="h:mm aa"
											timeFormat="h:mm aa"
											timeIntervals={30}
											minDate={isToday(fields.date) ? getStartOfDayTime(Date.now()) : undefined}
											minTime={isToday(fields.date) ? Date.now() + 1000 * 60 * 35 : undefined}
											timeSelectViewMode="dropdown"
											className="AppointmentForm-SelectField"
											errorText={errors.to}
											onChange={onChangeToDate}
										/>
									</Col>
								</Row>
							</Col>
						</Row>
						<Row>
							<Col md="6">
								<SelectField
									isMultiple
									hasSearchBox
									hasValueTooltip
									hasKeyboardSearch
									hasAllOption={false}
									hasNoneOption={false}
									hasKeyboardSearchText
									label="Service Provider"
									name="serviceProviderIds"
									options={mappedServiceProviders}
									value={compact([
										fields.isExternalProviderServiceProvider && 'EXTERNAL_PROVIDER',
										...(data.serviceProviderIds || [])
									])}
									errorText={errors.serviceProviderIds}
									className="AppointmentForm-SelectField"
									isDisabled={fields.organizationId === null}
									isFetchingOptions={isFetchingServiceProviders}
									onChange={onChangeServiceProvider}
								/>
							</Col>
						</Row>
					</div>

					<div className="AppointmentForm-Section">
						<div className="AppointmentForm-SectionTitle">
							Reminders
						</div>

						<AlertPanel className="AppointmentForm-AlertPanel">
							Client / Community Care Team Members can set up notifications about new, 
							updated or cancelled appointments through Care Team Settings. If you 
							don’t want to receive notifications, select Not viewable responsibility. 
							Client notifications can be set up in the current section.
						</AlertPanel>

						<Row>
							<Col md="6">
								<SelectField
									isMultiple
									hasAllOption={false}
									name="reminders"
									value={data.reminders}
									options={mappedClientReminders}
									label="Client Reminder"
									isDisabled={!isInteger(fields.clientId)}
									className="AppointmentForm-SelectField"
									onChange={onChangeReminder}
								/>
							</Col>
							<Col md="6">
								<SelectField
									isMultiple
									hasEmptyValue
									hasAllOption={false}
									name="notificationMethods"
									value={data.notificationMethods}
									options={mappedNotificationMethods}
									isDisabled={isNeverReminderSelected || !isInteger(fields.clientId)}
									label={"Notification Method" + (!isNeverReminderSelected ? "*" : "")}
									className="AppointmentForm-SelectField"
									errorText={errors.notificationMethods}
									onChange={changeSelectField}
								/>
							</Col>
						</Row>
						<Row>
							<Col md="6">
								<TextField
									label={"Email" + (isEmailNotificationSelected ? "*" : "")}
									name="email"
									value={fields.email}
									isDisabled={isNeverReminderSelected || !isInteger(fields.clientId)}
									errorText={errors.email}
									onChange={changeField}
									className="AppointmentForm-TextField"
								/>
							</Col>

							<Col md="6">
								<PhoneField
									name="phone"
									value={fields.phone}
									label={"Cell Phone #" + (isPhoneNotificationSelected ? "*" : "")}
									autoFormat
									alwaysDefaultMask
									isDisabled={isNeverReminderSelected || !isInteger(fields.clientId)}
									defaultMask="... ... ...."
									placeholder="XXX XXX XXXX"
									className="AppointmentForm-TextField"
									errorText={errors.phone}
									onChange={changeField}
								/>
							</Col>
						</Row>
					</div>
				</Scrollable>

				<div className="AppointmentForm-Footer">
					<div className="AppointmentForm-FooterInfo">
						<div>
							<Info />
						</div>
						<span className="AppointmentForm-FooterInfoText">
							Client / Community care team members can set up appointments for members of 
							their care team. These appointments may not be visible in calendar default 
							settings. If you don’t see an appointment you’ve created, update Calendar 
							filters to All.
						</span>
					</div>
					<Button
						outline
						color="success"
						onClick={onCancel}
					>
						Close
					</Button>

					<Button
						color="success"
						onClick={onSubmit}
						disabled={isFetching || !isValid || isInvalidDateTime}
					>
						Submit
					</Button>
				</div>
			</Form>

			{isBusySlotWarningDialogOpen && (
				<WarningDialog
					isOpen
					buttons={[{
						text: 'Close',
						onClick: () => {
							setTimeSlotErrors(null)
							toggleBusySlotWarningDialog(false)
						}
					}]}
				>
					<div className="h-flexbox justify-content-center">
						<div className="v-flexbox">
							{map(timeSlotErrors.split('\n'), msg => (
								<div className="margin-bottom-5">{msg}</div>
							))}
						</div>
					</div>
				</WarningDialog>
			)}

			{error && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</>
	)
}

export default memo(AppointmentForm)
