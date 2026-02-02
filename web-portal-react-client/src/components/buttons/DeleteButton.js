import React from 'react'

import PTypes from 'prop-types'

import { ReactComponent as Icon } from 'images/delete.svg'

import { IconButton } from './'

function DeleteButton(props) {
    return (
        <IconButton
            {...props}
            Icon={Icon}
        />
    )
}

export default DeleteButton

DeleteButton.propTypes = {
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