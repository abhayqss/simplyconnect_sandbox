import React from 'react'

import './RoleInfo.scss'

function B({ children }) {
    return (
        <span className="font-open-sans-semibold">{children}</span>
    )
}

function Space() {
    return (
        <div className="RoleInfo-TextSpace"/>
    )
}

export default function RoleInfo() {
    return (
        <div className="RoleInfo">
            <div className="RoleInfo-Header">
                <div className="RoleInfo-HeaderText">
                    Role
                </div>
                <div className="RoleInfo-HeaderText">
                    Data access
                </div>
                <div className="RoleInfo-HeaderText">
                    Restrictions
                </div>
                <div className="RoleInfo-HeaderText">
                    Chat
                </div>
            </div>
            <div className="RoleInfo-Sections">
                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Organization admin
                    </div>
                    <div className="RoleInfo-SectionText">
                        Has access to data within all communities associated with his/her organization.
                    </div>
                    <div className="RoleInfo-SectionText">
                        The highest level of access. No restrictions
                    </div>
                    <div className="RoleInfo-SectionText">
                        Can start chat with any user
                        and any client in his organization.
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Community admin
                    </div>
                    <div className="RoleInfo-SectionText">
                        Has access to the data of the community s(he) is associated with and
                        <Space/>
                        a) Clients records if the user was added to client care team and
                        <Space/>
                        b) All clients’ records in the community if the user was added to community care team.
                    </div>
                    <div className="RoleInfo-SectionText">
                        No restrictions at the community level.
                    </div>
                    <div className="RoleInfo-SectionText">
                        Can start chat with:
                        <Space/>
                        a) Client care team members if the user added to client care team
                        <Space/>
                        b) Community care team members if the user added to community care team
                        <Space/>
                        c) Clients s(he) has access to.
                        <Space/>
                        d) Users associated with his/her community
                        <Space/>
                        e) Any user in his/her organization
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Care Coordinator<br/>
                        Care Manager<br/>
                        Community Provider
                    </div>
                    <div className="RoleInfo-SectionText">
                        Don’t have access to any clients’ records until they’re added to client or community care team.
                        They will have access to
                        <Space/>
                        a) Clients records if the user was added to client care team and
                        <Space/>
                        b) All clients’ records in the community if the user was added to community care team.
                    </div>
                    <div className="RoleInfo-SectionText">
                        - Manage community/organization
                        <Space/>
                        - View audit logs
                    </div>
                    <div className="RoleInfo-SectionText">
                        Can start chat with:<br/>
                        <Space/>
                        a) Client care team members if the user added to client care team
                        <Space/>
                        b) Community care team members if the user added to community care team
                        <Space/>
                        c) Clients s(he) has access
                        <Space/>
                        d) Contacts (users) created by self
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Primary Physician
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as:
                        <Space/>
                        - Care Coordinator<br/>
                        - Care Manager<br/>
                        - Community Provider
                    </div>
                    <div className="RoleInfo-SectionText">
                        - Create/update clients’ records
                        <Space/>
                        - View audit logs
                        <Space/>
                        - Manage community /organization
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as:
                        <Space/>
                        - Care Coordinator<br/>
                        - Care Manager<br/>
                        - Community Provider
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Service Provider<br/>
                        Behavioral Health<br/>
                        Nurse
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as:
                        <Space/>
                        - Care Coordinator<br/>
                        - Care Manager<br/>
                        - Community Provider
                    </div>
                    <div className="RoleInfo-SectionText">
                        - Create/update clients’ records
                        <Space/>
                        - Create/update service plan
                        <Space/>
                        - Manage community /organization
                        <Space/>
                        - View audit logs
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as:
                        <Space/>
                        - Care Coordinator<br/>
                        - Care Manager<br/>
                        - Community Provider
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Parent/Guardian
                    </div>
                    <div className="RoleInfo-SectionText">
                        Don’t have access to any clients’ records
                        until added to client care team.
                    </div>
                    <div className="RoleInfo-SectionText">
                        - Create/update clients’ records
                        <Space/>
                        - Create/update assessments but PHQ-9/GAD-7
                        <Space/>
                        - Create/update/view service plan
                        <Space/>
                        - Request/view rides
                        <Space/>
                        - Place lab orders
                        <Space/>
                        - Manage community /organization
                        <Space/>
                        - View referrals
                        <Space/>
                        - View incident reports
                        <Space/>
                        - View audit logs
                    </div>
                    <div className="RoleInfo-SectionText">
                        Can start chat with:
                        <Space/>
                        a) Client care team members if the user added to client care team
                        <Space/>
                        b) Clients s(he) has access
                        <Space/>
                        c) Contacts (users) created by self
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Person Receiving Services
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as Parent/Guardian
                    </div>
                    <div className="RoleInfo-SectionText">
                        - Create/update clients’ records
                        <Space/>
                        - Create/update assessments but PHQ-9/GAD-7
                        <Space/>
                        - Create/update/view service plan
                        <Space/>
                        - Upload/delete documents
                        <Space/>
                        - Request/view rides
                        <Space/>
                        - Place lab orders
                        <Space/>
                        - Manage community/organization
                        <Space/>
                        - View referrals
                        <Space/>
                        - View incident reports
                        <Space/>
                        - View audit logs
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as Parent/Guardian
                    </div>
                </div>

                <div className="RoleInfo-Section">
                    <div className="RoleInfo-Role">
                        Pharmacist<br/>
                        Pharmacy Technician
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as:
                        <Space/>
                        -Care Coordinator<br/>
                        -Care Manager<br/>
                        -Community Provider
                    </div>
                    <div className="RoleInfo-SectionText">
                        - Create/update clients’ records
                        <Space/>
                        - View assessments
                        <Space/>
                        - View service plans
                        <Space/>
                        - Request/view rides
                        <Space/>
                        - Create event/note
                        <Space/>
                        - Place lab orders
                        <Space/>
                        - View referrals
                        <Space/>
                        - View incident reports
                        <Space/>
                        - Manage community/organization
                        <Space/>
                        - View audit logs
                    </div>
                    <div className="RoleInfo-SectionText">
                        The same as:
                        <Space/>
                        -Care Coordinator<br/>
                        -Care Manager<br/>
                        -Community Provider
                    </div>
                </div>
            </div>
        </div>
    )
}

