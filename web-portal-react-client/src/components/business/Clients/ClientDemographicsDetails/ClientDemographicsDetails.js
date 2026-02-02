import React from 'react'

import cn from 'classnames'

import { map } from 'underscore'

import { Link } from 'react-router-dom'

import {
	Detail as BaseDetail
} from 'components/business/common'

import {
	isInteger,
	getAddress,
	DateUtils as DU
} from 'lib/utils/Utils'

import { path } from 'lib/utils/ContextUtils'

import {
	DemographicsDetails,
	AssociatedContactDetail
} from '../../common'

import './ClientDemographicsDetails.scss'

const { format, formats } = DU

const LONG_DATE_FORMAT = formats.longDateMediumTime12

const formatDate = date => format(date, LONG_DATE_FORMAT)

function Detail({ children, ...props }) {
	return (
		<BaseDetail
			{...props}
			className={cn('ClientDemographicsDetail', props.className)}
			titleClassName={cn('ClientDemographicsDetail-Title', props.titleClassName)}
			valueClassName={cn('ClientDemographicsDetail-Value', props.valueClassName)}
		>
			{children}
		</BaseDetail>
	)
}

export default function ClientDemographicsDetails({ data = {}, onViewContact, onCreateContact }) {
	const ERdata = () =>{
		return `${data.emergencyContactName},${data.emergencyContactRelationship}
			 ${data.emergencyContactWorkPhone?','+data.emergencyContactWorkPhone :''}
		     ${data.emergencyContactCellPhone?','+data.emergencyContactCellPhone: ''}
		     ${data.emergencyContactEmail?','+data.emergencyContactEmail:''}`
	}
	return (
		<>
			<DemographicsDetails data={data}/>

			<Detail title="UNIT #">
				{data.unit}
			</Detail>

			<Detail title="RISK SCORE">
				{data.riskScore}
			</Detail>

			<Detail title="CLIENT HAS AN ADVANCED DIRECTIVE ON FILE">
				{data.hasAdvancedDirectiveOnFile ? 'Yes' : 'No'}
			</Detail>

			<Detail title="OPT IN / OPT OUT CONSENT">
				{data.hieConsentPolicyTitle}
			</Detail>

			<Detail title="ADMIT DATE AND TIME">
				{map(data.admitDates, formatDate).join(', ')}
			</Detail>

			<Detail title="DISCHARGE DATE AND TIME">
				{map(data.dischargeDates, formatDate).join(', ')}
			</Detail>

			<Detail title="DEACTIVATE DATE">
				{data.deactivatedDate && formatDate(data.deactivatedDate)}
			</Detail>

			<Detail title="DEATH DATE AND TIME">
				{data.deathDateTime && formatDate(data.deathDateTime)}
			</Detail>

			<Detail title="PHARMACY">
				{data.pharmacies ? (
					<div className="DemographicsDetail-Pharmacies">
						{
							data.pharmacies
								.filter(s => !!s.trim())
								.map(pharmacy => (
									<div className="DemographicsDetail-Pharmacy">{pharmacy}</div>
								))
						}
					</div>
				) : null}
			</Detail>
			{data.pharmacyPid && (
				<Detail title="PHARMACY PID">
					{data.pharmacyPid}
				</Detail>
			)}
			{
				data.emergencyContactName && (
					<Detail title="PRIMARY EMERGENCY CONTACT">
						{ERdata()}
					</Detail>
				)
			}

			<Detail
				title="PointClickCare MEDICAL RECORD NUMBER"
				titleClassName="ClientDemographicsDetail-PointClickCareIdTitle">
				{data.pointClickCareMedicalRecordNumber}
			</Detail>

			<AssociatedContactDetail
				data={data}
				onViewContact={onViewContact}
				onCreateContact={onCreateContact}
			/>

			{/*<Detail
                className="DemographicsDetail"
                titleClassName="DemographicsDetail-Title"
                valueClassName="DemographicsDetail-Value"
                title="CLIENT PARTICIPATES IN SHARING DATA">
                <SwitchField
                    name='isDataShareEnabled'
                    isChecked={isSharingDataParticipant}
                    isDisabled={true}
                    onChange={this.onChangeSharingDataParticipation}
                />
            </Detail>*/}
		</>
	)
}