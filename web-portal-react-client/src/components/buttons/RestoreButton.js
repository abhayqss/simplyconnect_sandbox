import React from 'react'

import PTypes from 'prop-types'

import { ReactComponent as Icon } from 'images/restore.svg'

import { IconButton } from './'

function RestoreButton(props) {
    return (
        <IconButton
            {...props}
            Icon={Icon}
        />
    )
}

export default RestoreButton

RestoreButton.propTypes = {
    id: PTypes.string,
    name: PTypes.string,
    size: PTypes.number,
    onClick: PTypes.func,
    tipText: PTypes.string,
    tipPlace: PTypes.string,
    renderTip: PTypes.func,
    className: PTypes.string,
    tooltip: PTypes.oneOfType([PTypes.string, PTypes.object])
}