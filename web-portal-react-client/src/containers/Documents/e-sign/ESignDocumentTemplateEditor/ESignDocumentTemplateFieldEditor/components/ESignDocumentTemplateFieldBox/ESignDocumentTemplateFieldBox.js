import React, {
    memo,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { Resizable } from 'react-resizable'

import {
    useToggle
} from 'hooks/common'

import {
    Tooltip
} from 'components'

import { noop } from 'lib/utils/FuncUtils'

import { ReactComponent as Remove } from 'images/basket.svg'
import { ReactComponent as Menu } from 'images/menu-v-dots.svg'
import { ReactComponent as Cross } from 'images/cross-2.svg'

import './ESignDocumentTemplateFieldBox.scss'

const COLORS = ['blue', 'yellow', 'green', 'red', 'deep-red']

function ESignDocumentTemplateFieldBox(
    {
        id,
        data,
        color,
        width,
        height,
        canEditProps,

        isResizable,
        resizeHandles,
        minSizeConstraints,
        maxSizeConstraints,

        onRemove,
        onDoubleClick,
        onSetProperties,

        onResize,
        onResizeStart,
        onResizeStop,

        children,
        className
    }
) {
    const [isPopupOpen, togglePopup] = useToggle()

    const _onDoubleClick = useCallback(() => {
        onDoubleClick(data)
    }, [data, onDoubleClick])

    const _onSetProperties = useCallback((closePopup) => () => {
        closePopup()
        onSetProperties(data)
    }, [data, onSetProperties])

    const _onResize = useCallback((e, params) => {
        onResize(e, data, params)
    }, [data, onResize])

    const _onResizeStart = useCallback((e, params) => {
        onResizeStart(e, data, params)
    }, [data, onResizeStart])

    const _onResizeStop = useCallback((e, params) => {
        onResizeStop(e, data, params)
    }, [data, onResizeStop])

    const _onRemove = useCallback((closePopup = noop) => () => {
        closePopup()
        onRemove(data)
    }, [data, onRemove])

    return (
        <Resizable
            width={width}
            height={height}
            handleSize={[500, 300]}
            resizeHandles={resizeHandles}
            minConstraints={minSizeConstraints}
            maxConstraints={maxSizeConstraints}
            {...!isResizable && { axis: 'none', resizeHandles: [] }}
            onResize={_onResize}
            onResizeStop={_onResizeStop}
            onResizeStart={_onResizeStart}
        >
            <div
                style={{ width, height }}
                className={cn(
                    'ESignDocumentTemplateFieldBox',
                    `ESignDocumentTemplateFieldBox_color_${color}`,
                    className
                )}
            >
                <div
                    onDoubleClick={_onDoubleClick}
                    className="ESignDocumentTemplateFieldBox-Body"
                >
                    {children}
                </div>
                <div className="ESignDocumentTemplateFieldBox-Actions">
                    {canEditProps ? (
                        <>
                            <div
                                id={`popup-toggle-${id}`}
                                className="ESignDocumentTemplateFieldBox-ActionPopupToggle"
                            >
                                {isPopupOpen ? (
                                    <Cross className="ESignDocumentTemplateFieldBox-ActionPopupToggleIcon" />
                                ) : (
                                    <Menu className="ESignDocumentTemplateFieldBox-ActionPopupToggleIcon" />
                                )}
                            </div>
                            <Tooltip
                                hideArrow
                                clickOutside
                                trigger="click"
                                onToggle={togglePopup}
                                placement="bottom-start"
                                target={`popup-toggle-${id}`}
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
                                className="ESignDocumentTemplateFieldBox-ActionPopup"
                            >
                                {(closePopup) => (
                                    <>
                                        <div
                                            onClick={_onSetProperties(closePopup)}
                                            className="ESignDocumentTemplateFieldBox-ActionPopupItem"
                                        >
                                            Set Properties
                                        </div>
                                        <div
                                            onClick={_onRemove(closePopup)}
                                            className="ESignDocumentTemplateFieldBox-ActionPopupItem"
                                        >
                                            Delete
                                        </div>
                                    </>
                                )}
                            </Tooltip>
                        </>
                    ) : (
                        <Remove
                            onClick={_onRemove()}
                            className="ESignDocumentTemplateFieldBox-Action ESignDocumentTemplateFieldBox-ActionIcon"
                        />
                    )}
                </div>
            </div>
        </Resizable>
    )
}

ESignDocumentTemplateFieldBox.propTypes = {
    id: PTypes.string.isRequired,
    data: PTypes.object,
    canEditProps: PTypes.bool,
    color: PTypes.oneOf(COLORS),
    className: PTypes.string,

    width: PTypes.number,
    height: PTypes.number,

    isResizable: PTypes.bool,
    resizeHandles: PTypes.arrayOf(PTypes.string),
    minSizeConstraints: PTypes.arrayOf(PTypes.number),
    maxSizeConstraints: PTypes.arrayOf(PTypes.number),

    onRemove: PTypes.func,
    onDoubleClick: PTypes.func,
    onSetProperties: PTypes.func,

    onResize: PTypes.func,
    onResizeStart: PTypes.func,
    onResizeStop: PTypes.func
}

ESignDocumentTemplateFieldBox.defaultProps = {
    data: {},
    width: 160,
    height: 50,
    color: 'blue',
    canEditProps: false,
    isResizable: true,
    minSizeConstraints: [100, 50],
    resizeHandles: ['s', 'w', 'e', 'n', 'sw', 'nw', 'se', 'ne'],
    onRemove: noop,
    onSetProperties: noop,
    onDoubleClick: noop,
    onResizeStart: noop,
    onResizeStop: noop,
    onResize: noop
}

export default memo(ESignDocumentTemplateFieldBox)



