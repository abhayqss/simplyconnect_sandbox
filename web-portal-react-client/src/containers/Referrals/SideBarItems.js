import React from 'react'

import { ReactComponent as Inbound } from 'images/inbound.svg'
import { ReactComponent as Outbound } from 'images/outbound.svg'

export function getSideBarItems() {
    return [
        {
            title: 'Inbound',
            hintText: 'Inbound Referrals and Inquiries',
            href: '/inbound-referrals',
            renderIcon: (className) => <Inbound className={className} />
        },
        {
            title: 'Outbound',
            hintText: 'Outbound Referrals',
            href: '/outbound-referrals',
            renderIcon: (className) => <Outbound className={className} />
        }
    ]
}