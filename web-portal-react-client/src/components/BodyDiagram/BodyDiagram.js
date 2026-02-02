import React, {
    memo,
    useRef,
    useState,
    useEffect,
} from 'react'

import cn from 'classnames'

import { isArray } from 'underscore'

import {
    Card,
    Label,
    CardImg,
    CardImgOverlay
} from 'reactstrap'

import BodyDiagramImage from 'images/body-diagram.jpg'
import { ReactComponent as CrossIcon } from 'images/cross.svg'

import PropTypes from 'prop-types'

import './BodyDiagram.scss'

const POINT_SIZE = 16

const getPercentageValue = (value, offset) => parseFloat((value / offset).toFixed(2))
const getPixelValue = (percentage, offset) => offset * percentage

function Dialog({ onClick, isOpen, children, area, container }) {
    const {
        width: containerWidth,
        height: containerHeight,
    } = container?.getBoundingClientRect() ?? {}

    return isOpen ? (
        <div
            onClick={onClick}
            className="BodyDiagram-Dialog"
            style={{
                left: getPixelValue(area.x, containerWidth),
                top: getPixelValue(area.y, containerHeight),
            }}
        >
            <CrossIcon className="BodyDiagram-DeleteIcon"/>

            {children}
        </div>
    ) : null
}

function BodyDiagram({
    id,
    name,
    value,
    label,
    error,
    onChange,
    className,
    isDisabled = false,
}) {
    const pointsContainer = useRef()
    const [selectedArea, setSelectedArea] = useState(null)
    const [isAddDialogOpen, toggleAddDialog] = useState(false)
    const [isFetchingImage, setIsFetchingImage] = useState(true)
    const [isRemoveDialogOpen, toggleRemoveDialog] = useState(false)

    const {
        width: containerWidth,
        height: containerHeight,
    } = pointsContainer.current?.getBoundingClientRect() ?? {}

    function changePoints(value) {
        if (isDisabled) return

        onChange(name, value)
    }

    function onCloseAddDialog() {
        toggleAddDialog(false)
    }

    function onCloseRemoveDialog() {
        toggleRemoveDialog(false)
    }

    function onDiagramClick(event) {
        if (isDisabled) return

        let { clientX, clientY, target } = event
        let { left, top, height, width } = target.getBoundingClientRect()

        let x = clientX - left
        let y = clientY - top

        let area = { x: getPercentageValue(x, width), y: getPercentageValue(y, height) }

        toggleAddDialog(true)
        setSelectedArea(area)
        toggleRemoveDialog(false)
    }

    function onClickPoint(point) {
        if (isDisabled) return

        setSelectedArea(point)
        toggleRemoveDialog(true)
    }

    function onAddPoint() {
        changePoints(isArray(value) ? [...value, selectedArea] : value.push(selectedArea))
        setSelectedArea(null)
    }

    function onRemovePoint() {
        changePoints(value.filter(p => p !== selectedArea))
        setSelectedArea(null)
    }

    function onRemoveAllPoints() {
        changePoints(isArray(value) ? [] : value.clear())
        setSelectedArea(null)
    }

    function hideDialogsIfDisabled() {
        if (isDisabled) {
            toggleAddDialog(false)
            toggleRemoveDialog(false)
        }
    }

    useEffect(hideDialogsIfDisabled, [isDisabled])

    let size = value.size || value.length

    return (
        <div id={id} className={cn(
            'BodyDiagram',
            className,
            { isDisabled: isDisabled },
            { hasError: !!error },
        )}>
            {label && (
                <Label className="BodyDiagram-Label">
                    {label}
                </Label>
            )}
            <Card className="BodyDiagram-Card">
                <CardImg
                    width="100%"
                    src={BodyDiagramImage}
                    onLoad={() => setIsFetchingImage(false)}
                />

                <CardImgOverlay className="BodyDiagram-Overlay">
                    <div
                        className="BodyDiagram-Points"
                        ref={pointsContainer}
                        onClick={onDiagramClick}
                    >
                        {!isFetchingImage && value.map((point, index) => (
                            <CrossIcon
                                key={point.x + point.y + index}
                                className="BodyDiagram-Point"
                                style={{
                                    width: POINT_SIZE,
                                    height: POINT_SIZE,
                                    left: getPixelValue(point.x, containerWidth) - POINT_SIZE / 2,
                                    top: getPixelValue(point.y, containerHeight) - POINT_SIZE / 2,
                                }}
                                onClick={e => {
                                    e.stopPropagation()
                                    onClickPoint(point)
                                }}
                            />
                        ))}
                    </div>

                    <Dialog
                        isOpen={isAddDialogOpen}
                        onClick={onCloseAddDialog}
                        area={selectedArea}
                        container={pointsContainer.current}
                    >
                        <div className="BodyDiagram-ActionPoint"></div>

                        <div className="BodyDiagram-ActionItem" onClick={onAddPoint}>Add Mark</div>

                        {size > 0 && (
                            <div className="BodyDiagram-ActionItem" onClick={onRemoveAllPoints}>
                                Remove All Marks
                            </div>
                        )}
                    </Dialog>

                    <Dialog
                        isOpen={isRemoveDialogOpen}
                        onClick={onCloseRemoveDialog}
                        area={selectedArea}
                        container={pointsContainer.current}
                    >
                        <div className="BodyDiagram-ActionItem" onClick={onRemovePoint}>Remove mark</div>

                        {size > 0 && (
                            <div className="BodyDiagram-ActionItem" onClick={onRemoveAllPoints}>
                                Remove all marks
                            </div>
                        )}
                    </Dialog>
                </CardImgOverlay>
            </Card>

            <div class="BodyDiagram-Error">
                {error}
            </div>
        </div>
    )
}

BodyDiagram.propTypes = {
    isDisabled: PropTypes.bool
}

export default memo(BodyDiagram)
