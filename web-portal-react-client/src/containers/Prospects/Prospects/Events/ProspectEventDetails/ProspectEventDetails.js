import React, {
	memo,
	useMemo,
	useState,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	Tabs,
	Dropdown,
	ErrorViewer,
	DataLoadable
} from 'components'

import {
	ProspectEventDetails as Details
} from 'components/business/Prospects'

import {
	useProspectQuery
} from 'hooks/business/Prospects'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import {
	allAreInteger
} from 'lib/utils/Utils'

import {
	map
} from 'lib/utils/ArrayUtils'

import {
	noop
} from 'lib/utils/FuncUtils'

import ProspectEventNotifications from '../ProspectEventNotifications/ProspectEventNotifications'

import './ProspectEventDetails.scss'

const TAB = {
	DESCRIPTION: 0,
	NOTIFICATIONS: 1
}

const TAB_TITLE = {
	[TAB.DESCRIPTION]: 'Event Description',
	[TAB.NOTIFICATIONS]: 'Notifications Sent'
}

const TAB_SHORT_TITLE = {
	[TAB.DESCRIPTION]: 'Description',
	[TAB.NOTIFICATIONS]: 'Notifications'
}

const TABS = [
	{
		name: TAB.DESCRIPTION,
		title: TAB_TITLE[TAB.DESCRIPTION],
		shortTitle: TAB_SHORT_TITLE[TAB.DESCRIPTION]
	},
	{
		name: TAB.NOTIFICATIONS,
		title: TAB_TITLE[TAB.NOTIFICATIONS],
		shortTitle: TAB_SHORT_TITLE[TAB.NOTIFICATIONS]
	}
]

function isIgnoredError(e = {}) {
	return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function getWindowWidth() {
	return document.body.getBoundingClientRect().width
}

function ProspectEventDetails(
	{
		eventId,
		prospectId,
		className
	}
) {
	const [error, setError] = useState(null)
	const [tab, setTab] = useState(TAB.DESCRIPTION)

	const {
		isFetching,
		data: event
	} = useProspectQuery({
		eventId, prospectId
	}, {
		staleTime: 0,
		onError: setError,
		enabled: allAreInteger(eventId, prospectId)
	})

	const changeTab = useCallback(() => {}, [])

	const windowWidth = getWindowWidth()

	const mappedTabs = useMemo(() => {
		map(TABS, o => ({
			value: o.name,
			text: windowWidth < 1200 ? o.shortTitle : o.title,
			isActive: tab === o.name,
			onClick: setTab
		}))
	}, [tab, windowWidth])

	const onViewDocument = useCallback(() => {}, [])
	const onViewAppointment = useCallback(() => {}, [])

	return (
		<div className={cn("ProspectEventDetails", className)}>
			<div className="ProspectEventDetails-Header">
				<Tabs
					items={mappedTabs}
					onChange={setTab}
					className='ProspectEventDetails-Tabs'
					containerClassName="ProspectEventDetails-TabsContainer"
				/>

				<Dropdown
					value={tab}
					items={mappedTabs}
					toggleText={TAB_TITLE[tab]}
					className="ProspectEventDetails-Dropdown Dropdown_theme_blue Dropdown_adaptive"
				/>
			</div>

			{tab === 0 && (
				<DataLoadable
					data={event}
					isLoading={isFetching}
					noDataText="No event details."
				>
					{data => (
						<Details
							data={data}
							onViewDocument={onViewDocument}
							onViewAppointment={onViewAppointment}
						/>
					)}
				</DataLoadable>
			)}

			{tab === 1 && (
				<ProspectEventNotifications
					eventId={eventId}
					prospectId={prospectId}
				/>
			)}

			{error && !isIgnoredError(error) && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</div>
	)
}

ProspectEventDetails.propTypes = {
	eventId: PTypes.number,
	prospectId: PTypes.number,
	className: PTypes.string
}

export default memo(ProspectEventDetails)