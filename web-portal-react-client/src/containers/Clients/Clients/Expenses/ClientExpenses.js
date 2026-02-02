import React, {
	memo,
	useState,
	useEffect,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { compact } from 'underscore'

import DocumentTitle from 'react-document-title'

import { useParams } from 'react-router-dom'

import { Badge } from 'reactstrap'

import {
	ErrorViewer,
	Breadcrumbs
} from 'components'

import {
	Button
} from 'components/buttons'

import {
	useListState,
	useQueryInvalidation
} from 'hooks/common'

import {
	useSideBarUpdate
} from 'hooks/business/client'

import {
	useClientQuery
} from 'hooks/business/client/queries'

import {
	useClientExpensesQuery,
	useCanAddClientExpenseQuery
} from 'hooks/business/client/expences'

import {
	toNumberExcept
} from 'lib/utils/Utils'

import ClientExpenseList from './ClientExpenseList/ClientExpenseList'
import ClientExpenseViewer from './ClientExpenseViewer/ClientExpenseViewer'
import ClientExpenseEditor from './ClientExpenseEditor/ClientExpenseEditor'

import './ClientExpenses.scss'

function ClientExpenses({ className }) {
	const [selected, setSelected] = useState(null)
	const [isViewerOpen, toggleViewer] = useState(false)
	const [isEditorOpen, toggleEditor] = useState(false)

	const params = useParams()
	const invalidate = useQueryInvalidation()

	const clientId = toNumberExcept(params.clientId, [null, undefined])

	const {
		state,
		setError,
		clearError
	} = useListState({})

	const {
		data: client
	} = useClientQuery({ clientId }, { staleTime: 0 })

	const updateSideBar = useSideBarUpdate({ clientId })

	const {
		sort,
		fetch,
		refresh,
		isFetching,
		pagination,
		data: { data = [] } = {}
	} = useClientExpensesQuery(
		{ clientId }, { onError: setError }
	)

	const {
		data: canAdd
	} = useCanAddClientExpenseQuery(
		{ clientId }, { staleTime: 0 }
	)

	const invalidateCount = useCallback(() => {
		invalidate('Client.ExpenseCount', { clientId })
	}, [clientId, invalidate])

	const onView = useCallback(o => {
		setSelected(o)
		toggleViewer(true)
	}, [])

	const onCloseViewer = useCallback(() => {
		setSelected(null)
		toggleViewer(false)
	}, [])

	const onAdd = useCallback(() => {
		toggleEditor(true)
	}, [])

	const onCloseEditor = useCallback(() => {
		toggleEditor(false)
	}, [])

	const onSaveSuccess = useCallback(() => {
		refresh()
		invalidateCount()
	}, [refresh, invalidateCount])

	useEffect(() => {
		updateSideBar()
	}, [updateSideBar])

	useEffect(() => { fetch() }, [fetch])

	return (
		<DocumentTitle title="Simply Connect | Client Expenses">
			<div className={cn("ClientExpenses", className)}>
				<Breadcrumbs
					items={compact([
						{ title: 'Clients', href: '/clients', isEnabled: true },
						client && { title: client.fullName || '', href: `/clients/${clientId}` },
						{ title: 'Expenses', href: `/clients/${clientId}/call-history`, isActive: true }
					])}
					className="margin-bottom-32"
				/>

				{client && (
					<div className="ClientExpenses-Header page-header">
						<div className="ClientExpenses-HeaderItem page-header-item">
							<div className="ClientExpenses-Title page-title">
								<div className="ClientExpenses-TitleText page-title-text">
									<div className="page-title-main-text">
										Expenses&nbsp;
									</div>
									<div className="page-title-second-text">
										/ {client.fullName}
									</div>
								</div>
								{(pagination.totalCount > 0) && (
									<Badge color='info' className="Badge Badge_place_top-right">
										{pagination.totalCount}
									</Badge>
								)}
							</div>
						</div>
						<div className="ClientExpenses-HeaderItem page-header-item">
							<div className="ClientExpenses-Actions page-actions">
								{canAdd && (
									<Button
										color="success"
										id="add-expense"
										onClick={onAdd}
										className="ClientExpenses-Action"
									>
										Add Expense
									</Button>
								)}
							</div>
						</div>
					</div>
				)}

				<ClientExpenseList
					data={data}
					isFetching={isFetching}
					pagination={pagination}
					onView={onView}
					onSort={sort}
					onRefresh={refresh}
				/>

				<ClientExpenseViewer
					isOpen={isViewerOpen}
					clientId={clientId}
					expenseId={selected?.id}
					onClose={onCloseViewer}
				/>

				<ClientExpenseEditor
					isOpen={isEditorOpen}
					clientId={clientId}
					onClose={onCloseEditor}
					onSaveSuccess={onSaveSuccess}
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

ClientExpenses.propTypes = {
	className: PTypes.string
}

export default memo(ClientExpenses)