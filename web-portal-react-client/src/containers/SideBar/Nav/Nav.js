import React, { memo } from 'react'
import cn from 'classnames'

import { Link as RRLink } from 'react-router-dom'
import { UncontrolledTooltip as Tooltip } from 'reactstrap'

import { hyphenate } from 'lib/utils/Utils'
import { path } from 'lib/utils/ContextUtils'

function LinkFallback ({ children, ...attributes }) {
    return (
        <span {...attributes}>
            {children}
        </span>
    )
}

function Nav({
    to,
    name,
    title,
    hasIcon,
    hasTitle,
    hintText,
    isActive,
    extraText,
    errorText,
    renderIcon,
    isDisabled
}) {
    const classes = cn('SideBar-Nav', {
        'SideBar-Nav_active': isActive,
        'SideBar-Nav_disabled': isDisabled
    })

    const tooltipId = (
        `${name || hyphenate(title)}-tooltip`
    )

    const canRenderIcon = hasIcon && renderIcon
    const Link = isDisabled ? LinkFallback : RRLink

    return (
        <div className={classes}>
            <Link id={tooltipId} to={path(to)}>
                {canRenderIcon && renderIcon('SideBar-NavIcon')}

                {hasTitle && (
                    <div className='SideBar-NavTitle'>
                        <span>{title}</span>
                        {extraText ? (
                            <>
                                <span className='SideBar-NavSeparator'>|</span>
                                <span className='SideBar-NavExtraText'>{extraText}</span>
                            </>
                        ) : null}
                    </div>
                )}
            </Link>

            {(hasIcon || isDisabled) && (
                <Tooltip
                    placement="right"
                    target={tooltipId}
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
                    {hintText}
                </Tooltip>
            )}
        </div>
    )
}

export default memo(Nav)