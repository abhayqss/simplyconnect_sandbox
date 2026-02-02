import React, {
	useCallback
} from 'react'

import cn from 'classnames'
import { map } from 'underscore'

import PTypes from 'prop-types'

import Highlighter from 'react-highlight-words'

import {
	Picture
} from 'components'

import {
	isNotEmpty
} from 'lib/utils/Utils'

import {
	noop
} from 'lib/utils/FuncUtils'

import { CommunityOverallRating } from '../index'

import './CommunitySummary.scss'

function CommunitySummary(
	{
		data,
		highlightedText,
		onMoreInfo,
		onNavigation,
		className
	}
) {
	const {
		communityId,
		communityName,
		organizationId,
		organizationName,
		serviceCategories,
		address,
		location
	} = data

	const _onMoreInfo = useCallback(() => {
		onMoreInfo(data)
	}, [data, onMoreInfo])

	const _onNavigation = useCallback(() => {
		onNavigation(data)
	}, [data, onNavigation])

	return (
		<div className={cn('MarketplaceCommunitySummary', className)}>
			<div className="MarketplaceCommunitySummary-Logo" onClick={_onMoreInfo}>
				<Picture
					className="MarketplaceCommunitySummary-LogoPicture"
					path={`/organizations/${organizationId}/communities/${communityId}/logo`}
				/>
				<Picture
					className="MarketplaceCommunitySummary-LogoPicture"
					path={`/organizations/${organizationId}/logo`}
				/>
			</div>
			<div className="MarketplaceCommunitySummary-Info">
				<div className="margin-bottom-12">
					<div className="MarketplaceCommunitySummary-Title" onClick={_onMoreInfo}>
						<Highlighter
							highlightClassName="MarketplaceCommunitySummary-Highlight"
							searchWords={[highlightedText]}
							textToHighlight={`${organizationName}, ${communityName}`}
						/>
					</div>
					{isNotEmpty(serviceCategories) && (
						<div className="MarketplaceCommunitySummary-PrimaryFocuses">
							<Highlighter
								highlightClassName="MarketplaceCommunitySummary-Highlight"
								searchWords={[highlightedText]}
								textToHighlight={map(serviceCategories, o => o.label).join(', ')}
							/>
						</div>
					)}
					<div className="h-flexbox justify-content-between">
						<div className="MarketplaceCommunitySummary-Address">
							<Highlighter
								highlightClassName="MarketplaceCommunitySummary-Highlight"
								searchWords={[highlightedText]}
								textToHighlight={address}
							/>
						</div>

						{location.distanceInMiles && (
							<div className="MarketplaceCommunitySummary-Distance">
								{location.distanceInMiles} mi
							</div>
                        )}
                        {/*<Directions className="MarketplaceCommunitySummary-Navigate" onClick={_onNavigation}/>*/}
                    </div>
				</div>
				<CommunityOverallRating
					rating={data.rating}
					isSaved={data.isSaved}
					communityId={communityId}
					onInfo={_onMoreInfo}
				/>
			</div>
		</div>
	)
}

CommunitySummary.propTypes = {
	className: PTypes.string,

	data: PTypes.object,
	highlightedText: PTypes.string,

	onMoreInfo: PTypes.func,
	onNavigation: PTypes.func
}

CommunitySummary.defaultProps = {
	onMoreInfo: noop,
	onNavigation: noop
}

export default CommunitySummary