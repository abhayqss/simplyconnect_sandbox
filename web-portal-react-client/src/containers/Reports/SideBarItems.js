import React from 'react'

import {ReactComponent as Report} from "images/report.svg"
import {ReactComponent as ReportSDoH} from "images/report-sdoh.svg"

const SIDE_BAR_ITEMS = [
    {
        title: 'Reports',
        href: '/reports',
        name: 'REPORTS',
        hintText: 'Reports',
        renderIcon: (className) => <Report className={className} />
    },
    {
        title: 'SDoH',
        href: '/sdoh/reports',
        name: 'SDOH',
        hintText: 'SDoH',
        renderIcon: (className) => <ReportSDoH className={className} />
    },
]

export function getSideBarItems () {
    return SIDE_BAR_ITEMS
}