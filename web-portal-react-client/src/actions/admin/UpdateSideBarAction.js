import React, { Component } from 'react'

import { isEqual } from 'underscore'

import { connect } from 'react-redux'

import { getSideBarItems } from 'containers/Admin/SideBarItems'

/*import {
    LoadCanViewAuditLogsAction
} from 'actions/admin'*/

import {
    SYSTEM_ROLES,
    PROFESSIONAL_SYSTEM_ROLES
} from 'lib/Constants'

import UpdateSideBarAction from '../sidebar/UpdateSideBarAction'

const { EXTERNAL_PROVIDER, CONTENT_CREATOR } = SYSTEM_ROLES

function mapStateToProps(state) {
    return {
        auth: state.auth,
        canViewAuditLogs: state.audit.log.can.view.value
    }
}

export default connect(mapStateToProps, null)(class extends Component {

    hasBeenPerformed = false

    componentDidMount() {
        this.hasBeenPerformed = true
    }

    render() {
        const {
            auth,
            canViewAuditLogs,
            params: { changes } = {}
        } = this.props

        const user = auth.login.user.data

        const isProfessionalRole = (
            PROFESSIONAL_SYSTEM_ROLES.includes(user?.roleName)
        )

        const isExternalProviderRole = (
            user?.roleName === EXTERNAL_PROVIDER
        )

        const isContentCreatorRole = user?.roleName === CONTENT_CREATOR

        const permissions = {
            canViewAuditLogs,
            canViewContacts: true,
            canViewOrganizations: (
                isProfessionalRole
                || isContentCreatorRole
            )
        }

        return (
            <>
                {/*<LoadCanViewAuditLogsAction/>*/}
                <UpdateSideBarAction
                    isMultiple
                    shouldPerform={prevParams => (
                        !(
                            this.hasBeenPerformed
                            && isEqual(prevParams.permissions, permissions)
                        )
                    )}
                    params={{
                        permissions,
                        changes: {
                            isHidden: isExternalProviderRole,
                            items: getSideBarItems(permissions),
                            ...changes
                        }
                    }}
                />
            </>
        )
    }
})