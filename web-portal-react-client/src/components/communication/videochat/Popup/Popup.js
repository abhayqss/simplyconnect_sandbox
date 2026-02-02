import React, {
    useRef,
    useMemo,
    useState,
    useEffect,
    forwardRef,
    useCallback,
    useImperativeHandle
} from 'react'

import $ from 'jquery'
import cn from 'classnames'
import PTypes from 'prop-types'
import { noop } from 'underscore'

import Draggable from 'react-draggable'
import { ResizableBox } from 'react-resizable'

import { ReactComponent as Maximize } from 'images/maximize.svg'
import { ReactComponent as Minimize } from 'images/minimize.svg'
import { ReactComponent as Collapse } from 'images/collapse.svg'

import { getRandomInt } from 'lib/utils/Utils'

import ControlButton from '../ControlButton/ControlButton'

import './Popup.scss'

const SIZES = ['small', 'medium', 'big', 'auto']

const sizes = {
    small: { width: 420, height: 240 },
    medium: { width: 540, height: 480 },
    big: { width: 720, height: 540 },
    auto: { width: 'auto', height: 'auto' }
}

const Popup = forwardRef((
    {
        handle,
        className,

        isDraggable,
        isResizable,

        size,
        width,
        height,
        offset = {},
        dragBounds,
        offsetParent,
        defaultPosition,
        minSizeConstraints,
        maxSizeConstraints,
        isMaximizedByDefault,

        hasMaximizeButton,
        hasMinimizeButton,

        children,

        onDoubleClickHeader,
        onClickMinimizeBtn,
        onDoubleClickBody
    },
    outerRef
) => {
    const ref = useRef()

    const [position, setPosition] = useState(null)
    const [isMaximized, setMaximized] = useState(isMaximizedByDefault ?? false)
    const [offsetParentNode, setParentNode] = useState(false)

    const w = width ? width : sizes[size]?.width
    const h = height ? height : sizes[size]?.height

    const uid = useMemo(() => getRandomInt(0, 1000000), [])

    const toggleMaximized = useCallback(() => {
        setMaximized(!isMaximized)
    }, [isMaximized])

    const onDrag = useCallback((e, position) => {
        setPosition(position)
    }, [])

    const onClickMinimizeButton = useCallback(() => {
        setMaximized(false)
        onClickMinimizeBtn()
    }, [onClickMinimizeBtn])

    useImperativeHandle(outerRef, () => ({
        toggleMaximized
    }))

    let content

    content = (
        <div
            ref={ref}
            className={cn('VideoChatPopup', className)}
        >
            {hasMaximizeButton && (
                <ControlButton
                    size={28}
                    Icon={isMaximized ? Collapse : Maximize}
                    color={null}
                    name={`VideoChatPopup${uid}-MaximizeBtn`}
                    tipText={isMaximized ? 'Collapse' : 'Expand'}
                    onClick={toggleMaximized}
                    className="VideoChatPopup-MaximizeButton"
                />
            )}
            {hasMinimizeButton && (
                <ControlButton
                    size={18}
                    Icon={Minimize}
                    color={null}
                    name={`VideoChatPopup${uid}-MinimizeBtn`}
                    tipText="Collapse"
                    onClick={onClickMinimizeButton}
                    className="VideoChatPopup-MinimizeButton"
                />
            )}
            <div
                className="VideoChatPopup-Header"
                onDoubleClick={onDoubleClickHeader}
            />
            <div
                className="VideoChatPopup-Body"
                onDoubleClick={onDoubleClickBody}
            >
                {children}
            </div>
        </div>
    )

    content = (
        <ResizableBox
            {...!isResizable ? { axis: 'none' } : null}
            {...isMaximized ? { axis: 'none' } : { width: w, height: h }}
            handleSize={[200, 200]}
            resizeHandles={isResizable ? ['s', 'e'] : []}
            minConstraints={minSizeConstraints}
            maxConstraints={maxSizeConstraints}
            className={cn(
                'VideoChatPopupContainer',
                { 'VideoChatPopupContainer_invisible': !position },
                { 'VideoChatPopupContainer_maximized': isMaximized },
                { [`VideoChatPopupContainer_size_${size}`]: !isResizable && SIZES.includes(size) }
            )}
        >
            {content}
        </ResizableBox>
    )

    content = (
        <Draggable
            nodeRef={ref}
            bounds={dragBounds}
            position={position}
            offsetParent={offsetParentNode}
            disabled={!isDraggable || isMaximized}
            handle={handle}
            onDrag={onDrag}
        >
            {content}
        </Draggable>
    )

    useEffect(() => {
        const containerNode = (
            ref.current?.parentNode
        )

        if (w === 'auto') {
            containerNode.style.width = 'auto'
        }

        if (h === 'auto') {
            containerNode.style.height = 'auto'
        }
    }, [w, h, isResizable])

    useEffect(() => {
        if (isDraggable) {
            const containerNode = (
                ref.current?.parentNode
            )

            setParentNode(
                offsetParent ?
                    $(offsetParent)[0]
                    : containerNode.parentNode
            )
        }
    }, [isDraggable, offsetParent])

    useEffect(() => {
        const containerNode = (
            ref.current?.parentNode
        )

        const w = containerNode?.clientWidth ?? 0
        const h = containerNode?.clientHeight ?? 0

        const offsetParentNode = (
            offsetParent ?
                $(offsetParent)[0]
                : containerNode.parentNode
        )

        const pw = offsetParentNode?.clientWidth ?? 0
        const ph = offsetParentNode?.clientHeight ?? 0

        const {
            x: pX, y: pY
        } = offsetParentNode.getBoundingClientRect()

        let x = pX
        let y = pY

        switch (defaultPosition) {
            case 'center':
                x += pw / 2 - w / 2
                y += ph / 2 - h / 2
                break
            case 'top-left':
                break
            case 'top-right':
                x += pw - w
                break
            case 'bottom-left':
                y += ph - h
                break
            case 'bottom-right':
                x += pw - w
                y += ph - h
                break
            default:
                x += defaultPosition?.x ?? 0
                y += defaultPosition?.y ?? 0
        }

        if (!isDraggable) {
            x += offset?.x ?? 0
            y += offset?.y ?? 0
        }

        setPosition({ x, y })
    }, [
        offset.x,
        offset.y,
        isDraggable,
        offsetParent,
        isMaximized,
        defaultPosition
    ])

    return content
})

