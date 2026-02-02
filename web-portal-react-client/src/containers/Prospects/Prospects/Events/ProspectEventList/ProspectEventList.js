import React, {
	useMemo,
	useCallback,
} from 'react'

import { Table } from 'components'

import {
	useEventTypesQuery
} from 'hooks/business/directory/query'

import {
	EVENT_GROUP_COLORS
} from 'lib/Constants'

import {
	format,
	formats
} from 'lib/utils/DateUtils'

import './ClientExpenseList.scss'
import { isInteger } from '../../../../../lib/utils/Utils'
import { any, find } from 'underscore'

const TIME_FORMAT = formats.time
const DATE_FORMAT = formats.americanMediumDate

function formatTime(date) {
	return format(date, TIME_FORMAT)
}

function formatDate(date) {
	return format(date, DATE_FORMAT)
}

export default function ProspectEventList(
	{
		data,
		prospectId,
		pagination,
		isFetching,
		isSelected,
		onSelect,
		onRefresh
	}
) {
	const {
		data: types
	} = useEventTypesQuery({ prospectId }, {
		staleTime: 0,
		enabled: isInteger(prospectId)
	})

	const getEventIndicatorColor = useCallback(e => {
		const group = find(types, group => any(
				group.eventTypes, o => o.name === e.typeName
			)
		)

		return EVENT_GROUP_COLORS[group?.name] ?? '#fff1ca'
	}, [types])

	const columns = useMemo(() => (
		[
			{
				text: '',
				dataField: 'clientName',
				style: (cell, row) => ({
					borderLeft: '8px solid',
					borderLeftColor: getEventIndicatorColor(row)
				}),
				formatter: (v, row) => (
					<>
						<div
							className="Event-ClientName">
							{row.clientName}
						</div>
						<div
							className="Event-Type">
							{row.typeTitle}
						</div>
					</>
				)
			},
			{
				text: '',
				dataField: 'date',
				formatter: (v) => (
					<>
						<div
							className="Event-Date">
							{formatDate(v)}
						</div>
						<div
							className="Event-Time">
							{formatTime(v)}
						</div>
					</>
				)
			}
		]
	), [getEventIndicatorColor])

	return (
		<Table
			keyField="id"
			hasHover={true}
			hasPagination={true}

			columns={columns}

			data={data}
			isLoading={isFetching}
			pagination={pagination}

			rowEvents={{ onClick: onSelect }}
			getRowStyle={row => ({
				...isSelected(row) ? ({
					backgroundColor: '#f2fbff'
				}) : null
			})}

			noDataText="No events found."
			className="ProspectEventList"
			containerClass="ProspectEventListContainer"

			onRefresh={onRefresh}
		/>
	)
}