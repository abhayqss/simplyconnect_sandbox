import { useCallback } from 'react'

function onObjectMoving(e) {
    const obj = e.target
    const canvas = obj.canvas
    const top = obj.top
    const left = obj.left
    const zoom = canvas.getZoom()
    const panX = canvas.viewportTransform[4]
    const panY = canvas.viewportTransform[5]

    const canvasWidth = canvas.width / zoom
    const canvasHeight = canvas.height / zoom

    const w = obj.width * obj.scaleX
    let leftAdjust
    let rightAdjust

    if (obj.originX === 'center') {
        leftAdjust = rightAdjust = w / 2
    } else {
        leftAdjust = 0
        rightAdjust = w
    }

    const h = obj.height * obj.scaleY
    let topAdjust
    let bottomAdjust

    if (obj.originY === 'center') {
        topAdjust = bottomAdjust = h / 2
    } else {
        topAdjust = 0
        bottomAdjust = h
    }

    const topBound = topAdjust - panY
    const bottomBound = canvasHeight - bottomAdjust - panY
    const leftBound = leftAdjust - panX
    const rightBound = canvasWidth - rightAdjust - panX

    if (w > canvasWidth) {
        obj.set({
            left: leftBound
        })
    } else {
        obj.set({
            left: Math.min(Math.max(left, leftBound), rightBound)
        })
    }

    if (h > canvasHeight) {
        obj.set({
            top: topBound
        })
    } else {
        obj.set({
            top: Math.min(Math.max(top, topBound), bottomBound)
        })
    }
}

function useCanvasObjectMovingRestrictions() {
    const add = useCallback(canvas => {
        if (canvas) canvas.on('object:moving', onObjectMoving)
    }, [])

    const remove = useCallback(canvas => {
        if (canvas) canvas.off('object:moving', onObjectMoving)
    }, [])

    return { add, remove }
}

export default useCanvasObjectMovingRestrictions
