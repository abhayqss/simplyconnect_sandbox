import React, {
	memo,
	useMemo
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { Table } from 'components'

import { withTooltip } from 'hocs'

import {
	format, formats
} from 'lib/utils/DateUtils'

import './EventNotificationList.scss'

function EventNotificationList(
	{
		data,
		pagination,
		isFetching,
		renderAvatar,
		onSort,
		onRefresh,
		className,
		onViewContact
	}
) {
	const columns = useMemo(() =>
		[
			{
				dataField: 'contactFullName',
				text: 'Contact',
				sort: true,
				headerClasses: 'EventNotificationList-ContactColHeader',
				onSort,
				formatter: (v, row) => {
					return withTooltip({
						placement: 'top-start',
						className: 'EventNotification-Hint',
						text: (
							<>
								<div className="font-weight-bold text-left">
									{row.hint.split('\n')[0]}
								</div>
								{row.hint.split('\n')[1]}
							</>
						)
					})(
						<div className="d-flex flex-row align-items-center">
							{renderAvatar(row)}
							<div>
								<div
									className={cn(
										'EventNotification-ContactButton',
										{ 'EventNotification-ContactButton_Disabled': !row.canViewContact }
									)}
									onClick={() => {
										if (row.canViewContact) {
											onViewContact({ id: row.contactId })
										}
									}}
								>
									<div className="EventNotification-ContactFullName">
										{row.contactFullName}
									</div>
									<div className="EventNotification-ContactRole">
										{row.careTeamMemberRole}
									</div>
								</div>
							</div>
						</div>
					)({ isTooltipEnabled: row.hint && row.contactId })
				}
			},
			{
				dataField: 'responsibility',
				text: 'Responsibility',
				headerClasses: 'EventNotificationList-ResponsibilityColHeader',
				sort: true,
				onSort
			},
			{
				dataField: 'organization',
				text: 'Organization',
				headerClasses: 'EventNotificationList-OrganizationColHeader',
				sort: true,
				onSort
			},
			{
				dataField: 'channels',
				text: 'Channel',
				classes: 'hide-on-tablet',
				headerClasses: 'EventNotificationList-ChannelColHeader hide-on-tablet'
			},
			{
				dataField: 'dateCreated',
				text: 'Date',
				headerAlign: 'right',
				align: 'right',
				headerClasses: 'EventNotificationList-DateColHeader',
				formatter: v => format(v, formats.longDateMediumTime12)
			}
		], [onSort, renderAvatar, onViewContact])

	return (
		<Table
			hasHover
			hasOptions
			hasPagination
			hasCaption={false}
			keyField="id"
			title="Call History"
			noDataText="No calls."
			isLoading={isFetching}
			className={cn('CallHistoryRecordList', className)}
			containerClass="CallHistoryRecordListContainer"
			data={data}
			pagination={pagination}
			columns={columns}
			columnsMobile={['name', 'date']}
			onRefresh={onRefresh}
		/>
	)
}

EventNotificationList.propTypes = {
	data: PTypes.object,
	pagination: PTypes.object,
	isFetching: PTypes.bool,
	renderAvatar: PTypes.func,
	onSort: PTypes.func,
	onRefresh: PTypes.func,
	className: PTypes.string,
	onViewContact: PTypes.func
}

export default memo(EventNotificationList)