Popup.propTypes = {
    isOpen: PTypes.bool,
    className: PTypes.string,

    isDraggable: PTypes.bool,
    isResizable: PTypes.bool,
    hasMaximizeButton: PTypes.bool,

    dragBounds: PTypes.oneOfType([
        PTypes.string,
        PTypes.object
    ]),

    width: PTypes.oneOfType([
        PTypes.string,
        PTypes.number
    ]),
    height: PTypes.oneOfType([
        PTypes.string,
        PTypes.number
    ]),
    size: PTypes.oneOf(SIZES),
    offset: PTypes.shape({
        x: PTypes.number,
        y: PTypes.number
    }),
    defaultPosition: PTypes.oneOf([
        'center',
        'top-left',
        'top-right',
        'bottom-left',
        'bottom-right',
        PTypes.shape({
            x: PTypes.number,
            y: PTypes.number
        })
    ]),

    minSizeConstraints: PTypes.arrayOf(PTypes.number),
    maxSizeConstraints: PTypes.arrayOf(PTypes.number),

    onDoubleClickHeader: PTypes.func,
    onClickMinimizeBtn: PTypes.func,
    onDoubleClickBody: PTypes.func
}

Popup.defaultProps = {
    size: 'big',
    isDraggable: true,
    isResizable: true,
    hasMaximizeButton: true,
    hasMinimizeButton: true,
    defaultPosition: 'center',
    onDoubleClickBody: noop,
    onClickMinimizeBtn: noop,
    onDoubleClickHeader: noop,
    minSizeConstraints: [450, 500],
    handle: '.VideoChatPopup-Header'
}

export default Popup