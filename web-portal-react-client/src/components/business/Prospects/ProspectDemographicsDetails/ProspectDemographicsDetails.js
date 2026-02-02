import React from 'react'

import cn from 'classnames'

import {
	Detail as BaseDetail
} from 'components/business/common'

import {
	DateUtils as DU
} from 'lib/utils/Utils'

import {
	DemographicsDetails,
	AssociatedContactDetail
} from '../../common'

import './ProspectDemographicsDetails.scss'

const { format, formats } = DU

function Detail({ children, ...props }) {
	return (
		<BaseDetail
			{...props}
			className={cn('ProspectDemographicsDetail', props.className)}
			titleClassName={cn('ProspectDemographicsDetail-Title', props.titleClassName)}
			valueClassName={cn('ProspectDemographicsDetail-Value', props.valueClassName)}
		>
			{children}
		</BaseDetail>
	)
}

export default function ProspectDemographicsDetails({ data = {}, onViewContact, onCreateContact }) {
	return (
		<>
			<DemographicsDetails data={data}/>

			<Detail title="Move-in Date">
				{data.moveInDate && format(data.moveInDate, formats.americanMediumDate)}
			</Detail>

			<Detail title="Rental Agreement Signed Date">
				{data.rentalAgreementSignedDate && format(
					data.rentalAgreementSignedDate, formats.americanMediumDate
				)}
			</Detail>

			<Detail title="Deactivation date">
				{data.deactivationDate}
			</Detail>

			<div className="ProspectDemographicsDetail-Section">
				<div className="ProspectDemographicsDetail-SectionTitle">
					Related party:
				</div>

				<Detail title="Full name">
					{data?.relatedParty?.fullName}
				</Detail>
				<Detail title="Relationship">
					{data?.relatedParty?.relationshipTypeTitle}
				</Detail>
				<Detail title="Cell Phone">
					{data?.relatedParty?.cellPhone}
				</Detail>
			</div>

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