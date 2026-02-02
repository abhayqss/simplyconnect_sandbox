import React from 'react'

import { ReactComponent as Manual } from 'images/manual.svg'
import { ReactComponent as Product } from 'images/product.svg'
import { ReactComponent as ContactUs } from 'images/contact-us.svg'

const SIDE_BAR_ITEMS = [
    {
        section: {
            title: 'RELEASE NOTES & MANUALS',
            items: [
                {
                    title: 'Release Notes',
                    name: 'RELEASE_NOTES',
                    href: '/help/release-notes',
                    hintText: 'Release Notes',
                    renderIcon: (className) => <Product className={className}/>
                },
                {
                    title: 'User Manuals',
                    name: 'USER_MANUALS',
                    href: '/help/user-manuals',
                    hintText: 'User Manuals',
                    renderIcon: (className) => <Manual className={className}/>
                },
                {
                    title: 'Contact Us',
                    name: 'CONTACT_US',
                    href: '/help/contact-us',
                    hintText: 'Contact Us',
                    renderIcon: (className) => <ContactUs className={className}/>
                }
            ]
        }
    }
]

export function getSideBarItems() {
    return SIDE_BAR_ITEMS
}