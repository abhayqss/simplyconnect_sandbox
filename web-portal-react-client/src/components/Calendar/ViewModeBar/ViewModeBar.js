import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { VIEW_MODE } from '../Constants'

import { noop } from 'lib/utils/FuncUtils'
import { isNotEmpty } from 'lib/utils/ArrayUtils'

import './ViewModeBar.scss'

const { DAY, TODAY, WEEK, WORK_WEEK, MONTH } = VIEW_MODE

const ITEMS = [
    { name: DAY, title: 'DAY' },
    { name: WORK_WEEK, title: 'WORK WEEK' },
    { name: WEEK, title: 'FULL WEEK' },
    { name: MONTH, title: 'MONTH' },
    { name: TODAY, title: 'TODAY' }
]

const ViewModeBarItems = memo(
    function ViewModeBarItems({ items, onClickItem }) {
        return isNotEmpty(items) && (
            <>
                {items.map(o => (
                    <div
                        key={o.name}
                        onClick={e => onClickItem(o, e)}
                        className={cn('ViewModeBar-Item', { 'ViewModeBar-Item_active': o.isActive })}
                    >
                        {o.title}
                    </div>
                ))}
            </>
        )
    }
)

ViewModeBarItems.propTypes = {
    items: PTypes.array,
    onClickItem: PTypes.func
}

ViewModeBarItems.defaultProps = {
    items: [],
    onClickItem: noop
}

function ViewModeBar({ selectedMode, onChangeMode }) {
    const items = useMemo(
        () => ITEMS.map(
            o => o.name === selectedMode ? {
                ...o, isActive: true
            } : o
        ), [selectedMode]
    )

    const onClickItem = useCallback(item => {
        onChangeMode(item.name)
    }, [onChangeMode])

    return (
        <div className="ViewModeBar">
            <ViewModeBarItems
                items={items}
                onClickItem={onClickItem}
            />
        </div>
    )
}

ViewModeBar.propTypes = {
    selectedMode: PTypes.string,
    onChangeMode: PTypes.func
}

ViewModeBar.defaultProps = {
    onChangeMode: noop
}

export default memo(ViewModeBar)