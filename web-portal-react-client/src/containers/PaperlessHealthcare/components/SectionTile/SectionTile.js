import React, {
    memo,
    useCallback
} from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'

import { ReactComponent as Arrow } from 'images/arrow-right-2.svg'

import './SectionTile.scss'

function SectionTile({ name, title, imageSrc, onClick, className }) {

    const _onClick = useCallback(e => {
        onClick(name, e)
    }, [name, onClick])

    return (
        <div
            id={`tile-tooltip_${name}`}
            className={cn('SectionTile', className)}
            onClick={_onClick}
        >
            <img src={imageSrc} className="SectionTile-Image"/>
            <div className="SectionTile-Title">
                {title}
                <Arrow className="SectionTile-TitleIcon"/>
            </div>
            <Tooltip
                target={`tile-tooltip_${name}`}
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
            >
                Click to find out more
            </Tooltip>
        </div>
    )
}

SectionTile.propTypes = {
    name: PTypes.string,
    title: PTypes.string,
    imageSrc: PTypes.string,
    className: PTypes.string,
    onClick: PTypes.func
}

export default memo(SectionTile)