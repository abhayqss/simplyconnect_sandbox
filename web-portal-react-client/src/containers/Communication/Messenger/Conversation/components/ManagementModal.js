import React from 'react'

import cn from 'classnames'

import {
    Modal,
    Button,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { Avatar } from 'components/communication'

import { useAuthUser } from 'hooks/common'

import { getInitials } from 'lib/utils/Utils'

import { ReactComponent as CloseButton } from 'images/close.svg'

function ConversationManagementModal({
    onLeave,
    onClose,
    className,
    participants,
    canLeave = true,
    isDisconnected,
    onDeleteParticipant,
    canDeleteParticipants,
}) {
    const isMobile = window.innerWidth <= 667

    const authUser = useAuthUser()

    return (
        <Modal
            isOpen
            backdrop
            toggle={onClose}
            centered={!isMobile}
            modalClassName="ConversationManagementModal-Container"
            modalTransition={{
                baseClass: 'fade ConversationManagementModal_fade',
                baseClassActive: 'show ConversationManagementModal_show',

            }}
            className={cn('ConversationManagementModal', className)}
        >
            <CloseButton
                onClick={onClose}
                className="ConversationManagementModal-CloseButton"
            />

            <div className="ConversationManagementModal-Participants">
                {participants.map((o) => (
                    <div
                        key={o.identity}
                        className={cn(
                            'ConversationManagementModal-Participant',
                            { 'ConversationManagementModal-Participant_disabled': !o.isActive }
                        )}
                    >
                        <Avatar
                            id={o.avatarId}
                            className="ConversationManagementModal-ParticipantAvatar margin-right-10"
                        >
                            {getInitials(o)}
                        </Avatar>

                        <div className="ConversationManagementModal-ParticipantName">
                            <div className="padding-right-30 text-nowrap">
                                {o.firstName} {o.lastName}
                            </div>

                            {canDeleteParticipants && authUser.id !== o?.employeeId && (
                                <>
                                    <div
                                        id={`remove-participant-${o.identity}-mobile`}
                                        onClick={() => !isDisconnected && onDeleteParticipant(o)}
                                        className={cn(
                                            "ConversationManagementModal-DeleteParticipantAction",
                                            { "ConversationManagementModal-DeleteParticipantAction__disconnected": isDisconnected }
                                        )}
                                    >
                                        Delete
                                    </div>
                                    {isDisconnected && (
                                        <Tooltip
                                            target={`remove-participant-${o.identity}-mobile`}
                                            modifiers={[
                                                {
                                                    name: 'offset',
                                                    options: { offset: [0, 6] }
                                                },
                                                {
                                                    name: 'preventOverflow',
                                                    options: { boundary: document.body }
                                                }
                                            ]}
                                        >
                                            Access to client data is not available per client request
                                        </Tooltip>
                                    )}
                                </>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            {canLeave && (
                <div className="ConversationManagementModal-Footer">
                    <Button
                        id="leave-chat-mobile"
                        color="success"
                        onClick={onLeave}
                        className="ConversationManagementModal-LeaveAction"
                        disabled={isDisconnected}
                    >
                        Leave chat
                    </Button>
                    {isDisconnected && (
                        <Tooltip
                            target="leave-chat"
                            modifiers={[
                                {
                                    name: 'offset',
                                    options: { offset: [0, 6] }
                                },
                                {
                                    name: 'preventOverflow',
                                    options: { boundary: document.body }
                                }
                            ]}
                        >
                            Access to client data is not available per client request
                        </Tooltip>
                    )}
                </div>
            )}
        </Modal>
    )
}

export default ConversationManagementModal