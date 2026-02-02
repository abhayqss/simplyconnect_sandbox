import React, {
    Component,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import {
    noop,
    isNumber,
    isBoolean
} from 'underscore'

import { Collapse } from 'reactstrap'

import { isInteger } from 'lib/utils/Utils'

import { ReactComponent as Plus } from 'images/plus.svg'
import { ReactComponent as Minus } from 'images/minus.svg'

import './CollapsibleSection.scss'

export default function CollapsibleSection(
    {
        name,
        title,
        toggledBy = 'header',
        className,
        titleClassName,
        headerClassName,
        bodyClassName,
        children,

        isOpen: isOpenProp,
        isOpenByDefault = false,

        renderHeader,
        renderHeaderIcon,

        onToggle: onToggleCb
    }
) {
    const [isOpenState, toggle] = useState(isOpenByDefault)

    const isOpen = isBoolean(isOpenProp) ? isOpenProp : isOpenState

    const onToggle = useCallback(() => {
        if (onToggleCb) onToggleCb(name, isOpenProp)
        else toggle(!isOpenState)
    }, [name, isOpenProp, isOpenState, onToggleCb])

    return (
        <div className={cn('CollapsibleSection', className)}>
            <div
                onClick={toggledBy === 'header' ? onToggle : noop}
                className={cn('CollapsibleSection-Header', headerClassName)}
            >
                {renderHeader ? renderHeader(isOpen, onToggle) : (
                    <>
                        <div className={cn('CollapsibleSection-Title', titleClassName)}>
                            {title}
                        </div>
                        <div
                            className="CollapsibleSection-IconWrapper"
                            onClick={toggledBy === 'icon' ? onToggle : noop}
                        >
                            {renderHeaderIcon ? renderHeaderIcon({ isOpen, className: 'CollapsibleSection-Icon' }) : (
                                isOpen ? (
                                    <Minus className="CollapsibleSection-Icon CollapsibleSection-MinusIcon"/>
                                ) : (
                                    <Plus className="CollapsibleSection-Icon"/>
                                )
                            )}
                        </div>
                    </>
                )}
            </div>
            <Collapse
                isOpen={isOpen}
                className={cn(
                    'CollapsibleSection-Body',
                    bodyClassName
                )}
            >
                {children}
            </Collapse>
        </div>
    )
}

CollapsibleSection.propTypes = {
    name: PTypes.string,
    title: PTypes.string,
    toggledBy: PTypes.oneOf(['header', 'icon']),
    className: PTypes.string,
    headerClassName: PTypes.string,
    bodyClassName: PTypes.string,

    isOpen: PTypes.bool,
    isOpenByDefault: PTypes.bool,

    renderHeader: PTypes.func,
    renderHeaderIcon: PTypes.func,

    onToggle: PTypes.func
}