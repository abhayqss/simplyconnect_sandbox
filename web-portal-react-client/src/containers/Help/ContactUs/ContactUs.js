import React, {
    useState,
    useEffect
} from 'react'

import DocumentTitle from 'react-document-title'

import { Button } from 'reactstrap'

import {
    Breadcrumbs
} from 'components'

import ContactUsEditor from './ContactUsEditor/ContactUsEditor'

import { getSideBarItems } from 'containers/Help/SideBarItems'

import {
    useSideBarUpdate
} from 'hooks/common/redux'

import './ContactUs.scss'

function ContactUs() {
    const [isEditorOpen, setIsEditorOpen] = useState(false)

    const updateSideBar = useSideBarUpdate()

    function openEditor() {
        setIsEditorOpen(true)
    }

    function closeEditor() {
        setIsEditorOpen(false)
    }

    function onSaveSuccess() {
        setIsEditorOpen(false)
    }

    useEffect(() => {
        updateSideBar({
            isHidden: false,
            items: getSideBarItems()
        })
    }, [updateSideBar])

    return (
        <DocumentTitle title="Simply Connect | Contact Us">
            <div className="ContactUs">
                <div className="ContactUs-Header">
                    <Breadcrumbs
                        items={[
                            { title: 'Help', href: '/help' },
                            { title: 'Contact Us', href: '/help/contact-us', isActive: true },
                        ]}
                        className="margin-bottom-30"
                    />

                    <div className="ContactUs-Title">Contact Us</div>
                </div>

                <div className="ContactUs-Body">
                    <div className="ContactUs-Info">
                        <p className="ContactUs-InfoTitle">Address</p>
                        <p>Corporate Headquarters</p>
                        <p>12400 Whitewater Drive, Suite 2010</p>
                        <p>Minnetonka, MN 55343</p>
                    </div>

                    <div className="ContactUs-Info">
                        <p className="ContactUs-InfoTitle">Sales Team</p>
                        <p>(844) 666-3038 opt 1</p>
                        <p>Email: sales@simplyconnect.me</p>
                    </div>

                    <div className="ContactUs-Info">
                        <p className="ContactUs-InfoTitle">Support Team</p>
                        <p>(844) 666-3038 opt 2</p>
                        <p>Email: support@simplyconnect.me</p>
                    </div>

                    <div className="ContactUs-ActionButtons">
                        <Button
                            color="success"
                            className="ContactUs-Button"
                            onClick={openEditor}
                        >
                            Contact Us
                        </Button>
                    </div>
                </div>

                <ContactUsEditor
                    isOpen={isEditorOpen}
                    onClose={closeEditor}
                    onSaveSuccess={onSaveSuccess}
                />
            </div>
        </DocumentTitle>
    )
}

export default ContactUs
