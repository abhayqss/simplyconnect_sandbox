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

import './AssociatedContactDetail.scss'

const { format, formats } = DU

const LONG_DATE_FORMAT = formats.longDateMediumTime12

const formatDate = date => format(date, LONG_DATE_FORMAT)

function Detail({ children, ...props }) {
	return (
		<BaseDetail
			{...props}
			className={cn('AssociatedContactDetail', props.className)}
			titleClassName={cn('AssociatedContactDetail-Title', props.titleClassName)}
			valueClassName={cn('AssociatedContactDetail-Value', props.valueClassName)}
		>
			{children}
		</BaseDetail>
	)
}

export default function AssociatedContactDetail({ data = {}, onViewContact, onCreateContact }) {
	const contact = data?.associatedContact

	return (
		<>
			{contact && isInteger(contact.id) && (
				<Detail title="ACCOUNT IN SIMPLY CONNECT">
					{contact.canView ? (
						<div
							className="link"
							onClick={onViewContact}
						>
							{contact?.fullName}
						</div>
					) : contact.fullName}
				</Detail>
			)}
			{contact && !isInteger(contact.id) && (
				<Detail title="ACCOUNT IN SIMPLY CONNECT">
					{contact.canCreate && data?.isActive && (
						<div
							className="link"
							onClick={onCreateContact}
						>
							Create Account
						</div>
					)}
				</Detail>
			)}
		</>
	)
}