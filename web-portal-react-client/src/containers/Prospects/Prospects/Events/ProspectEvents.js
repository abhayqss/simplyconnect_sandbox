import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	compact
} from 'underscore'

import DocumentTitle from 'react-document-title'

import { useParams } from 'react-router-dom'

import {
	Badge,
	Collapse
} from 'reactstrap'

import {
	ErrorViewer,
	Breadcrumbs
} from 'components'

import {
	Button
} from 'components/buttons'

import {
	SuccessDialog
} from 'components/dialogs'

import {
	useQueryInvalidation
} from 'hooks/common'

import {
	useSideBarUpdate,
	useProspectQuery
} from 'hooks/business/Prospects'

import {
	useProspectEventsQuery,
	useProspectEventListState,
	useProspectEventPageNumberQuery
} from 'hooks/business/Prospects/Events'

import {
	useCanAddProspectEventQuery
} from 'hooks/business/Prospects/Events'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import {
	isInteger,
	allAreInteger,
	toNumberExcept
} from 'lib/utils/Utils'

import { ReactComponent as Filter } from 'images/filters.svg'

import ProspectEventList from './ProspectEventList/ProspectEventList'
import ProspectEventFilter from './ProspectEventFilter/ProspectEventFilter'

import './ProspectEvents.scss'

function isIgnoredError(e = {}) {
	return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function ProspectEvents({ className }) {
	const [selected, setSelected] = useState(null)
	const [isFilterOpen, toggleFilter] = useState(true)

	const [isViewerOpen, toggleViewer] = useState(false)
	const [isEditorOpen, toggleEditor] = useState(false)
	const [isSaveEventSuccessDialogOpen, toggleSaveEventSuccessDialog] = useState(false)

	const params = useParams()
	const invalidate = useQueryInvalidation()

	const prospectId = toNumberExcept(
		params.prospectId, [null, undefined]
	)

	const {
		state,
		setError,
		clearError,
		changeFilter
	} = useProspectEventListState()

	const filter = useMemo(() => state.filter.toJS(), [])

	const {
		sort,
		fetch,
		refresh,
		pagination,
		isFetching,
		data: { data = [] } = {}
	} = useProspectEventsQuery(
		{ prospectId, ...filter },
		{ onError: setError }
	)

	const {
		data: prospect
	} = useProspectQuery(
		{ prospectId },
		{ staleTime: 0, enabled: isInteger(prospectId) }
	)

	const {
		data: canAdd
	} = useCanAddProspectEventQuery(
		{ prospectId },
		{ staleTime: 0, enabled: isInteger(prospectId) }
	)

	const {
		data: pageNumber,
		isFetching: isFetchingPageNumber
	} = useProspectEventPageNumberQuery(
		{ prospectId, eventId: selected.id },
		{
			staleTime: 0,
			enabled: allAreInteger(prospectId, selected.id)
		}
	)

	const select = useCallback(o => {}, [])

	const isSelected = useCallback(o => {}, [])

	const onCloseSaveEventSuccessDialog = useCallback(o => {}, [])

	const onViewSavedEvent = useCallback(o => {}, [])

	const onAdd = useCallback(() => {
		toggleEditor(true)
	}, [])

	useEffect(() => { fetch() }, [fetch])

	return (
		<div className={cn('ProspectEvents', className)}>
			<DocumentTitle title="Simply Connect | Client Events">
				<>
					<Breadcrumbs
						items={compact([
							{ title: 'Prospects', href: '/prospects', isEnabled: true },
							prospect && { title: prospect.fullName || '', href: `/prospects/${prospectId}` },
							{ title: 'Events', href: `/prospects/${prospectId}/events`, isActive: true }
						])}
						className="margin-bottom-32"
					/>

					{prospect && (
						<div className="ProspectEvents-Header page-header">
							<div className="ProspectEvents-HeaderItem page-header-item">
								<div className="ProspectEvents-Title page-title">
									<div className="ProspectEvents-TitleText page-title-text">
										<div className="page-title-main-text">
											Events&nbsp;
										</div>
										<div className="page-title-second-text">
											/ {prospect.fullName}
										</div>
									</div>
									{(pagination.totalCount > 0) && (
										<Badge color='info' className="Badge Badge_place_top-right">
											{pagination.totalCount}
										</Badge>
									)}
								</div>
							</div>
							<div className="ProspectEvents-HeaderItem page-header-item">
								<div className="ProspectEvents-Actions page-actions">
									<Filter
										className={cn(
											'ProspectEventFilter-Icon',
											isFilterOpen
												? 'ProspectEventFilter-Icon_rotated_90'
												: 'ProspectEventFilter-Icon_rotated_0',
										)}
										onClick={() => toggleFilter(!isFilterOpen)}
									/>
									{canAdd && (
										<Button
											color="success"
											id="add-expense"
											onClick={onAdd}
											className="ProspectEvents-Action"
										>
											Create Event
										</Button>
									)}
								</div>
							</div>
						</div>
					)}

					<Collapse isOpen={isFilterOpen}>
						<ProspectEventFilter
							prospectId={prospectId}
							onChange={changeFilter}
							onReset={isSaved => isSaved && fetch()}
							onApply={fetch}
						/>
					</Collapse>

					<div className="ProspectEvents-NavigationContainer row">
						<div
							className={cn(
								"col-sm-6",
								"col-xl-4",
								"ProspectEvents-Navigation",
							)}
						>
							<ProspectEventList
								data={data}
								prospectId={prospectId}
								pagination={pagination}
								isFetching={isFetching || isFetchingPageNumber}
								onSelect={select}
								onRefresh={refresh}
								isSelected={isSelected}
							/>
						</div>
						<div
							className={cn(
								"col-sm-6",
								"col-xl-8",
								"Events-DetailsContainer"
							)}
						>
						</div>
					</div>
					{isSaveEventSuccessDialogOpen && (
						<SuccessDialog
							isOpen
							title="The event has been submitted"
							buttons={[
								{
									text: 'Close',
									outline: true,
									onClick: onCloseSaveEventSuccessDialog
								},
								{
									text: 'View details',
									onClick: onViewSavedEvent
								}
							]}
						/>
					)}
					{state.error && !isIgnoredError(state.error) && (
						<ErrorViewer
							isOpen
							error={state.error}
							onClose={() => setError(null)}
						/>
					)}
				</>
			</DocumentTitle>
		</div>
	)
}

ProspectEvents.propTypes = {
	className: PTypes.string
}

export default memo(ProspectEvents)