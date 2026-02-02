import React, {
	memo,
	useState,
	useEffect,
	useCallback,
} from 'react'

import PTypes from 'prop-types'

import {
	omit
} from 'underscore'

import {
	Col, Row
} from 'reactstrap'

import {
	Directory,
	ErrorViewer,
	SearchField
} from 'components'

import {
	Button
} from 'components/buttons'

import {
	DocumentList
} from 'components/business/Documents'

import {
	useClientDocumentsQuery
} from 'hooks/business/client/documents'

import {
	defer
} from 'lib/utils/Utils'

import {
	path
} from 'lib/utils/ContextUtils'

import {
	E_SIGN_STATUSES
} from 'lib/Constants'

import {
	DocumentManager
} from 'containers/Clients/Clients/Documents'

import './DocumentDirectory.scss'

const {
	SENT,
	SIGNED,
	RECEIVED,
	REQUESTED
} = E_SIGN_STATUSES

const DocumentFilter = memo(function DocumentFilter({ data, onChange, onClear, onApply }) {
	return (
		<Row className='DocumentFilter ClientDocumentDirectory-Filter'>
			<Col md={8} lg={4}>
				<SearchField
					name="text"
					value={data.text}
					placeholder="Search by title or category"
					className="ClientDocumentDirectory-SearchField"

					onChange={onChange}
					onClear={onClear}
				/>
			</Col>
			<Col md={4} lg={4}>
				<Button
					id="doc-directory__doc-filter-search-btn"
					color="success"
					onClick={() => onApply()}
				>
					Search
				</Button>
			</Col>
		</Row>
	)
})

function DocumentDirectory(
	{
		isOpen,
		clientId,

		selectedDocuments,
		selectedDocumentMaxCount,

		onClose,
		onComplete,
	}
) {
	const [error, setError] = useState(null)

	const [filter, setFilter] = useState({})
	const [viewedDoc, setViewedDoc] = useState(null)

	const [isDocManagerOpen, toggleDocManager] = useState(false)

	const {
		sort,
		fetch,
		refresh,
		isFetching,
		pagination,
		data: { data = [] } = {}
	} = useClientDocumentsQuery(
		{
			size: 6,
			clientId,
			includeDeleted: false,
			title: filter.text,
			includeWithoutSignature: true,
			includeSearchByCategoryName: true,
			signatureStatusNames: [SENT, SIGNED, RECEIVED, REQUESTED]
		},
		{ onError: setError }
	)

	const openDocManager = useCallback(o => {
		setViewedDoc(o)
		toggleDocManager(true)
	}, [])

	const closeDocManager = useCallback(() => {
		setViewedDoc(null)
		toggleDocManager(false)
	}, [])

	const changeFilter = useCallback((name, value) => {
		setFilter(prev => ({ ...prev, [name]: value }))
	}, [])

	const clearFilter = useCallback(name => {
		setFilter(prev => omit(prev, name))
		defer().then(refresh)
	}, [refresh])

	const renderFilter = useCallback(() => (
		<DocumentFilter
			data={filter}
			onChange={changeFilter}
			onClear={clearFilter}
			onApply={refresh}
		/>
	), [
		filter,
		refresh,
		changeFilter,
		clearFilter
	])

	useEffect(() => { fetch() }, [fetch])

	return (
		<>
			<Directory
				isOpen={isOpen}

				isLoading={isFetching}
				title="Upload Document"
				completeBtnText="Upload"
				itemUniqKeyField="id"
				selectedItems={selectedDocuments}
				className="ClientDocumentDirectory"

				onClose={onClose}
				onComplete={onComplete}
			>
				{({ setSelectedItems }) => (
					<DocumentList
						data={data}
						hasAuthorCol
						hasCreatedDateCol
						hasSizeCol={false}
						hasSignatureStatusCol
						hasActionsCol={false}
						pagination={pagination}
						isFetching={isFetching}
						hasDescriptionCol={false}
						noDataText="No results found."
						defaultSelected={selectedDocuments}
						selectedMaxCount={selectedDocumentMaxCount}
						className="ClientDocumentDirectory-List"
						getPath={o => path(`/clients/${clientId}/documents/${o.id}-${o.title}`)}
						renderCaption={renderFilter}
						onView={openDocManager}
						onSort={sort}
						onRefresh={refresh}
						onSelect={setSelectedItems}
					/>
				)}
			</Directory>

			<DocumentManager
				clientId={clientId}
				isOpen={isDocManagerOpen}
				hasEditBtn={false}
				hasDeleteBtn={false}
				documentId={viewedDoc?.id}
				documentName={viewedDoc?.title}
				documentMimeType={viewedDoc?.mimeType}
				onClose={closeDocManager}
			/>

			{error && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</>
	)
}

DocumentDirectory.propTypes = {
	isOpen: PTypes.bool,
	clientId: PTypes.number,
	selectedDocuments: PTypes.array,
	selectedDocumentMaxCount: PTypes.array,

	onClose: PTypes.func,
	onComplete: PTypes.func,
}

export default memo(DocumentDirectory)
