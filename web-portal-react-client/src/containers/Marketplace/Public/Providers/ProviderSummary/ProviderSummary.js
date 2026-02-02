import React, {
	memo,
	useCallback
} from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import {
	Picture
} from 'components'

import {
	Button
} from 'components/buttons'

import {
	map,
	first
} from 'lib/utils/ArrayUtils'

import { ReactComponent as StubIcon } from 'images/image.svg'

import './ProviderSummary.scss'

function ProviderSummary({ data, onView, onInquiry, className }) {
	const {
		id,
		name,
		state,
		zipCode,
		pictures,
		serviceCategories,
		servicesSummaryDescription,
		shouldReceiveNonNetworkReferrals
	} = data

	const picture = first(pictures)

	const _onView = useCallback(
		() => onView(data), [data, onView]
	)

	const _onInquiry = useCallback(
		() => onInquiry(data), [data, onInquiry]
	)

	return (
		<div className={cn("MarketplaceProviderSummary", className)}>
			<div className="MarketplaceProviderSummary-PictureContainer">
				{picture ? (
					<Picture
						mimeType={picture.mimeType}
						renderFallback={() => (
							<Picture
								mimeType={picture.mimeType}
								renderFallback={() => (
									<StubIcon className="MarketplaceProviderSummary-StubIcon"/>
								)}
								path={`/open-marketplace/service-providers/${id}/logo`}
								className="MarketplaceProviderSummary-Logo"
							/>
						)}
						path={`/open-marketplace/service-providers/${id}/pictures/${picture.id}`}
						className="MarketplaceProviderSummary-Picture"
					/>
				) : (
					<Picture
						renderFallback={() => (
							<StubIcon className="MarketplaceProviderSummary-StubIcon"/>
						)}
						path={`/open-marketplace/service-providers/${id}/logo`}
						className="MarketplaceProviderSummary-Logo"
					/>
				)}
			</div>

			<div className="flex-1">
				<div onClick={_onView} className="MarketplaceProviderSummary-Name">
					{name}
				</div>
				<div className="MarketplaceProviderSummary-ServiceCategories">
					{map(serviceCategories, o => o.title).join(', ')}
				</div>
				<div className="MarketplaceProviderSummary-Description">
					{servicesSummaryDescription}
				</div>
				<div className="MarketplaceProviderSummary-ZipCode">
					{state} {zipCode}
				</div>
			</div>

			{shouldReceiveNonNetworkReferrals && (
				<div className="text-center">
					<Button
						color="success"
						onClick={_onInquiry}
					>
						Send Inquiry
					</Button>
				</div>
			)}
		</div>
	)
}

ProviderSummary.propTypes = {
	data: PTypes.object,
	onView: PTypes.func,
	onInquiry: PTypes.func,
	className: PTypes.string
}

export default memo(ProviderSummary)