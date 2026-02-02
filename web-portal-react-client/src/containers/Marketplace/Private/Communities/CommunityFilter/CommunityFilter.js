import React, {
	memo,
	useRef,
	useMemo,
	useCallback
} from 'react'

import cn from 'classnames'

import {
	without
} from 'underscore'

import {
	Col, Row
} from 'reactstrap'

import {
	Loader
} from 'components'

import {
	CheckboxField
} from 'components/Form'

import {
	Button,
	ToggleButtonGroup
} from 'components/buttons'

import {
	useCommunityFilterDirectory,
	useCommunityFilterInitialization,
	useCommunityFilterDefaultDataCache
} from 'hooks/business/Marketplace'

import {
	isNotEmpty
} from 'lib/utils/Utils'

import {
	map,
	isEqLength
} from 'lib/utils/ArrayUtils'

import {
	SERVICE_CATEGORY_ICONS
} from '../../../lib/Constants'

import './CommunityFilter.scss'

export const NAME = 'COMMUNITY_FILTER'

function CommunityFilter(
	{
		data,
		reset,
		apply,
		isSaved,
		communityId,
		areInNetworkCommunities,

		changeField,
		changeFields,

		className
	}
) {
	const filterRef = useRef()

	const { serviceCategoryId } = data

	const {
		services,
		serviceCategories,
		isFetchingServices,
		isFetchingServiceCategories
	} = useCommunityFilterDirectory({
		categoryId: data.serviceCategoryId
	})

	const serviceCategoryButtons = useMemo(
		() => map(serviceCategories, o => ({
			data: o,
			id: o.id,
			size: 20,
			text: o.title,
			shouldHighLight: false,
			isChecked: o.id === data.serviceCategoryId,
			Icon: o.name && SERVICE_CATEGORY_ICONS[o.name],
			className: 'ServiceCategoryToggleButton'
		})),
		[serviceCategories, data.serviceCategoryId]
	)

	const {
		update: updateDefaultData
	} = useCommunityFilterDefaultDataCache()

	useCommunityFilterInitialization({
		isSaved,
		changeFields,
		updateDefaultData
	})

	const onChangeCategory = useCallback((e, { data: o }) => {
		changeField('serviceIds', [])
		changeField('serviceCategoryId', o.id !== serviceCategoryId ? o.id : null)
	}, [changeField, serviceCategoryId])

	const onChangeServices = useCallback(name => {
		let serviceIds

		if (name === 'ALL') {
			serviceIds = (
				!isEqLength(services, data.serviceIds)
					? map(services, o => o.id) : []
			)
		} else {
			const id = parseInt(name.split('-')[1])

			serviceIds = (
				data.serviceIds.includes(id)
					? without(data.serviceIds, id)
					: [...data.serviceIds, id]
			)
		}

		changeField('serviceIds', serviceIds)
	}, [
		services,
		changeField,
		data.serviceIds
	])

	return (
		<div ref={filterRef} className={cn('CommunityFilter', className)}>
			{!areInNetworkCommunities && (
				<div className="CommunityFilter-Section">
					<Row>
						<Col md="auto">
							<CheckboxField
								name="includeMyCommunities"
								className="CommunityFilter-CheckboxField"
								value={data.includeMyCommunities}
								label="My Vendors"
								onChange={changeField}
							/>
						</Col>
						<Col md="auto">
							<CheckboxField
								name="includeInNetworkCommunities"
								className="CommunityFilter-CheckboxField"
								value={data.includeInNetworkCommunities}
								label="In-network Partners"
								onChange={changeField}
							/>
						</Col>
					</Row>
				</div>
			)}

			<div className="CommunityFilter-Section margin-bottom-7">
				{isFetchingServiceCategories && (
					<Loader isCentered/>
				)}

				<ToggleButtonGroup
					onClick={onChangeCategory}
					buttons={serviceCategoryButtons}
					className="ServiceCategoryToggleButtonGroup"
				/>
			</div>

			{isFetchingServices && (
				<div className="position-relative">
					<Loader isCentered/>
				</div>
			)}

			{isNotEmpty(services) && (
				<div className="CommunityFilter-Section">
					<div className="CommunityFilter-SectionTitle margin-bottom-4">
						Services
					</div>
					<div className="CommunityFilter-Services">
						<div className="CommunityFilter-Service">
							<CheckboxField
								name="ALL"
								className="CommunityFilter-CheckboxField"
								value={isEqLength(services, data.serviceIds)}
								label="All"
								onChange={onChangeServices}
							/>
						</div>
						{map(services, o => (
							<div className="CommunityFilter-Service">
								<CheckboxField
									key={o.id}
									name={`service-${o.id}`}
									className="CommunityFilter-CheckboxField"
									value={data.serviceIds.includes(o.id)}
									label={o.label ?? o.title}
									onChange={onChangeServices}
								/>
							</div>
						))}
					</div>
				</div>
			)}

			<div className="text-right">
				<Button
					outline
					color="success"
					className="margin-right-30"
					onClick={() => reset()}
				>
					Clear
				</Button>
				<Button
					color="success"
					onClick={apply}
					disabled={isFetchingServices || isFetchingServiceCategories}
				>
					Apply
				</Button>
			</div>
		</div>
	)
}

export default memo(CommunityFilter)