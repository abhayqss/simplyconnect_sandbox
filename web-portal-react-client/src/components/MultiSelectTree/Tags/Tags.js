import React from 'react'

import cn from 'classnames'

import { ReactComponent as CrossIcon } from 'images/delete.svg'

import './Tags.scss'

function Tags({ name, items, onRemove, isDisabled, ...restParams }) {
	return (
		<div className={cn('Tags', { 'Tags_disabled': isDisabled })} {...restParams}>
			{items.map((o, i) => {
				const id = o?.value ?? i
				const text = o?.text ?? o

				return (
					<Tag
						key={id}
						testId={`${name}_field-tag-${o.value}`}
						isDisabled={isDisabled}
						onRemove={() => onRemove(id)}
					>
						{text}
					</Tag>
				)
			})}
		</div>
	)
}

function Tag({ testId, children, onRemove }) {
	return (
		<div data-testid={testId} className="Tag">
            <span className="Tag-Name">
                {children}
            </span>
			<CrossIcon
				onClick={onRemove}
				className="Tag-Icon Tag-Icon__cross"
			/>
		</div>
	)
}

export default Tags
