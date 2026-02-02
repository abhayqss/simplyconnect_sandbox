import React, { memo, useRef, useEffect } from 'react'

import cn from 'classnames'

import { ifElse } from 'lib/utils/Utils'
import { css, iframe } from 'lib/utils/DomUtils'

import './Track.scss'

function addVideoStream(node, track) {
    const video = iframe()
        .add({
            parent: node,
            attributes: {
                width: '100%',
                height: '100%',
                allowFullscreen: true,
                className: 'Track-VideoFrame'
            }
        })
        .body(track.attach())
        .style({ margin: '0' })
        .find('video')

    css(video, {
        width: '100%',
        height: '100%',
        objectFit: 'contain'
    })
}

const addTrack = ifElse(
    (track) => track.kind === 'video',
    (track, node) => addVideoStream(node, track),
    (track, node) => node.appendChild(track.attach())
)

const removeTrack = track => {
    const mediaElements = track.detach()

    mediaElements.forEach(mediaElement => mediaElement.remove())
}

function Track({ track, className }) {
    const ref = useRef()

    useEffect(() => {
        const node = ref.current

        addTrack(track, node)

        return () => removeTrack(track)
    }, [track])

    return track ? (
        <div
            ref={ref}
            className={cn('Track', {
                'Track-Audio': track.kind === 'audio',
                'Track-Video': track.kind === 'video',
            }, className)}
        />
    ) : null
}

export default memo(Track)