import React, {
    useRef,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import { reject } from 'underscore'

import useResizeObserver from 'use-resize-observer'

import { useDebouncedCallback } from 'use-debounce'

import './TilesLayout.scss'

const MOBILE_MAX_WIDTH = 667
const TABLET_MAX_WIDTH = 1024

function grid(node) {
    return {
        setDimension(cols, rows) {
            node.style.setProperty('--col-count', cols)
            node.style.setProperty('--row-count', rows)
            return this
        },
        cell(index) {
            const cellNode = node.children.item(index)

            return {
                node: cellNode,
                setGrow(dir, grow) {
                    const base = reject(
                        cellNode.className.split(' '),
                        s => /grid-\w+-span-\d/.test(s)
                    ).join(' ')

                    cellNode.className = cn(base, `grid-${dir}-span-${grow}`)
                }
            }
        }
    }
}

/*
* @return [colCount, rowCount]
* */

function getGridDimension(total, width) {
    if (!total || total <= 1) return [1, 1]

    if (width < TABLET_MAX_WIDTH)
        return total === 2 ? [1, 2] : [2, Math.round(total / 2)]

    if (total === 2) return [2, 1]
    else if (total > 2 && total <= 8) {
        return [Math.round(total / 2), 2]
    } else if (total >= 9 && total <= 15) {
        return [Math.round(total / 3), 3]
    } else if (total >= 16 && total <= 25) {
        return [Math.round(total / 4), 4]
    }

    return [1, 1]
}

function getFirstGridCellGrowValue(total, width) {
    if (!total || total <= 2) return 1
    if (width < TABLET_MAX_WIDTH) return 2
    if ([17, 21].includes(total)) return 4
    if ([10, 13, 18, 22].includes(total)) return 3
    if ([3, 5, 7, 11, 14, 19, 23].includes(total)) return 2
    return 1
}

export default function TilesLayout({ children, className }) {
    const ref = useRef()

    const count = children.size ?? children.length

    const update = useCallback(({ width = ref.current.clientWidth } = {}) => {
        const node = ref.current

        const grow = getFirstGridCellGrowValue(count, width)
        const [colCount, rowCount] = getGridDimension(count, width)
        const direction = width <= TABLET_MAX_WIDTH ? 'column' : 'row'

        grid(node)
            .setDimension(colCount, rowCount)
            .cell(0).setGrow(direction, grow)
    }, [count])

    const debouncedUpdate = useDebouncedCallback(update, 100)

    useResizeObserver({ ref, onResize: debouncedUpdate })

    useEffect(update, [update])

    return (
        <div ref={ref} className={cn('VideoChatTilesLayout', className)}>
            {children}
        </div>
    )
}