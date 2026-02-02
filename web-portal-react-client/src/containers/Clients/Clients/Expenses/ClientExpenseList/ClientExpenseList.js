import React, {
	useMemo
} from 'react'

import Currency from 'currency.js'

import {
	UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { Table } from 'components'

import {
	DateUtils as DU
} from 'lib/utils/Utils'

import './ClientExpenseList.scss'

const DATE_FORMAT = DU.formats.americanMediumDate

function formatDate(date) {
	return DU.format(date, DATE_FORMAT)
}

export default function ClientExpenseList(
	{
		data,
		pagination,
		isFetching,
		onSort,
		onView,
		onRefresh
	}
) {
	const columns = useMemo(() => (
		[
			{
				dataField: 'typeTitle',
				text: 'Expense Type',
				headerAlign: 'left',
				formatter: (v, row) => (
					<a
						id={`expense-${row.id}-type`}
						className="link"
						onClick={e => {
							e.preventDefault()
							onView(row)
						}}
					>
						{v}
						<Tooltip
							target={`expense-${row.id}-type`}
							modifiers={[
								{
									name: 'offset',
									options: { offset: [0, 6] }
								},
								{
									name: 'preventOverflow',
									options: { boundary: document.body }
								}
							]}
						>
							View expense details
						</Tooltip>
					</a>
				)
			},
			{
				dataField: 'cost',
				text: 'Cost, $',
				formatter: v => Currency(v / 100, { symbol: '', separator: '' }).format()
			},
			{
				dataField: 'date',
				text: 'Expense Date',
				sort: true,
				align: 'right',
				headerAlign: 'right',
				onSort,
				formatter: v => v && formatDate(v, DATE_FORMAT)
			},
			{
				dataField: 'reportedDate',
				text: 'Date Reported',
				headerAlign: 'right',
				align: 'right',
				sort: true,
				onSort,
				formatter: v => v && formatDate(v, DATE_FORMAT)
			},
			{
				dataField: 'author',
				text: 'Author',
				align: 'right',
				sort: true,
				onSort,
				headerAlign: 'right'
			}
		]
	) , [onSort, onView])

	return (
		<Table
			hasHover
			hasOptions
			hasPagination
			keyField="id"
			title="Audit Logs"
			noDataText="No expenses to display."
			isLoading={isFetching}
			className='ClientExpenseList'
			containerClass='ClientExpenseListContainer'
			data={data}
			pagination={pagination}
			columns={columns}
			hasCaption={false}
			columnsMobile={['type', 'date', 'author']}
			onRefresh={onRefresh}
		/>
	)
}