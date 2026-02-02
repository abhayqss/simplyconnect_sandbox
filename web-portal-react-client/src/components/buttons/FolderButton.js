import React from 'react'

import PTypes from 'prop-types'

import { ReactComponent as Icon } from 'images/folder-4.svg'

import { IconButton } from '.'

export default function FolderButton(props) {
    return (
        <IconButton
            {...props}
            Icon={Icon}
        />
    )
}

FolderButton.propTypes = {
    name: PTypes.string,
    size: PTypes.number,
    onClick: PTypes.func,
    tipText: PTypes.string,
    tipPlace: PTypes.string,
    className: PTypes.string,
    tooltip: PTypes.oneOfType([PTypes.string, PTypes.object])
}