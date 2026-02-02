import React, {
    memo,
    useState,
    useEffect
} from 'react'

import { compact } from 'underscore'

import { useParams } from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import {
    Breadcrumbs
} from 'components'

import {
    Dialog,
    ErrorDialog
} from 'components/dialogs'

import {
    useAuthUser,
    useLocationState
} from 'hooks/common'

import {
    useSideBarUpdate,
	useProspectQuery,
    useCanViewProspectCareTeamQuery
} from 'hooks/business/Prospects'

import { pushIf } from 'lib/utils/ArrayUtils'

import {
    SYSTEM_ROLES,
    CARE_TEAM_AFFILIATION_TYPES
} from 'lib/Constants'

import CareTeamMemberList from './CareTeamMemberList/CareTeamMemberList'

import './CareTeam.scss'

const {
    REGULAR
} = CARE_TEAM_AFFILIATION_TYPES

const { HOME_CARE_ASSISTANT } = SYSTEM_ROLES

const SYS_ROLES_WITH_NOT_VIEWABLE_PROSPECT = [
    HOME_CARE_ASSISTANT
]

function CareTeam() {
    const { prospectId } = useParams()

    const user = useAuthUser()

    const {
        data: prospect
    } = useProspectQuery({ prospectId }, { staleTime: 0 })

    const {
        data: canView,
        isFetching: isFetchingCanView
    } = useCanViewProspectCareTeamQuery({ prospectId }, { staleTime: 0 })

    const updateSideBar = useSideBarUpdate({ prospectId })

    useEffect(() => {
        updateSideBar()
    }, [updateSideBar])

    const [
        {
            isCommunityStaffContactNeed,
            isAddingMembersInstructionNeed
        } = {},
        clearLocationState
    ] = useLocationState()

    const [isCommunityStaffContactDialogOpen, toggleCommunityStaffContactDialog] = useState(isCommunityStaffContactNeed)
    const [isAddingMembersInstructionDialogOpen, toggleAddingMembersInstructionDialog] = useState(isAddingMembersInstructionNeed)
    const [isNoPermissionDialogOpen, toggleNoPermissionDialog] = useState(false)

    useEffect(() => {
        if (!isFetchingCanView && !canView && !isNoPermissionDialogOpen) toggleNoPermissionDialog(true)
    }, [
        canView,
        isFetchingCanView
    ])

    const canViewProspect = Boolean(user && !(
        SYS_ROLES_WITH_NOT_VIEWABLE_PROSPECT.includes(
            user.roleName
        )
    ))

    return (
        <DocumentTitle title="Simply Connect | Prospects | Prospect Care Team">
            <div className="CareTeam">
                <Breadcrumbs
                    className='CareTeam-Breadcrumbs'
                    items={compact([
                        { title: 'Prospect', href: '/prospects', isEnabled: true },
                        prospect && {
                            title: prospect?.fullName ?? '',
                            href: `/prospects/${prospectId}`,
                            isActive: !canViewProspect
                        },
                        {
                            title: 'Care Team',
                            href: `/prospects/${prospectId}`,
                            isActive: true,
                        },
                    ])}
                />                
                <CareTeamMemberList
                    type={REGULAR}
                    title={pushIf(
                        ['Care Team'],
                        <span className="CareTeamMemberList-ProspectName">{` / ${prospect?.fullName}`}</span>,
                        !!prospect
                    )}
                    prospectId={prospectId}
                    hasActions={prospect?.isActive}
                />

                {isCommunityStaffContactDialogOpen && (
                    <Dialog
                        isOpen
                        buttons={[
                            {
                                text: 'Close',
                                color: 'success',
                                onClick: () => {
                                    clearLocationState()
                                    toggleCommunityStaffContactDialog(false)
                                }
                            }
                        ]}
                    >
                        Please reach out to Community Staff for help in Care Team set up.
                    </Dialog>
                )}

                {isAddingMembersInstructionDialogOpen && (
                    <Dialog
                        isOpen
                        title="Let's set up your care team!"
                        buttons={[
                            {
                                text: 'Close',
                                color: 'success',
                                onClick: () => {
                                    clearLocationState()
                                    toggleAddingMembersInstructionDialog(false)
                                }
                            }
                        ]}
                    >
                        <p>You can share your health information and communicate with your care team securely.</p>
                        <p>Once a new member is added to your care team, s(he) will receive a notification that s(he) can access your record.</p>
                        <p>Click Add Member button located on the top right corner and select a person you want to add.</p>
                        <p>You can change default responsibility and notification method(s) your care team member will be notified about events related to changes in your health.</p>
                    </Dialog>
                )}

                {isNoPermissionDialogOpen && (
                    <ErrorDialog
                        isOpen
                        title="You don't have permissions to see the prospect care team"
                        buttons={[
                            {
                                text: 'Close',
                                onClick: () => toggleNoPermissionDialog(false)
                            }
                        ]}
                    />
                )}
            </div>
        </DocumentTitle>
    )
}

export default memo(CareTeam)