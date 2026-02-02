import React from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import { map } from 'lib/utils/ArrayUtils'

import { ReactComponent as Details } from 'images/details.svg'
import { ReactComponent as Inquiry } from 'images/referral.svg'

import './ProviderTile.scss'

function Button({ icon, onClick, visible = true, children }) {
	const Icon = icon

	return (
		<div
			onClick={onClick}
			className={cn(
				'ProviderTile-Button',
				{ invisible: !visible }
			)}
		>
			<div className="ProviderTile-ButtonIcon">
				<Icon/>
			</div>
			<div className="ProviderTile-ButtonLabel">
				{children}
			</div>
		</div>
	)
}

function ProviderTile(
	{
		data,
		renderPicture,
		className,
		onDetails,
		onInquiry,
	}
) {
	const {
		name,
		state,
		zipCode,
		serviceCategories,
		shouldReceiveNonNetworkReferrals
	} = data

	return (
		<div className={cn('ProviderTile', className)}>
			<div className="ProviderTile-Body">
				<div className="ProviderTile-Name">
					{name}
				</div>
				<div className="ProviderTile-ServiceCategories">
					{map(serviceCategories, o => o.title).join(', ')}
				</div>
				<div className="ProviderTile-ZipCode">
					{state} {zipCode}
				</div>
				<div className="ProviderTile-PictureContainer">
					{renderPicture()}
				</div>
			</div>
			<div className="ProviderTile-Footer">
				<Button
					icon={Details}
					onClick={onDetails}
				>
					Details
				</Button>
				{shouldReceiveNonNetworkReferrals && (
					<Button
						icon={Inquiry}
						onClick={onInquiry}
					>
						Inquiry
					</Button>
				)}
			</div>
		</div>
	)
}

ProviderTile.propTypes = {
	data: PTypes.object,
	renderPicture: PTypes.func,
	onDetails: PTypes.func,
	onInquiry: PTypes.func
}

export default ProviderTile
