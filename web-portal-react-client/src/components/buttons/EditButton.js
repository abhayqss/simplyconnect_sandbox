import React from 'react'

import PTypes from 'prop-types'

import { ReactComponent as Icon } from 'images/pencil.svg'

import { IconButton } from './'

export default function EditButton(props) {
    return (
        <IconButton
            {...props}
            Icon={Icon}
        />
    )
}

EditButton.propTypes = {
    id: PTypes.string,
    name: PTypes.string,
    size: PTypes.number,
    onClick: PTypes.func,
    tipText: PTypes.string,
    tipPlace: PTypes.string,
    className: PTypes.string,
    tooltip: PTypes.oneOfType([PTypes.string, PTypes.object])
}