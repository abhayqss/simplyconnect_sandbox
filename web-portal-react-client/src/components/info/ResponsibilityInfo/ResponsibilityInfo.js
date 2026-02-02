import React from 'react'

import './ResponsibilityInfo.scss'

export default function ResponsibilityInfo() {
    return (
        <div className="ResponsibilityInfo">
            <div className="ResponsibilityInfo-Header">
                <div className="ResponsibilityInfo-HeaderText">
                    Responsibility
                </div>
                <div className="ResponsibilityInfo-HeaderText">
                    Event visibility
                </div>
                <div className="ResponsibilityInfo-HeaderText">
                    Notification
                </div>
            </div>
            <div className="ResponsibilityInfo-Sections">
                <div className="ResponsibilityInfo-Section">
                    <div className="ResponsibilityInfo-Responsibility">
                        Responsible
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        The event is visible to Care Team Member
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        Notification is sent to Care Team Member
                    </div>
                </div>

                <div className="ResponsibilityInfo-Section">
                    <div className="ResponsibilityInfo-Responsibility">
                        Accountable
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        The event is visible to Care Team Member
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        Notification is sent to Care Team Member
                    </div>
                </div>

                <div className="ResponsibilityInfo-Section">
                    <div className="ResponsibilityInfo-Responsibility">
                        Consulted
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        The event is visible to Care Team Member
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        Notification is sent to Care Team Member
                    </div>
                </div>

                <div className="ResponsibilityInfo-Section">
                    <div className="ResponsibilityInfo-Responsibility">
                        Informed
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        The event is visible to Care Team Member
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        Notification is sent to Care Team Member
                    </div>
                </div>

                <div className="ResponsibilityInfo-Section">
                    <div className="ResponsibilityInfo-Responsibility">
                        Viewable
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        The event is visible to Care Team Member
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        Notification is <span className="font-open-sans-semibold">NOT</span> sent to Care Team Member
                    </div>
                </div>

                <div className="ResponsibilityInfo-Section">
                    <div className="ResponsibilityInfo-Responsibility">
                        Not Viewable
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        The event is <span className="font-open-sans-semibold">NOT</span> visible to Care Team Member
                    </div>
                    <div className="ResponsibilityInfo-SectionText">
                        Notification is <span className="font-open-sans-semibold">NOT</span> sent to Care Team Member
                    </div>
                </div>
            </div>
        </div>
    )
}

