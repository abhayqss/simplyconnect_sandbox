import React from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { connect } from 'react-redux'

import ReactAvatar from 'react-avatar'
import { Image } from 'react-bootstrap'

import { useAvatarQuery } from 'hooks/business/avatar'

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
    staleTime: 0,
    isRound: true,
    maxInitials: 2,
    nameColor: '#03b6f3',
    backgroundColor: '#ffffff'
}

function Avatar(
    {
        id,
        name,
        size,
        isRound,
        nameColor,
        className,
        cacheTime,
        staleTime,
        defaultSrc,
        maxInitials,
        backgroundColor
    }
) {
    const { data } = useAvatarQuery(id, {
        cacheTime, staleTime
    })

    return data ? (
        <Image
            src={data}
            style={{
                width: size + 'px',
                height: size + 'px',
                borderRadius: isRound ? '50%' : 0
            }}
            className={className}
        />
    ) : (
        <ReactAvatar
            name={name}
            size={String(size)}
            round={isRound}
            src={defaultSrc}
            fgColor={nameColor}
            maxInitials={maxInitials}
            color={backgroundColor}
            className={cn('Avatar', className)}
        />
    )
}

Avatar.propTypes = propTypes
Avatar.defaultProps = defaultProps

export default connect()(Avatar)
