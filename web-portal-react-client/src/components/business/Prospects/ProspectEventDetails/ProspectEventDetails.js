import React, {
	memo,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	map
} from 'underscore'

import { Link } from 'react-router-dom'

import {
	Detail as BaseDetail
} from 'components/business/common'

import {
	E_SIGN_STATUSES
} from 'lib/Constants'

import {
	hyphenate,
	formatSSN,
	getAddress,
	isNotEmpty
} from 'lib/utils/Utils'

import {
	format,
	formats
} from 'lib/utils/DateUtils'

import {
	noop
} from 'lib/utils/FuncUtils'

import {
	isNotEmptyOrBlank
} from 'lib/utils/ObjectUtils'

import { path } from 'lib/utils/ContextUtils'

import './ProspectEventDetails.scss'

const DATE_TIME_FORMAT = formats.longDateMediumTime12
const DATE_TIME_TIMEZONE_FORMAT = formats.longDateMediumTime12TimeZone

const {
	SIGNED
} = E_SIGN_STATUSES

const SECTIONS = {
	PROSPECT_INFO: { name: 'prospect', title: 'Prospect Info' },
	ESSENTIALS: { name: 'essentials', title: 'Event Essentials' },
	DESCRIPTION: { name: 'description', title: 'Event Description' },
	TREATMENT: { name: 'treatment', title: 'Treatment Details' },
	RESPONSIBLE_MANAGER: { name: 'responsibleManager', title: 'Responsible Manager' },
	REGISTERED_NURSE: { name: 'registeredNurse', title: 'Registered Nurse (RN)' },
	PATIENT_VISIT: { name: 'patientVisit', title: 'Patient Visit' },
	INSURANCE: { name: 'insurances', title: 'Insurance' },
	GUARANTOR: { name: 'guarantors', title: 'Guarantor' },
	PROCEDURES: { name: 'procedures', title: 'Procedures' },
	DIAGNOSIS: { name: 'diagnoses', title: 'Diagnoses' },
	ALLERGIES: { name: 'allergies', title: 'Allergies' }
}

function Detail(
	{
		title,
		children,
		titleClassName,
		valueClassName
	}
) {
	return isNotEmpty(children) && (
		<BaseDetail
			title={title}
			titleClassName={cn('ProspectEventDetail-Title', titleClassName)}
			valueClassName={cn('ProspectEventDetail-Value', valueClassName)}
			className="ProspectEventDetail"
		>
			{children}
		</BaseDetail>
	)
}

function SubDetail({ title, value }) {
	return isNotEmpty(value) && (
		<div className="ProspectEventSubDetail">
			<span className="ProspectEventSubDetail-Title">{title}</span>
			<span className="ProspectEventSubDetail-Value">{value}</span>
		</div>
	)
}

