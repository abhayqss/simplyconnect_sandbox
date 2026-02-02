import React, {
	memo
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { ReactComponent as Indicator } from 'images/dot.svg'

import './DocumentCategory.scss'

function DocumentCategory({ id, color, name, className }) {
	return (
		<div
			key={id}
			className={cn("DocumentCategory", className)}
			style={{ borderColor: color || '#000000' }}
		>
			<Indicator
				style={{ fill: color || '#000000' }}
				className="DocumentCategory-Indicator"
			/>
			<div className="DocumentCategory-Name">
				{name}
			</div>
		</div>
	)
}

DocumentCategory.propTypes = {
	id: PTypes.oneOfType([PTypes.number, PTypes.string]),
	name: PTypes.string,
	color: PTypes.string,
	className: PTypes.string
}

DocumentCategory.defaultProps = {}

export default memo(DocumentCategory)