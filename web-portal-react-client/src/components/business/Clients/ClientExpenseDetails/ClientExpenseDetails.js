import React from 'react'

import Currency from 'currency.js'

import {
	Detail as BaseDetail
} from 'components/business/common'

import { isNotEmpty } from 'lib/utils/Utils'

import { DateUtils as DU } from 'lib/utils/Utils'

import './ClientExpenseDetails.scss'

const { format, formats } = DU

const LONG_DATE_FORMAT = formats.americanMediumDate

const formatDate = date => format(date, LONG_DATE_FORMAT)

function Detail({ title, children }) {
	return (
		<BaseDetail
			title={title}
			titleClassName="ClientExpenseDetail-Title"
			valueClassName="ClientExpenseDetail-Value"
			className="ClientExpenseDetail"
		>
			{children}
		</BaseDetail>
	)
}

export default function ClientExpenseDetails({ data = {} }) {
	return isNotEmpty(data) && (
		<>
			<Detail title="Type of expense">
				{data.typeTitle}
			</Detail>

			<Detail title="Cost of expense">
				{Currency(data.cost / 100, { symbol: '$ ', separator: '' }).format()}
			</Detail>

			<Detail title="Cumulative cost of expense">
				{Currency(data.cumulativeCost / 100, { symbol: '$ ', separator: '' }).format()}
			</Detail>

			<Detail title="Date of expense">
				{formatDate(data.date)}
			</Detail>

			<Detail title="Comment">
				{data.comment}
			</Detail>

			<Detail title="Date reported">
				{formatDate(data.reportedDate)}
			</Detail>

			<Detail title="Author">
				{data.author}
			</Detail>
		</>
	)
}