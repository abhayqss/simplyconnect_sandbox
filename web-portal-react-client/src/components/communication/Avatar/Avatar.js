import React, {
    useRef,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { Image } from 'react-bootstrap'

import useResizeObserver from 'use-resize-observer'

import { useAvatarQuery } from 'hooks/business/avatar'

import { measure } from 'lib/utils/Utils'

import { ReactComponent as StatusOffline } from 'images/status-offline.svg'

import './Avatar.scss'

const propTypes = {
    id: PTypes.number,
    name: PTypes.string,
    size: PTypes.number,
    isRound: PTypes.bool,
    actions: PTypes.object,
    nameColor: PTypes.string,
    className: PTypes.string,
    maxInitials: PTypes.number,
    backgroundColor: PTypes.string,
}

const defaultProps = {
    size: 50,
    isRound: true,
    maxInitials: 2,
    nameColor: '#03b6f3',
    backgroundColor: '#ffffff'
}

export default function Avatar(
    {
        id,
        src,
        isRound,
        children,
        isOnline,
        className,
        withStatus,
        initialsColor,
        backgroundColor
    }
) {
    const ref = useRef()

    const { data } = useAvatarQuery(id)

    const update = useCallback(({ width, height }) => {
        const node = ref.current
        const size = Math.min(width, height) / 2.8 + 'px'
        node && node.style.setProperty('--initialsSize', size)
    }, [])

    useResizeObserver({ ref, onResize: update })

    useEffect(() => update(measure(ref.current)), [update])

    return (
        <div
            ref={ref}
            className={cn('Avatar', className)}
        >
            {(src || data) ? (
                <Image
                    src={src || data}
                    className="Avatar-Image"
                    style={{ borderRadius: isRound ? '50%' : 0 }}
                />
            ) : (
                <div
                    style={{
                        backgroundColor,
                        color: initialsColor,
                        borderRadius: isRound ? '50%' : 0
                    }}
                    className="Avatar-Initials"
                >
                    {children}
                </div>
            )}

            {withStatus && (
                <StatusOffline className={cn(
                    'Avatar-StatusIndicator',
                    { 'Avatar-StatusIndicator_online': isOnline }
                )} />
            )}
        </div>
    )
}

Avatar.propTypes = propTypes
Avatar.defaultProps = defaultProps
