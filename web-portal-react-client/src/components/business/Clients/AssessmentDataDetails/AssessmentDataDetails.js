import React, { memo } from 'react'

import {
	Detail as BaseDetail
} from 'components/business/common'

import './AssessmentDataDetails.scss'

function Detail({ children, ...props }) {
	return (
		<BaseDetail
			{...props}
			layout='v'
			className="AssessmentDataDetail"
			titleClassName="AssessmentDataDetail-Title"
			valueClassName="AssessmentDataDetail-Value"
		>
			{children}
		</BaseDetail>
	)
}

function AssessmentDataDetails({ data = {} }) {
	return data ? (
		<>
			<Detail title="Did member accept ECM Services?">
				{data.wasEcmServicesAcceptedByMember}
			</Detail>

			<Detail title="Date accepted">
				{data.dateOfEcmServicesAcceptedByMember}
			</Detail>

			<Detail title="Did member consent to data sharing between MCP and LSS?">
				{data.wasDataSharingMcpLssConsentedByMember}
			</Detail>

			<Detail title="Currently housed?">
				{data.isCurrentlyHoused}
			</Detail>
		</>
	) : (
		<div className="text-center">
			Data is not configured in the Nor Cal Comprehensive assessment
		</div>
	)
}

export default memo(AssessmentDataDetails)