function ProspectEventDetails({ data, className, onViewDocument, onViewAppointment }) {
	const _onViewDocument = useCallback(() => {
		onViewDocument(data.documentSignature)
	}, [data, onViewDocument])

	const _onViewAppointment = useCallback(() => {
		onViewAppointment(data.appointmentId)
	}, [data, onViewAppointment])

	return isNotEmpty(data) && (
		<div className={cn("ProspectEventDetails", className)}>
			<div className="ProspectEventDetails-Navigation">
				{map(SECTIONS, section => {
					return isNotEmptyOrBlank(data[section.name], true) ? (
						<a
							key={section.name}
							className="ProspectEventDetails-NavLink"
							href={`#prospect-event-details__${hyphenate(section.name)}`}
						>
							{section.title}
						</a>
					) : null
				})}
			</div>
			{isNotEmpty(data.prospect) && (
				<div className="ProspectEventDetails-Section EventProspectInfo">
					<div
						id="prospect-event-details__prospect"
						className="ProspectEventDetails-SectionAnchor"
					/>

					<div className="d-flex justify-content-between margin-bottom-24 padding-right-24">
						<div className="ProspectEventDetails-SectionTitle">
							Prospect Info
						</div>
					</div>

					<Detail title="Prospect name">
						{
							data.canViewProspect ? (
								<Link
									className="ProspectEventDetails-Link"
									to={path(`/prospects/${data.prospect.id}`)}
								>
									{data.prospect.fullName}
								</Link>
							) : (
								data.prospect.fullName
							)}
					</Detail>
					<Detail title="Prospects Aliases">
						{data.prospect.aliases && data.prospect.aliases.join(', ')}
					</Detail>
					<Detail title="Prospects identifiers">
						{data.prospect.identifiers && data.prospect.identifiers.join(', ')}
					</Detail>
					<Detail title="Social security number">
						{data.prospect.ssn && formatSSN(data.prospect.ssn)}
					</Detail>
					<Detail title="Date of birth">
						{data.prospect.birthDate}
					</Detail>
					<Detail title="Gender">
						{data.prospect.gender}
					</Detail>
					<Detail title="Marital Status">
						{data.prospect.maritalStatus}
					</Detail>
					<Detail title="Primary language">
						{data.prospect.primaryLanguage}
					</Detail>
					<Detail title="Prospects Account Number">
						{data.prospect.prospectAccountNumber}
					</Detail>
					<Detail title="Race">
						{data.prospect.race}
					</Detail>
					<Detail title="Ethnic group">
						{data.prospect.ethnicGroup}
					</Detail>
					<Detail title="Nationality">
						{data.prospect.nationality}
					</Detail>
					<Detail title="Religion">
						{data.prospect.religion}
					</Detail>
					<Detail title="Citizenship">
						{data.prospect.citizenships && data.prospect.citizenships.join(', ')}
					</Detail>
					<Detail title="Veterans Military Status">
						{data.prospect.veteranStatus}
					</Detail>
					<Detail title="Phone number - Home">
						{data.prospect.homePhone && (
							<SubDetail
								title="Telephone number"
								value={data.prospect.homePhone}
							/>
						)}
					</Detail>
					<Detail title="Phone number - Business">
						{data.prospect.businessPhone && (
							<SubDetail
								title="Telephone number"
								value={data.prospect.businessPhone}
							/>
						)}
					</Detail>
					{data.prospect.address && (
						<Detail title="Address">
							{getAddress(data.prospect.address, ',')}
						</Detail>
					)}
					<Detail title="Organization">
						{data.prospect.organizationTitle}
					</Detail>
					<Detail title="Community">
						{data.prospect.communityTitle}
					</Detail>
					<Detail title="Death Date and Time">
						{format(data.prospect.deathDate, DATE_TIME_FORMAT)}
					</Detail>
				</div>
			)}
			{isNotEmpty(data.essentials) && (
				<div className="EventDetails-Section EventEssentials">
					<div
						id="event-details__essentials"
						className="EventDetails-SectionAnchor"
					/>
					<div className="d-flex justify-content-between margin-bottom-24">
						<div className="EventDetails-SectionTitle">
							Event Essentials
						</div>
					</div>
					<Detail title="Person submitting event">
						{data.essentials.author}
					</Detail>
					<Detail title="Care team role">
						{data.essentials.authorRole}
					</Detail>
					<Detail title="Event date and time">
						{format(data.essentials.date, DATE_TIME_FORMAT)}
					</Detail>
					<Detail title="Event type">
						<span
							style={{ backgroundColor: '#fff1ca' }}
							className="EventDetails-Type">
                            {data.essentials.typeTitle}
                        </span>
					</Detail>
					<Detail title="Emergency department visit">
						{data.essentials.isEmergencyDepartmentVisit ? 'Yes' : 'No'}
					</Detail>
					<Detail title="Overnight in-patient">
						{data.essentials.isOvernightInpatient ? 'Yes' : 'No'}
					</Detail>
					<Detail title="Client device ID">
						{data.essentials.deviceId}
					</Detail>
					<Detail title="Event type code">
						{data.essentials.typeCode}
					</Detail>
					<Detail title="Recorded date/time">
						{format(data.essentials.recordedDate, DATE_TIME_FORMAT)}
					</Detail>
				</div>
			)}
			{isNotEmptyOrBlank(data.description, false) && (
				<div className="EventDetails-Section EventDescription">
					<div
						id="event-details__description"
						className="EventDetails-SectionAnchor"
					/>
					<div className="d-flex justify-content-between margin-bottom-24">
						<div className="EventDetails-SectionTitle">
							Event Description
						</div>
					</div>
					<Detail title="Location">
						{data.description.location}
					</Detail>
					{isNotEmptyOrBlank(data.documentSignature) ? (
						<Detail title="Situation">
							<SubDetail
								title={`${data.documentSignature.statusName === SIGNED ? 'Signed' : 'Received'} document`}
								value={data.documentSignature.templateName}
							/>
							<SubDetail
								value={format(data.documentSignature.signedDate, DATE_TIME_TIMEZONE_FORMAT)}
								title={`Date ${data.documentSignature.statusName === SIGNED ? 'signed' : 'received'}`}
							/>
							<span
								className="EventDetails-Link"
								onClick={_onViewDocument}
							>
								View Document
							</span>
						</Detail>
					) : (
						<Detail title="Situation">
							{data.description.situation}
						</Detail>
					)}
					<Detail title="Background">
						{data.description.background}
					</Detail>
					<Detail>
						{data.canViewAppointment && Boolean(data.appointmentId) && (
							<span
								className="EventDetails-Link"
								onClick={_onViewAppointment}
							>
								View Appointment
							</span>
						)}
					</Detail>
					<Detail title="Assessment">
						{data.description.assessment}
					</Detail>
					<Detail title="Injury">
						{data.description.hasInjury ? 'Yes' : 'No'}
					</Detail>
					<Detail title="Follow Up Expected">
						{data.description.isFollowUpExpected ? 'Yes' : 'No'}
					</Detail>
					<Detail title="Follow Up Details">
						{data.description.followUpDetails}
					</Detail>
				</div>
			)}
		</div>
	)
}

ProspectEventDetails.propTypes = {
	data: PTypes.object,
	className: PTypes.string,
	onViewDocument: PTypes.func,
	onViewAppointment: PTypes.func
}

ProspectEventDetails.defaultProps = {
	onViewDocument: noop,
	onViewAppointment: noop
}

export default memo(ProspectEventDetails)