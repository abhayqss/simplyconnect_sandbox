import React, {
	memo,
	useEffect,
	useCallback
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
	useSideBarUpdate,
	useProspectQuery
} from 'hooks/business/Prospects'

import {
	useProspectCallHistoryQuery
} from 'hooks/business/Prospects/CallHistory'

import {
	isInteger,
	toNumberExcept
} from 'lib/utils/Utils'

import './ProspectCallHistory.scss'

function ProspectCallHistory() {
	const {
		state,
		setError,
		clearError
	} = useListState({})

	const params = useParams()

	const prospectId = toNumberExcept(
		params.clientId, [null, undefined]
	)

	const updateSideBar = useSideBarUpdate({ prospectId })

	const { data: prospect } = useProspectQuery(
		{ prospectId }, {
			staleTime: 0,
			enabled: isInteger(prospectId)
		}
	)

	const {
		fullName,
		associatedContact: contact
	} = prospect ?? {}

	const {
		sort,
		fetch,
		refresh,
		isFetching,
		pagination,
		data: { data = [] } = {}
	} = useProspectCallHistoryQuery(
		{ employeeId: contact?.id },
		{ onError: setError }
	)

	const onSort = useCallback((field, order) => {
		sort(field, order)
	}, [sort])

	const onRefresh = useCallback(page => {
		refresh(page)
	}, [refresh])

	useEffect(() => {
		updateSideBar()
	}, [updateSideBar])

	useEffect(() => {
		fetch()
	}, [fetch])

	return (
		<DocumentTitle title="Simply Connect | Call History">
			<div className="ProspectCallHistory">
				<Breadcrumbs
					items={compact([
						{ title: 'Prospects', href: '/prospects', isEnabled: true },
						fullName && { title: fullName || '', href: `/prospects/${prospectId}` },
						{ title: 'Call History', href: `/prospects/${prospectId}/call-history`, isActive: true }
					])}
				/>
				<div className="ProspectCallHistory-Header">
					<div className="ProspectCallHistory-Title">
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
					onRefresh={onRefresh}
					onSort={onSort}
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

export default memo(ProspectCallHistory)