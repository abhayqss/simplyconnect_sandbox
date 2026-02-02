import React, {
    useRef,
    useState,
    useLayoutEffect
} from 'react'

import cn from 'classnames'

import './CollapsibleText.scss'

function CollapsibleText({
    lines = 2,
    readMoreText = 'Read more',
    readLessText = 'Close',

    className,

    children: content
}) {
    const ref = useRef()

    const [isCollapsed, setCollapsed] = useState(false)
    const [hasMoreText, setHasMoreText] = useState(false)

    function toggle() {
        setCollapsed(prevValue => !prevValue)
    }

    useLayoutEffect(() => {
        const element = ref.current

        if (element) {
            const contentLines = element.getClientRects().length
            const moreThanOne = contentLines > lines

            setCollapsed(moreThanOne)
            setHasMoreText(moreThanOne)
        }
    }, [lines])

    if (!content) {
        return null
    }

    return (
        <div className={cn('CollapsibleText', className)}>
            <span
                ref={ref}
                className={
                    cn('CollapsibleText-Content', {
                        'CollapsibleText-Content_collapsed': isCollapsed
                    })
                }
                style={{
                    WebkitLineClamp: isCollapsed ? lines : 'none'
                }}
            >
                {content}
            </span>

            {hasMoreText && (
                <div
                    onClick={toggle}
                    className="CollapsibleText-Button"
                >
                    {isCollapsed ? readMoreText : readLessText}
                </div>
            )}
        </div>
    )
}

export default CollapsibleText
