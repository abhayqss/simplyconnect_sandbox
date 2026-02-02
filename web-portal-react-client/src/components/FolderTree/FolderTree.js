import React from 'react'

import cn from 'classnames'

import './FolderTree.scss'

function FolderNode({ data }) {
    const { name, children = [] } = data

    return (
        <div className="FolderNode">
            <div>
                <i className="FolderNode-Icon" />
                <div>{name}</div>
            </div>

            <FolderTree
                nodes={children}
                className="FolderTree_subtree"
            />
        </div>
    )
}

function FolderTree({ nodes, className }) {
    if (!nodes?.length) {
        return null
    }

    return (
        <div className={cn('FolderTree', className)}>
            {nodes.map((node, i) => (
                <FolderNode key={i} data={node} />
            ))}
        </div>
    )
}

export default FolderTree
