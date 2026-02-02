import React, {
  memo,
  useRef,
  useMemo,
  forwardRef,
  useLayoutEffect,
  useImperativeHandle,
} from 'react'

import cn from 'classnames'

import {
  noop,
  isFunction
} from 'underscore'

import {useDebouncedCallback} from 'use-debounce'

import {getFirstNodeInViewPort} from 'lib/utils/Utils'

import './FlatList.scss'

const DEFAULT_THRESHOLD = 20

function FlatList(
  {
    list,
    itemKey,
    loadMore,
    isReversed,
    className,
    itemClassName,
    shouldLoadMore,
    viewPortTop = 0,
    shouldAutoScroll,
    onScroll: onScrollCb = noop,
    children: renderItem,
    onEndReachedThreshold = DEFAULT_THRESHOLD,
  },
  outerRef
) {
  const ref = useRef()

  function getNode() {
    return ref.current
  }

  function getItemNodes() {
    return ref.current?.querySelectorAll('.FlatList-Item')
  }

  function scrollToBottom({behavior = 'auto'} = {}) {
    if (isReversed) {
      const node = ref.current
      let {scrollHeight, clientHeight} = node

      node.scroll({top: scrollHeight, behavior})

      if (scrollHeight === clientHeight) onScroll()
    }
  }

  const scrollTarget = useMemo(() => {
    // Find all elements in container which will be checked if are in view or not
    if (shouldLoadMore && shouldAutoScroll) {
      const nodes = getItemNodes()

      if (nodes) {
        return getFirstNodeInViewPort(nodes, viewPortTop)
      }
    }
  }, [viewPortTop, shouldLoadMore, shouldAutoScroll])

  const onScroll = useDebouncedCallback(() => {
    const node = getNode()
    const scrollTop = Math.abs(node?.scrollTop)

    if (isReversed) {
      if (scrollTop <= onEndReachedThreshold && shouldLoadMore) {
        loadMore()
      }
    } else {
      if (node.scrollHeight - scrollTop - node.clientHeight <= onEndReachedThreshold && shouldLoadMore) {
        loadMore()
      }
    }

    const nodes = getItemNodes()

    if (nodes) {
      onScrollCb({
        nodes,
        scrollTop,
        clientHeight: node.clientHeight,
        scrollHeight: node.scrollHeight,
      })
    }
  }, 200)

  useLayoutEffect(scrollToBottom, [isReversed])

  useLayoutEffect(() => {
    if (scrollTarget) {
      scrollTarget.scrollIntoView()
    }
  }, [scrollTarget])

  useImperativeHandle(outerRef, () => ({
    getNode,
    getItemNodes,
    scrollToBottom,
  }))

  const List = useMemo(() => list.map((item, index) => {
    let className = isFunction(itemClassName) ? itemClassName(item, index) : itemClassName
    let key = itemKey ? item[itemKey] : index

    return (
      <div key={key} className={cn('FlatList-Item', className)}>
        {renderItem(item, index)}
      </div>
    )
  }), [itemClassName, itemKey, list, renderItem])

  return (
    <div className={cn('FlatList', className)} ref={ref} onScroll={onScroll}>
      {List}
    </div>
  )
}

export default memo(forwardRef(FlatList))
