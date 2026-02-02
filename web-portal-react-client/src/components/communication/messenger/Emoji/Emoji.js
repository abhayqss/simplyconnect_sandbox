import React, {
	memo,
	useState
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
	getRandomInt
} from 'lib/utils/Utils'

import { map } from 'lib/utils/ArrayUtils'
import { noop, isFunction } from 'lib/utils/FuncUtils'

import { EMOJI } from './lib/Constants'

import './Emoji.scss'

const EmojiList = memo(
	function EmojiList({ each, onClick }) {
		const [uuid] = useState(getRandomInt(0, 9999999))

		return (
			<ul className="Emoji-List">
				{map(EMOJI, o => {
					const { renderIcon, ...data } = o

					let options = each && each(o)
					if (options === false) return

					const {
						tooltip,
						isDisabled,
						description
					} = options ?? {}

					function _onClick() {
						if (!isDisabled) onClick(data)
					}

					return (
						<li
							key={data.id}
							id={`${data.name}-${uuid}`}
							onClick={_onClick}
							className={cn(
								'Emoji-ListItem',
								{ 'Emoji-ListItem_disabled': isDisabled }
							)}
						>
							{renderIcon({ className: 'Emoji-Icon' })}
							{description && (
								<div className="Emoji-Description margin-left-4">
									{description}
								</div>
							)}
							{tooltip && (
								<Tooltip
									{...tooltip}
									render={undefined}
									target={`${data.name}-${uuid}`}
									modifiers={[
										{
											name: 'offset',
											options: { offset: [0, 6] }
										},
										{
											name: 'preventOverflow',
											options: { boundary: document.body }
										}
									]}
								>
									{tooltip.render ? tooltip.render(data) : tooltip.text}
								</Tooltip>
							)}
						</li>
					)
				})}
			</ul>
		)
	}
)

function Emoji({ each, onSelect, className }) {
	return (
		<div className={cn('Emoji', className)}>
			<EmojiList
				each={each}
				onClick={onSelect}
			/>
		</div>
	)
}

Emoji.propTypes = {
	each: PTypes.func,
	onSelect: PTypes.func,
	className: PTypes.string,
}

Emoji.defaultProps = {
	onSelect: noop,
	renderDescription: noop
}

export default memo(Emoji)