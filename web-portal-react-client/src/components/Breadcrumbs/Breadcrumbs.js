import React, {
    memo,
    Fragment
} from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import { Link } from 'react-router-dom'

import { Button } from 'reactstrap'

import { camel } from 'lib/utils/Utils'
import { path } from 'lib/utils/ContextUtils'

import './Breadcrumbs.scss'

import {ReactComponent as Separator} from 'images/lever-right.svg'

function Breadcrumbs({ items, renderSeparatorIcon, className }) {
    return (
        <div className={cn('Breadcrumbs', className)}>
            {
                items.map((o, i) => {
                    return (
                        <Fragment key={camel(o.title)}>
                            <div
                                className={cn(
                                    'Breadcrumbs-Item',
                                    { 'Breadcrumbs-Item_root': i === 0 && !o.isEnabled },
                                    { 'Breadcrumbs-Item_frozen': o.isFrozen },
                                    { 'Breadcrumbs-Item_active': o.isActive }
                                )}>
                                <Link
                                    to={path(o.href)}
                                    onClick={o.onClick}
                                    className='Breadcrumbs-ItemTitle'>
                                    {o.title}
                                </Link>
                            </div>
                            {i < (items.length - 1) ? (
                                renderSeparatorIcon ? renderSeparatorIcon() : (
                                    <Separator className='Breadcrumbs-Separator'/>
                                )
                            ) : null}
                        </Fragment>
                    )
                })
            }
        </div>
    )
}

Breadcrumbs.propTypes = {
    items: PTypes.arrayOf(PTypes.shape({
        title: PTypes.string,
        href: PTypes.string,
        onClick: PTypes.func,
        isButton: PTypes.bool
    })),
    renderSeparatorIcon: PTypes.func,
    className: PTypes.string
}

Breadcrumbs.defaultProps = {
    items: []
}

export default memo(Breadcrumbs)
