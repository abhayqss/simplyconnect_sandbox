import React, {
	useMemo,
	useState
} from 'react'

import {
	map,
	find,
	chain,
	first,
	filter
} from 'underscore'

import cn from 'classnames'

import {
	useFeaturedCommunitiesQuery
} from 'hooks/business/Marketplace'

import {
	Table,
	AlertPanel
} from 'components'

import {
	TextField,
	CheckboxField
} from 'components/Form'

import { IconButton } from 'components/buttons'

import {
	isInteger,
	getDataPage
} from 'lib/utils/Utils'

import { PAGINATION } from 'lib/Constants'

import { ReactComponent as ArrowTop } from 'images/arrowtop.svg'

import './FeaturedServiceProviderSection.scss'

const { MAX_SIZE } = PAGINATION

const ORDER_SHIFT = {
	UP: -1,
	DOWN: 1
}

function FeaturedServiceProviderSection(
	{
		canEdit,
		isVisible,
		communityId,
		communities: selectedCommunities,
		organizationId,
		onChange
	}
) {
	const [page, setPage] = useState(1)
	const [sortOrder, setSortOrder] = useState('asc')

	const {
		data: { data } = {},
		isFetching
	} = useFeaturedCommunitiesQuery({
		communityId,
		size: MAX_SIZE,
		sort: 'communityName',
		order: 'asc'
	}, {
		enabled: isInteger(communityId),
		refetchOnMount: 'always'
	})

	const activeSelectedCommunities = useMemo(() => chain(selectedCommunities)
			.map(o => ({
				...(find(data, i => i.id === o.id) || {}),
				...o
			}))
			.filter(
				o => o.selected && o.confirmVisibility
			)
			.sortBy(o => o.displayOrder)
			.map((o, index) => ({
				...o,
				displayOrder: index + 1
			}))
			.value(),
		[selectedCommunities, data]
	)

	const deselectedCommunities = useMemo(() => filter(
		selectedCommunities,
		o => o.deselected
	), [selectedCommunities])

	const hiddenSelectedCommunities = useMemo(() => filter(
		selectedCommunities,
		o => o.selected && !o.confirmVisibility
	), [selectedCommunities])

	const pagination = useMemo(() => ({
		page: page,
		size: 15,
		totalCount: data?.length
	}), [page, data])

	const paginatedData = useMemo(() => {
		if (!canEdit) return activeSelectedCommunities

		const orderedActiveSelectedCommunities = chain(activeSelectedCommunities)
			.sortBy((_, index) => index)
			.map((o, index) => ({
				...o,
				displayOrder: index + 1
			}))
			.value()

		const dataWithoutSelected = chain(data)
			.filter(
				o => !find(activeSelectedCommunities, c => c.id === o.id)
			)
			.sortBy((_, index) => sortOrder === 'asc' ? index : -index)
			.map(o => ({
				...o,
				displayOrder: undefined
			}))
			.value()

		const paginatedData = getDataPage(
			dataWithoutSelected,
			{
				...pagination,
				page: pagination.page - 1
			}
		)

		return [
			...orderedActiveSelectedCommunities,
			...first(paginatedData, pagination.size - activeSelectedCommunities.length)
		]
	}, [
		data,
		canEdit,
		sortOrder,
		pagination,
		activeSelectedCommunities
	])

	function add(community) {
		onChange([
			...activeSelectedCommunities,
			{
				...community,
				selected: true,
				displayOrder: activeSelectedCommunities.length + 1
			},
			...filter(deselectedCommunities, o => o.id !== community.id),
			...hiddenSelectedCommunities
		])
	}

	function remove(community) {
		onChange([
			...chain(activeSelectedCommunities)
				.filter(o => o.id !== community.id)
				.map((o, index) => ({ ...o, displayOrder: index + 1 }))
				.value(),
			{
				...community,
				selected: false,
				deselected: true,
				displayOrder: undefined
			},
			...deselectedCommunities,
			...hiddenSelectedCommunities
		])
	}

	function changeDisplayOrder(community, orderShift) {
		const communityToSwap = find(
			activeSelectedCommunities,
			(_, index, source) => source[index - orderShift]?.id === community.id
		)

		onChange([
			...map(
				activeSelectedCommunities,
				o => {
					if (o.id === community.id) return {
						...communityToSwap,
						displayOrder: community.displayOrder
					}

					if (o.id === communityToSwap.id) return {
						...community,
						displayOrder: communityToSwap.displayOrder
					}

					return o
				}
			),
			...deselectedCommunities,
			...hiddenSelectedCommunities
		])
	}

	function moveUp(community) {
		changeDisplayOrder(community, ORDER_SHIFT.UP)
	}

	function moveDown(community) {
		changeDisplayOrder(community, ORDER_SHIFT.DOWN)
	}

	if (!isVisible) return null

	return (
		<Table
			hasHover
			hasPagination={canEdit && paginatedData.length !== 0}
			className="FeaturedCommunities"
			noDataText=""
			title={(
				<>
					Featured Service Providers
					<AlertPanel className="FeaturedCommunities-Alert">
						{paginatedData.length === 0 ? (
							<>
								<p className="FeaturedCommunities-AlertParagraph">Having community vendors in one place
									with an ability to send referrals to your vendors and receive referral responses
									quickly and secure is possible with Simply Connect.</p>
								<p className="FeaturedCommunities-AlertParagraph">Please reach out to Simply Connect
									support team to configure your vendors network.</p>
							</>
						) : (
							<>
								<p className="FeaturedCommunities-AlertParagraph">Please reach out to Simply Connect
									support team to update your vendors network if needed.</p>
								<p className="FeaturedCommunities-AlertParagraph">You can select up to 8 featured
									service providers.</p>
							</>
						)}
					</AlertPanel>
				</>
			)}
			keyField="id"
			isLoading={isFetching}
			data={paginatedData}
			pagination={pagination}
			columns={[{
				dataField: 'communityName',
				text: 'Service Providers',
				sort: true,
				onSort: (_, order) => setSortOrder(order)
			}, {
				dataField: '',
				text: 'Featured',
				headerClasses: 'width-120',
				formatExtraData: {
					selectedCommunities: selectedCommunities.filter(
						o => o.selected && o.confirmVisibility
					)
				},
				formatter: (_, record, rowIndex, {
					selectedCommunities
				}) => {
					const alreadySelected = record.selected

					return record.confirmVisibility ? (
						<CheckboxField
							value={!!alreadySelected}
							className="mb-0 pt-0"
							isDisabled={!canEdit || (!alreadySelected && selectedCommunities.length === 8)}
							onChange={() => {
								const onChange = !alreadySelected ? add : remove

								onChange(record)
							}}
						/>
					) : null
				}
			}, {
				dataField: 'displayOrder',
				text: 'Order',
				headerClasses: 'width-200',
				formatExtraData: {
					selectedCommunities: selectedCommunities.filter(
						o => o.selected && o.confirmVisibility
					)
				},
				formatter: (_, record, rowIndex, {
					selectedCommunities
				}) => {
					return (
						<div className="d-flex align-items-center">
							<TextField
								isDisabled={true}
								value={record.selected && record.confirmVisibility ? record.displayOrder : undefined}
								className="width-80 mb-0"
							/>
							{record.selected && record.confirmVisibility && canEdit && (
								<>
									<IconButton
										Icon={ArrowTop}
										disabled={record.displayOrder === 1}
										className={cn({
											invisible: record.displayOrder === 1
										})}
										onClick={() => record.displayOrder !== 1 && moveUp(record)}
									/>
									<IconButton
										Icon={ArrowTop}
										className={cn('FeaturedCommunities-OrderButton_rotated_180', {
											invisible: record.displayOrder === selectedCommunities.length
										})}
										onClick={() => record.displayOrder !== selectedCommunities.length && moveDown(record)}
									/>
								</>
							)}
						</div>
					)
				}
			}]}
			onRefresh={page => setPage(page)}
		/>
	)
}

export default FeaturedServiceProviderSection