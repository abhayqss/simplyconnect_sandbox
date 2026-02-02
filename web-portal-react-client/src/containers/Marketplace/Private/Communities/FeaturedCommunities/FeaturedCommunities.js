import React, {
	memo,
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import {
	useHistory
} from 'react-router-dom'

import {
	chain,
	findWhere
} from 'underscore'

import {
	Loader,
	Picture,
	PrimaryFilter
} from 'components'

import CommunityEditor from 'containers/Admin/Organizations/Communities/CommunityEditor/CommunityEditor'

import {
	useQueryParams
} from 'hooks/common'

import {
	usePrimaryFilter
} from 'hooks/common/filter'

import {
	useCanEditPartnerProviders,
	useFeaturedCommunitiesQuery,
	useCommunityPrimaryFilterDirectory
} from 'hooks/business/Marketplace'

import {
	hyphenate
} from 'lib/utils/Utils'

import {
	path
} from 'lib/utils/ContextUtils'

import {
	map
} from 'lib/utils/ArrayUtils'

import { STEP } from 'containers/Admin/Organizations/Communities/CommunityForm2/Constants'

import { ReactComponent as Settings } from 'images/settings.svg'

import CommunityTile from './CommunityTile/CommunityTile'

import './FeaturedCommunities.scss'

const MAX_COMMUNITY_COUNT = 8

const Tiles = memo(function Tiles({
	communities,

	onDetails,
	onReferral
}) {
	return (
		<div className="FeaturedCommunities-Tiles">
			{map(communities, o => (
				<div
					key={o.communityId}
					className="FeaturedCommunities-Tile"
				>
					<CommunityTile
						title={o.communityName}
						websiteUrl={o.websiteUrl}
						hasDetailsButton={!!o.communityId}
						hasReferralButton={o.canAddReferral && o.allowExternalInboundReferrals}
						description={map(o.serviceCategories, o => o.label ?? o.title).join(', ')}
						renderLogo={() => (
							<>
								<Picture
									path={`/organizations/${o.organizationId}/communities/${o.communityId}/logo`}
									className="CommunityTile-LogoPicture"
								/>
								<Picture
									path={`/organizations/${o.organizationId}/logo`}
									className="CommunityTile-LogoPicture"
								/>
							</>
						)}
						onDetails={() => onDetails(o)}
						onReferral={() => onReferral({ ...o, isFeatured: true })}
					/>
				</div>
			))}
		</div>
	)
})

function FeaturedCommunities({ onReferral }) {
	const history = useHistory()

	const [isEditorOpen, setEditorOpen] = useState(false);

	const primaryFilter = usePrimaryFilter(
		'FEATURED_COMMUNITY_PRIMARY_FILTER',
		{
			isCommunityMultiSelection: false
		}
	)

	const {
		providerId,
		communityId: cId,
		organizationId: orgId,
		shouldCreateReferral
	} = useQueryParams()

	const { 
		communityId,
		organizationId		
	} = primaryFilter.data

	const {
		communities,
		organizations
	} = useCommunityPrimaryFilterDirectory(
		{ organizationId, communityId },
		{ actions: primaryFilter.changeCommunityField }
	)

	const {
		isFetching,
		data: { data: featuredCommunities } = {},
		refetch
	} = useFeaturedCommunitiesQuery({
		communityId,
		size: MAX_COMMUNITY_COUNT,
		isFeatured: true
	}, {
		enabled: !!communityId,
		staleTime: 0
	})

	const filteredFeaturedCommunities = useMemo(() => {
		return chain(featuredCommunities)
			.filter(o => !!o.displayOrder)
			.sortBy(o => o.displayOrder)
			.value()
	}, [featuredCommunities])	

	useEffect(() => {
		if (shouldCreateReferral) {
			primaryFilter.changeFields({
				organizationId: orgId,
				communityId: cId
			})

			const community = findWhere(filteredFeaturedCommunities, { communityId: providerId })

			if (community && community.allowExternalInboundReferrals) {
				history.replace()

				onReferral({ ...community, isFeatured: true })
			}
		}
	}, [
		cId,
		orgId,
		providerId,
		filteredFeaturedCommunities,
		shouldCreateReferral
	])

	const canEditPartnerProviderQuery = useCanEditPartnerProviders({ 
		communityId,
		organizationId
	}, {
		enabled: !!communityId
	})

	const canEditPartnerProviders = canEditPartnerProviderQuery.data?.data
	const hasData = filteredFeaturedCommunities?.length !== 0

	const onDetails = useCallback(o => {
		history.push(
			path(`marketplace/communities/${hyphenate(o.communityName)}--@id=${o.communityId}`)
		)
	}, [history])

	const onSaveSuccess = () => {
		refetch()
		setEditorOpen(false)
	}

	return (
		<div className="FeaturedCommunities">
			<div className="d-flex flex-row flex-wrap justify-content-between align-items-center">
				<div className="FeaturedCommunities-Title">
					Featured Service Providers
				</div>
				{canEditPartnerProviders && (
					<Settings 
						className="cursor-pointer width-32 height-32"
						onClick={() => canEditPartnerProviders && setEditorOpen(true)}
					/>
				)}
			</div>
			<PrimaryFilter
				communities={communities}
				organizations={organizations}
				{...primaryFilter}
				onChangeCommunityField={(communityId) =>
					primaryFilter.changeCommunityField(communityId)
				}
				onChangeOrganizationField={(organizationId) =>
					primaryFilter.changeOrganizationField(organizationId)
				}
				isCommunityMultiSelection={false}
				className="margin-bottom-15"
			/>

			{isFetching && (
				<Loader className="margin-bottom-40"/>
			)}

			{!isFetching && !hasData && (
				<div className="font-size-15 text-center margin-bottom-30">
					No Featured Service Providers
				</div>
			)}

			{!isFetching && hasData && (
				<Tiles
					communities={filteredFeaturedCommunities}
					primaryFilter={primaryFilter}
					onDetails={onDetails}
					onReferral={onReferral}
				/>
			)}

			{isEditorOpen && !!communityId && (
				<CommunityEditor
					isOpen={isEditorOpen}
					communityId={communityId}
					organizationId={organizationId}
					defaultActiveTab={STEP.MARKETPLACE}
					onClose={() => setEditorOpen(false)}
					onSaveSuccess={onSaveSuccess}					
				/>
			)}
		</div>
	)
}

export default memo(FeaturedCommunities)
