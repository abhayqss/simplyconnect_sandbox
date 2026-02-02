import React from 'react'

import { compact } from 'underscore'

import { Breadcrumbs as BaseBreadcrumbs } from 'components'

const items = compact([
    {
        title: 'Marketplace',
        href: '/marketplace',
        isEnabled: true,
    },
    {
        title: 'Saved for Later',
        href: '/marketplace',
        isActive: true
    }
])

function Breadcrumbs() {
    return (
        <BaseBreadcrumbs
            className='margin-top-15 margin-bottom-15'
            items={items}
        />
    )
}

export default Breadcrumbs