import React from 'react'

import PTypes from 'prop-types'

import { ReactComponent as Icon } from 'images/add-item.svg'

import { IconButton } from './'

export default function AddButton(props) {
    return (
        <IconButton
            {...props}
            Icon={Icon}
        />
    )
}

AddButton.propTypes = {
    size: PTypes.number,
    name: PTypes.string,
    tipText: PTypes.string,
    tipPlace: PTypes.string,
    className: PTypes.string,
    tipTrigger: PTypes.string,
    tipClassName: PTypes.string,
    tooltip: PTypes.oneOfType([PTypes.string, PTypes.object]),

    onClick: PTypes.func,
    renderTip: PTypes.func
}