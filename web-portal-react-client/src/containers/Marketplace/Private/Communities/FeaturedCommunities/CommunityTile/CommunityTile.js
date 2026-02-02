import React from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import { getAbsoluteUrl } from 'lib/utils/UrlUtils'

import { ReactComponent as Details } from 'images/details.svg'
import { ReactComponent as Website } from 'images/website.svg'
import { ReactComponent as Referral } from 'images/referral.svg'

import './CommunityTile.scss'

function Button({ icon, onClick, visible = true, children }) {
	const Icon = icon

	return (
		<div
			onClick={onClick}
			className={cn(
				'CommunityTile-Button',
				{ invisible: !visible }
			)}
		>
			<div className="CommunityTile-ButtonIcon">
				<Icon/>
			</div>
			<div className="CommunityTile-ButtonLabel">
				{children}
			</div>
		</div>
	)
}

function CommunityTile(
	{
		title,
		description,
		websiteUrl,
		renderLogo,
		hasReferralButton,
		onDetails,
		onReferral,
	}
) {
	const onWebsite = () => websiteUrl && window.open(getAbsoluteUrl(websiteUrl))

	return (
		<div className="CommunityTile">
			<div className="CommunityTile-Body">
				<div className="CommunityTile-Logo">
					{renderLogo()}
				</div>
				<div
					className="CommunityTile-Label"
					title={title}
				>
					{title}
				</div>
				<div
					className="CommunityTile-Types line-clamp-3"
					title={description}
				>
					{description}
				</div>
			</div>
			<div className="CommunityTile-Footer">
				<Button
					icon={Details}
					onClick={onDetails}
				>
					Details
				</Button>
				{hasReferralButton && (
					<Button
						icon={Referral}
						onClick={onReferral}
					>
						Referral
					</Button>
				)}
				{Boolean(websiteUrl) && (
					<Button
						icon={Website}
						onClick={onWebsite}
					>
						Website
					</Button>
				)}
			</div>
		</div>
	)
}

CommunityTile.propTypes = {
	title: PTypes.string,
	renderLogo: PTypes.func,
	description: PTypes.string,
	websiteUrl: PTypes.string,
	hasReferralButton: PTypes.bool,
	onReferral: PTypes.func,
}

export default CommunityTile
