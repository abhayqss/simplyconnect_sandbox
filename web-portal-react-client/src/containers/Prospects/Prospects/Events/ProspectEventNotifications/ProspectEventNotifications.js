import React, {
	memo,
	useState,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { ErrorViewer } from 'components'

import {
	EventNotificationList
} from 'components/business/common'

import {
	useProspectEventNotificationsQuery
} from 'hooks/business/Prospects/Events'

import Avatar from 'containers/Avatar/Avatar'
import ContactViewer from 'containers/Admin/Contacts/ContactViewer/ContactViewer'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import './ProspectEventNotifications.scss'

function isIgnoredError(e = {}) {
	return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function ProspectEventNotifications({ eventId, prospectId, className }) {
	const [error, setError] = useState(null)

	const [contact, setContact] = useState(null)
	const [isContactViewerOpen, toggleContactViewer] = useState(false)

	const {
		sort,
		fetch,
		refresh,
		pagination,
		isFetching,
		data: { data = [] } = {}
	} = useProspectEventNotificationsQuery({
		eventId, prospectId
	}, { onError: setError })

	const onViewContact = useCallback(o => {
		setContact(o)
		toggleContactViewer(true)
	}, [])

	const onCloseContactViewer = useCallback(() => {
		setContact(null)
		toggleContactViewer(false)
	}, [])

	useEffect(() => fetch(), [fetch])

	return (
		<div className={cn("ProspectEventNotifications", className)}>
			<EventNotificationList
				data={data}
				isFetching={isFetching}
				pagination={pagination}
				className="ProspectEventNotificationList"
				onSort={sort}
				onRefresh={refresh}
				onViewContact={onViewContact}
				renderAvatar={o => (
					<Avatar
						id={o.contactAvatarId}
						name={o.contactFullName}
						className="EventNotification-ContactAvatar"
					/>
				)}
			/>

			{isContactViewerOpen && (
				<ContactViewer
					isOpen
					contactId={contact.id}
					onClose={onCloseContactViewer}
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

ProspectEventNotifications.propTypes = {
	eventId: PTypes.number,
	prospectId: PTypes.number,
	className: PTypes.string
}

export default memo(ProspectEventNotifications)