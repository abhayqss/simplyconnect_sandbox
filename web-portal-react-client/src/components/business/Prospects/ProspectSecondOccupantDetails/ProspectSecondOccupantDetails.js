import React from 'react'

import cn from 'classnames'

import {
	Detail as BaseDetail
} from 'components/business/common'

import {
	DateUtils as DU
} from 'lib/utils/Utils'

import {
	DemographicsDetails
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

export default function ProspectSecondOccupantDetails({ data = {} }) {
	return (
		<DemographicsDetails data={data}/>
	)
}