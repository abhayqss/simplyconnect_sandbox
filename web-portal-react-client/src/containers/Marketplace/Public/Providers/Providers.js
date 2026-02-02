import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import {
	compose
} from 'underscore'

import cn from 'classnames'

import DocumentTitle from 'react-document-title'
import { useDebouncedCallback } from 'use-debounce'

import {
	useParams,
	useHistory
} from 'react-router-dom'

import {
	Col,
	Row,
	Collapse
} from 'reactstrap'

import {
	Loader,
	Footer,
	FlatList,
	ErrorViewer,
	SearchField
} from 'components'

import {
	Button
} from 'components/buttons'

import {
	withMarketplaceContext
} from 'hocs'

import {
	useDeferred,
	useSubDomain
} from 'hooks/common'

import {
	useOrganizationQuery,
	useProviderListState,
	useProvidersInfiniteQuery,
} from 'hooks/business/Marketplace/Public'

import {
	hyphenate
} from 'lib/utils/Utils'

import { path } from 'lib/utils/ContextUtils'

import { ReactComponent as Filter } from 'images/filters.svg'

import InquiryEditor from './Inquiry/InquiryEditor/InquiryEditor'

import {
	ProviderHeader,
	ProviderFilter,
	ProviderSummary,
	FeaturedProviders
} from './'

import './Providers.scss'

function Providers() {
	const [selected, setSelected] = useState(null)
	const [searchText, setSearchText] = useState(null)
	const [isFilterOpen, toggleFilter] = useState(true)

	const [isInquiryEditorOpen, setInquiryEditorOpen] = useState(false)

	const history = useHistory()
	const organizationCode = useSubDomain()

	const {
		state,
		setError,
		clearError,
		changeFilter
	} = useProviderListState()

	const { error } = state

	const {
		data: organization
	} = useOrganizationQuery({ organizationCode })

	const filter = useMemo(
		() => state.filter.toJS(), [state.filter]
	)

	const {
		fetch,
		refetch,
		fetchMore,
		isFetching,
		pagination,
		isFetchingMore,
		aggregatedData: data
	} = useProvidersInfiniteQuery({
		...filter,
		searchText,
		organizationCode
	}, {
		staleTime: 0,
		onError: setError
	})

	const refetchDeferred = useDeferred(refetch)

	const onToggleFilter = useCallback(() => {
		toggleFilter(v => !v)
	}, [])

	const onChangeSearchText = useCallback((name, value) => {
		setSearchText(value)
	}, [])

	const onClearSearchText = useCallback(() => {
		refetchDeferred()
		setSearchText(null)
	}, [refetchDeferred])

	const onView = useCallback(data => {
		history.push(
			path(`/communities/${hyphenate(data.name)}--@id=${data.id}`),
		)
	}, [history])

	const debouncedFetchMore = useDebouncedCallback(fetchMore, 100)

	function onCreateInquiry(data) {
		setSelected(data)
		setInquiryEditorOpen(true)
	}

	const onCloseInquiryEditor = useCallback(() => {
		setInquiryEditorOpen(false)
	}, [])

	useEffect(() => {
		fetch()
	}, [fetch])

	return (
		<DocumentTitle
			title={`${organization?.loginCompanyId} | Simply Connect`}>
			<div className="MarketplaceProviders">
				<ProviderHeader/>

				<div className="MarketplaceProviders-Body">
					{/*<FeaturedProviders
						onView={onView}
						onInquiry={onCreateInquiry}
					/>*/}

					<div className="MarketplaceProviderFilter-Header">
						<div className="MarketplaceProviderFilter-Title">
							Filter By
						</div>
						<div className="flex-1 d-flex flex-row justify-content-end">
							<div className="h-flexbox margin-right-24">
								<SearchField
									type="text"
									maxLength={256}
									name="searchText"
									value={searchText}
									placeholder="Search by name, zip code"
									className="MarketplaceProviderFilter-TextField margin-right-10"
									onEnterKeyDown={() => refetch()}
									onChange={onChangeSearchText}
									onClear={onClearSearchText}
								/>
								<Button
									color="success"
									disabled={(searchText?.length ?? 0) < 2}
									onClick={() => refetch()}
								>
									Search
								</Button>
							</div>

							<Filter
								className={cn(
									'MarketplaceProviderFilter-Icon',
									isFilterOpen
										? 'MarketplaceProviderFilter-Icon_rotated_90'
										: 'MarketplaceProviderFilter-Icon_rotated_0'
								)}
								onClick={onToggleFilter}
							/>
						</div>
					</div>

					<Collapse isOpen={isFilterOpen}>
						<ProviderFilter
							onChange={changeFilter}
							onApply={refetch}
							onReset={() => {
								refetch()
								setSearchText(null)
							}}
							className="MarketplaceProviders-Filter"
						/>
					</Collapse>

					<Row className='flex-1 align-items-center'>
						<Col lg={2} md={4}/>
						<Col lg={8} md={4}>
							<div className="MarketplaceProviderList">
								{!isFetching && !data.length && (
									<div className="MarketplaceProviderList-NotFound font-size-15 text-center">
										No communities found. Please change the filtering criteria.
									</div>
								)}

								{!!data.length && (
									<FlatList
										list={data}
										itemKey="id"
										className={cn(
											'MarketplaceProviderList-List',
											data.length > 1 && 'd-grid grid-template-columns-2',
											{ 'MarketplaceProviderList-List_fetching': isFetchingMore }
										)}
										itemClassName={cn(
											'MarketplaceProviderList-ListItem',
											data.length > 1 && 'max-width-none'
										)}
										loadMore={debouncedFetchMore}
										shouldLoadMore={!isFetchingMore && data.length < pagination.totalCount}
										onEndReachedThreshold={100}
									>
										{provider => {
											return (
												<ProviderSummary
													data={provider}
													onView={onView}
													onInquiry={onCreateInquiry}
													className="h-100"
												/>
											)
										}}
									</FlatList>
								)}
								{isFetching && !isFetchingMore && (
									<Loader className="MarketplaceProviderList-Loader"/>
								)}

								{isFetchingMore && (
									<Loader className="MarketplaceProviderList-Loader_paginated"/>
								)}
							</div>
						</Col>
						<Col lg={2} md={4}/>
					</Row>
				</div>

				<Footer hasLogo>
					<div className="MarketplaceProviders-OrganizationPhone">
						Call (844) 666-3038
					</div>
				</Footer>

				<InquiryEditor
					communityId={selected?.id}
					serviceCategories={selected?.serviceCategories}
					isOpen={isInquiryEditorOpen}
					onClose={onCloseInquiryEditor}
				/>

				{error && (
					<ErrorViewer
						isOpen
						error={error}
						onClose={clearError}
					/>
				)}
			</div>
		</DocumentTitle>
	)
}

export default compose(
	memo,
	withMarketplaceContext
)(Providers)