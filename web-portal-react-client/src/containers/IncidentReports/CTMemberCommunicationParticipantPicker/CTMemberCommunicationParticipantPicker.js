import React, {
    useMemo,
    useState,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import {
    any,
    map,
    noop,
    union,
    isNumber
} from 'underscore'

import { Button } from 'reactstrap'

import {
    Modal,
    Loader,
    Scrollable
} from 'components'

import { TextField } from 'components/Form'

import { CancelConfirmDialog } from 'components/dialogs'

import { useItemPicker } from 'hooks/common'

import {
    isEmpty,
    isNotEmpty,
    anyIsNotEmpty
} from 'lib/utils/Utils'

import {
    useClientCareTeamMembersQuery,
    useCommunityCareTeamMembersQuery
} from 'hooks/business/conversations'

import { CTMemberList } from './components'

import './CTMemberCommunicationParticipantPicker.scss'

export default function CTMemberCommunicationParticipantPicker(
    {
        isOpen,
        clientId,
        communityId,
        isDisabledGroupName,
        communicationType = 'conversation',
        excludedContactIds = [],
        onClose: onCloseCb = noop,
        onComplete: onCompleteCb = noop,
        ...props
    }
) {
    const [groupName, setGroupName] = useState(props.groupName)

    const [clientCTeam, setClientCTeam] = useState([])
    const [communityCTeam, setCommunityCTeam] = useState([])

    const [isConfirmOpen, toggleConfirm] = useState(false)

    const {
        pick: pickClientCTMember,
        unpick: unpickClientCTMember,
        pickAll: pickAllClientCTMembers,
        unpickAll: unpickAllClientCTMembers
    } = useItemPicker(clientCTeam, {
        onPicked: setClientCTeam,
        isEqual: (a, b) => a.id === b.id
    })

    const {
        pick: pickCommunityCTMember,
        unpick: unpickCommunityCTMember,
        pickAll: pickAllCommunityCTMembers,
        unpickAll: unpickAllCommunityCTMembers
    } = useItemPicker(communityCTeam, {
        onPicked: setCommunityCTeam,
        isEqual: (a, b) => a.id === b.id
    })

    const isExcluded = useCallback(m => (
        excludedContactIds.includes(m.id)
    ), [excludedContactIds])

    const isChanged = (
        groupName !== props.groupName
        || anyIsNotEmpty(clientCTeam, communityCTeam)
    )

    const onChangeGroupName = useCallback((_, value) => {
        setGroupName(value)
    }, [])

    const {
        data: clientCTMembers,
        isFetching: isClientCTFetching
    } = useClientCareTeamMembersQuery(
        { clientId, communicationType },
        { enabled: isNumber(clientId) }
    )

    const mappedClientCTMembers = useMemo(() => (
        map(clientCTMembers, o => ({
            ...o,
            isDisabled: isExcluded(o),
            isChecked: isExcluded(o) || any(
                clientCTeam, m => m.id === o.id
            )
        }))
    ), [clientCTeam, isExcluded, clientCTMembers])

    const {
        data: communityCTMembers,
        isFetching: isCommunityCTFetching
    } = useCommunityCareTeamMembersQuery(
        {
            communicationType,
            communityIds: [communityId]
        },
        { enabled: isNumber(communityId) }
    )

    const mappedCommunityCTMembers = useMemo(() => (
        map(communityCTMembers, o => ({
            ...o,
            isDisabled: isExcluded(o),
            isChecked: isExcluded(o) || any(
                communityCTeam, m => m.id === o.id
            )
        }))
    ), [communityCTeam, isExcluded, communityCTMembers])

    const onCloseIntent = useCallback(() => {
        if (isChanged) toggleConfirm(true)
        else onCloseCb()
    }, [isChanged, onCloseCb])

    const onClose = useCallback(() => {
        toggleConfirm(false)
        onCloseCb()
    }, [onCloseCb])

    const onToggleClientCTMember = useCallback((isChecked, member) => {
        if (isChecked) pickClientCTMember(member)
        else unpickClientCTMember(member)
    }, [pickClientCTMember, unpickClientCTMember])

    const onToggleAllClientCTMembers = useCallback((isChecked, members) => {
        if (isChecked) pickAllClientCTMembers(members)
        else unpickAllClientCTMembers()
    }, [pickAllClientCTMembers, unpickAllClientCTMembers])

    const onToggleCommunityCTMember = useCallback((isChecked, member) => {
        if (isChecked) pickCommunityCTMember(member)
        else unpickCommunityCTMember(member)
    }, [pickCommunityCTMember, unpickCommunityCTMember])

    const onToggleAllCommunityCTMembers = useCallback((isChecked, members) => {
        if (isChecked) pickAllCommunityCTMembers(members)
        else unpickAllCommunityCTMembers()
    }, [pickAllCommunityCTMembers, unpickAllCommunityCTMembers])

    const onComplete = useCallback(() => {
        const contactIds = union(
            map(clientCTeam, o => o.id),
            map(communityCTeam, o => o.id)
        )
        onCompleteCb({ contactIds, groupName })
    }, [clientCTeam, communityCTeam, onCompleteCb, groupName])

    return (
        <>
            <Modal
                isOpen={isOpen}
                hasCloseBtn={false}
                title="Select Care Team Members"
                className="CTMemberGroupConversationParticipantPicker"
                renderFooter={() => (
                    <div className="">
                        <Button
                            outline
                            color="success"
                            onClick={onCloseIntent}
                        >
                            Cancel
                        </Button>
                        <Button
                            color="success"
                            disabled={
                                isEmpty(clientCTeam)
                                && isEmpty(communityCTeam)
                            }
                            onClick={onComplete}
                        >
                            Start {communicationType === 'conversation' ? 'Chat' : 'Call'}
                        </Button>
                    </div>
                )}
            >
                <Scrollable>
                    <div className="CTMemberGroupConversationParticipantPicker-Section">
                        <TextField
                            label="Group Name"
                            name="groupName"
                            value={groupName}
                            isDisabled={isDisabledGroupName}
                            onChange={onChangeGroupName}
                            placeholder="Group Name"
                        />
                    </div>
                    {isClientCTFetching && (
                        <Loader/>
                    )}
                    {!isClientCTFetching && isNotEmpty(clientCTMembers) && (
                        <div className="CTMemberGroupConversationParticipantPicker-Section">
                            <div className="CTMemberGroupConversationParticipantPicker-SectionTitle">
                                Client Care Team
                            </div>
                            <CTMemberList
                                data={mappedClientCTMembers}
                                onCheck={onToggleClientCTMember}
                                onCheckAll={onToggleAllClientCTMembers}
                            />
                        </div>
                    )}
                    {isCommunityCTFetching && (
                        <Loader/>
                    )}
                    {!isCommunityCTFetching && isNotEmpty(communityCTMembers) && (
                        <div className="CTMemberGroupConversationParticipantPicker-Section">
                            <div className="CTMemberGroupConversationParticipantPicker-SectionTitle">
                                Community Care Team
                            </div>
                            <CTMemberList
                                data={mappedCommunityCTMembers}
                                onCheck={onToggleCommunityCTMember}
                                onCheckAll={onToggleAllCommunityCTMembers}
                            />
                        </div>
                    )}
                </Scrollable>
            </Modal>

            {isConfirmOpen && (
                <CancelConfirmDialog
                    isOpen
                    title="The changes will not be saved"
                    onCancel={() => toggleConfirm(false)}
                    onConfirm={onClose}
                />
            )}
        </>
    )
}

CTMemberCommunicationParticipantPicker.propTypes = {
    communicationType: PTypes.oneOf(['video', 'conversation'])
}