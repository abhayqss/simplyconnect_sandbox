import React, {
	memo,
	useEffect
} from 'react'

import { compact } from 'underscore'

import { useParams } from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import {
	Breadcrumbs,
	ErrorViewer
} from 'components'

import {
	CallHistoryRecordList as List
} from 'components/business/common'


import {
	useListState
} from 'hooks/common'

import {
	useSideBarUpdate
} from 'hooks/business/client'

import {
	useClientQuery
} from 'hooks/business/client/queries'

import {
	useClientCallHistoryQuery
} from 'hooks/business/client/call-history'

import {
	isInteger,
	toNumberExcept
} from 'lib/utils/Utils'

import './ClientCallHistory.scss'

function ClientCallHistory() {
	const {
		state,
		setError,
		clearError
	} = useListState({})

	const params = useParams()

	const clientId = toNumberExcept(
		params.clientId, [null, undefined]
	)

	const updateSideBar = useSideBarUpdate({ clientId })

	const { data: client } = useClientQuery(
		{ clientId }, {
			staleTime: 0,
			enabled: isInteger(clientId)
		}
	)

	const {
		fullName,
		associatedContact: contact
	} = client ?? {}

    const {
        sort,
        fetch,
        refresh,
        isFetching,
        pagination,
        data: { data } = {}
    } = useClientCallHistoryQuery(
        { employeeId: contact?.id },
        {
            retry: 1,
            onError: setError,
            enabled: isInteger(contact?.id)
        }
    )

	useEffect(() => {
		updateSideBar()
	}, [updateSideBar])

	useEffect(() => {
		if (contact) fetch()
	}, [fetch, contact])

	return (
		<DocumentTitle title="Simply Connect | Call History">
			<div className="ClientCallHistory">
				<Breadcrumbs
					items={compact([
						{ title: 'Clients', href: '/clients', isEnabled: true },
						fullName && { title: fullName || '', href: `/clients/${clientId}` },
						{ title: 'Call History', href: `/clients/${clientId}/call-history`, isActive: true }
					])}
				/>
				<div className="ClientCallHistory-Header">
					<div className="ClientCallHistory-Title">
						<div className="page-title-main-text">
							Call History&nbsp;
						</div>
						{contact && (
							<div className="page-title-second-text">
								/ {contact.fullName}
							</div>
						)}
					</div>
				</div>
				<List
					data={data}
					isFetching={isFetching}
					pagination={pagination}
					onRefresh={refresh}
					onSort={sort}
				/>
				{state.error && (
					<ErrorViewer
						isOpen
						error={state.error}
						onClose={clearError}
					/>
				)}
			</div>
		</DocumentTitle>
	)
}

export default memo(ClientCallHistory)