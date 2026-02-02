import React, { useCallback, useEffect } from "react";

import { useSelector } from "react-redux";

import {
  useConversations,
  useConversationUpdates,
  useCurrentCall,
  useOutgoingCall,
  useVideo,
  useVideoState,
} from "hooks/business/conversations";

import { ErrorViewer, Loader } from "components";

import { Conversation } from "factories";

import conversationsActions from "redux/conversations/conversationsActions";

import { useBoundActions } from "hooks/common/redux";

import { IncomingCall, OutgoingCall, Room } from "./";

import { ifElse } from "lib/utils/Utils";

const selectIsReady = (state) => state.conversations.isReady;
const selectCurrentUser = (state) => state.conversations.currentUser;
const selectLiveConversationSids = (state) => state.conversations.liveConversationSids;

let declineTimeoutId = null;

function VideoChat() {
  const { room, error, isOnCall, currentCall, isConnecting, isIncomingCall, isOutgoingCall, incidentReport } =
    useVideoState();

  const { emit } = useConversations();

  const isReady = useSelector(selectIsReady);
  const currentUser = useSelector(selectCurrentUser);
  const liveConversationSids = useSelector(selectLiveConversationSids);

  const reduxActions = useBoundActions(conversationsActions);

  const isGroup = currentCall?.callees.size > 1;

  const {
    joinCall,
    setOnCall,
    disconnect,
    clearError,
    declineCall,
    receiveCall,
    initiateCall,
    cancelCall,
    closeConnection,
    setOutgoingCall,
    setIncomingCall,
    callWasDeclined,
    updateCurrentCall,
    addOnCallParticipants,
    addDeclinedParticipant,
    addTimeoutParticipants,
    addPendingParticipants,
    acceptCall,
    dismissCallInitiation,
  } = useVideo();

  const shouldShowRoom = room && currentCall && isOnCall;

  const decline = useCallback(() => {
    setIncomingCall(null);
    declineCall({ roomSid: currentCall.name });
  }, [declineCall, currentCall, setIncomingCall]);

  const endConnection = useCallback(() => {
    disconnect();
    closeConnection();
  }, [closeConnection, disconnect]);

  const cancel = useCallback(async () => {
    disconnect();
    cancelCall();
    clearTimeout(declineTimeoutId);
    console.log("cancel", "..cancel");
  }, [cancelCall, disconnect]);

  const closeOutgoingCall = useCallback(() => {
    endConnection();
    setOutgoingCall(false);
    clearTimeout(declineTimeoutId);
  }, [endConnection, setOutgoingCall]);

  const onCallWasAccepted = useCallback(() => {
    setOnCall(true);
    setOutgoingCall(false);
  }, [setOnCall, setOutgoingCall]);

  const onCallDeclined = useCallback(
    ifElse(
      ({ identity }) => identity === currentUser.identity,
      endConnection,
      ({ identity }) => addDeclinedParticipant(identity),
    ),
    [currentUser, endConnection, addDeclinedParticipant],
  );

  const onTimeout = useCallback(
    ifElse(
      ({ identities }) => identities.includes(currentUser.identity),
      endConnection,
      ({ identities }) => addTimeoutParticipants(identities),
    ),
    [currentUser, endConnection, addTimeoutParticipants],
  );

  const popLiveConversation = useCallback(
    (conversationSid) => {
      reduxActions.updateLiveConversations(liveConversationSids.filter((sid) => sid !== conversationSid));
    },
    [reduxActions, liveConversationSids],
  );

  const accept = () => acceptCall();
  const acceptWithVideo = () => acceptCall(true);

  useCurrentCall({
    onJoinCall: joinCall,
    onMemberJoined: useCallback(
      ({ onCall }) => {
        addOnCallParticipants(onCall);
      },
      [addOnCallParticipants],
    ),
    onCallReceived: useCallback(
      (data) => {
        receiveCall(data);
        clearTimeout(declineTimeoutId);
      },
      [receiveCall],
    ),
    onTimeout: useCallback(
      (data) => {
        if (currentCall) {
          onTimeout(data);
        }
      },
      [currentCall, onTimeout],
    ),
    onCallDeclined: useCallback(
      (data) => {
        if (currentCall) {
          onCallDeclined(data);
        }
      },
      [currentCall, onCallDeclined],
    ),
    onPendingMembers: useCallback(
      ({ callees }) => {
        if (currentCall) {
          addPendingParticipants(callees);
        }
      },
      [currentCall, addPendingParticipants],
    ),
    onCallEnded: useCallback(
      (roomSid) => {
        const isIncomingCallEnded = !room && currentCall;
        const isCurrentCallEnded = room?.name === roomSid && currentCall && !currentCall.isDeclined;

        if (isIncomingCallEnded || isCurrentCallEnded) {
          endConnection();
          popLiveConversation(currentCall.conversationSid);
        }
      },
      [room, endConnection, popLiveConversation, currentCall],
    ),
    onConversationUpdated: useCallback(
      ({ roomSid, conversationSid }) => {
        if (currentCall?.name === roomSid) {
          updateCurrentCall({ conversationSid });

          !isGroup && emit("conversationTurnedIntoGroup", conversationSid);
        }
      },
      [emit, isGroup, currentCall, updateCurrentCall],
    ),
  });

  useOutgoingCall({
    onOutgoingCall: initiateCall,
  });

  useConversationUpdates(
    ({ conversation: cv }) => {
      if (currentCall) {
        const conversation = Conversation(cv);

        if (conversation.sid === currentCall.conversationSid) {
          updateCurrentCall({ friendlyName: conversation.friendlyName });
        }
      }
    },
    [currentCall, updateCurrentCall],
  );

  useEffect(() => {
    if (currentCall?.callees.size) {
      const allIsTimeout = currentCall.callees.size - currentCall.timeoutIdentities.size < 1;

      if (allIsTimeout) {
        endConnection();
      }
    }
  }, [currentCall, endConnection]);

  useEffect(() => {
    if (currentCall?.callees.size) {
      const allIsDeclined = currentCall.callees.size - currentCall.declinedIdentities.size < 1;

      if (allIsDeclined) {
        callWasDeclined();
      }
    }
  }, [currentCall, callWasDeclined]);

  useEffect(() => {
    if (currentCall?.isDeclined) {
      declineTimeoutId = setTimeout(closeOutgoingCall, 5000);
    }
  }, [currentCall, closeOutgoingCall]);

  useEffect(
    function safeLogoutDuringCall() {
      if (isReady && room) {
        return disconnect;
      }
    },
    [isReady, disconnect, room],
  );

  useEffect(() => {
    return () => {
      dismissCallInitiation();
    };
  }, [dismissCallInitiation]);

  useEffect(() => {
    window.onbeforeunload = (event) => {
      event.preventDefault();
      disconnect();
    };
  }, [disconnect]);

  return (
    <>
      {isConnecting && <Loader hasBackdrop />}

      {isIncomingCall && (
        <IncomingCall call={currentCall} onAccept={accept} onAcceptWithVideo={acceptWithVideo} onDecline={decline} />
      )}

      {isOutgoingCall && (
        <OutgoingCall
          room={room}
          call={currentCall}
          onCancel={cancel}
          onClose={closeOutgoingCall}
          onConnected={onCallWasAccepted}
        />
      )}

      {shouldShowRoom && (
        <Room room={room} call={currentCall} onClose={endConnection} incidentReport={incidentReport} />
      )}

      {error && <ErrorViewer isOpen error={error} onClose={clearError} />}
    </>
  );
}

export default VideoChat;
