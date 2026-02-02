import React, {
	memo,
	useCallback
} from 'react'

import {
	Loader,
	Picture,
	Carousel
} from 'components'

import {
	useSubDomain
} from 'hooks/common'

import {
	useProvidersQuery
} from 'hooks/business/Marketplace/Public'

import {
	map,
	first
} from 'lib/utils/ArrayUtils'

import {
	PAGINATION
} from 'lib/Constants'

import { ReactComponent as StubIcon } from 'images/image.svg'

import ProviderTile from './ProviderTile/ProviderTile'

import './FeaturedProviders.scss'

const { MAX_SIZE } = PAGINATION

const Tiles = memo(function Tiles(
	{
		data,
		onView,
		onInquiry
	}
) {
	return (
		<Carousel
			className="FeaturedProviders-Tiles"
			containerClassName="FeaturedProviders-TilesContainer"
		>
			{map(data, o => {
				const picture = first(o.pictures)

				return (
					<Carousel.Slide
						key={o.id}
						className="FeaturedProviders-Tile"
					>
						<ProviderTile
							data={o}
							className="flex-1"
							renderPicture={() => (
								picture ? (
									<Picture
										path={`/open-marketplace/service-providers/${o.id}/pictures/${picture.id}`}
										renderFallback={() => (
											<Picture
												path={`/open-marketplace/service-providers/${o.id}/logo`}
												renderFallback={() => (
													<StubIcon className="ProviderTile-StubIcon"/>
												)}
												className="ProviderTile-Picture"
											/>
										)}
										className="ProviderTile-Picture"
									/>
								) : (
									<Picture
										path={`/open-marketplace/service-providers/${o.id}/logo`}
										renderFallback={() => (
											<StubIcon className="ProviderTile-StubIcon"/>
										)}
										className="ProviderTile-Picture"
									/>
								)
							)}
							onDetails={() => onView(o)}
							onInquiry={() => onInquiry({ ...o, isFeatured: true })}
						/>
					</Carousel.Slide>
				)
			})}
		</Carousel>
	)
})

function FeaturedProviders({ onView, onInquiry }) {
	let organizationCode = useSubDomain()

	const {
		isFetching,
		data: featuredProviders = []
	} = useProvidersQuery({
		size: MAX_SIZE,
		isFeatured: true,
		organizationCode
	}, { staleTime: 0 })

	const hasData = featuredProviders?.length !== 0

	const _onView = useCallback(
		data => onView(data), [onView]
	)

	return (
		<div className="FeaturedProviders">
			<div className="FeaturedProviders-Title">
				Featured Service Providers
			</div>

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
					data={featuredProviders}
					onView={_onView}
					onInquiry={onInquiry}
				/>
			)}
		</div>
	)
}

export default memo(FeaturedProviders)
