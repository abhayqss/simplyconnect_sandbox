import React, { memo, useCallback, useEffect, useMemo, useRef, useState } from "react";

import cn from "classnames";
import { filesize } from "filesize";

import { map, noop } from "underscore";

import { useSelector } from "react-redux";

import { useDebounce } from "use-debounce";

import { useAuthUser, useBoundActions } from "hooks/common/redux";

import { useConversations, useConversationState, useParticipants } from "hooks/business/conversations";

import { ErrorViewer, FlatList, Loader } from "components";

import { Message as MessageEntity } from "factories";

import { SuccessDialog, WarningDialog } from "components/dialogs";

import { DateSeparator, Message, ModalActionPicker } from "components/communication/messenger";

import { authSessionActions, conversationsActions } from "redux/index";

import NoteEditor from "containers/Events/NoteEditor/NoteEditor";

import { AbortionController, DateUtils as DU, getLastNodeIndexInViewPort, measure } from "lib/utils/Utils";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, DOCU_TRACK } from "lib/Constants";

import { ReactComponent as ArrowTop } from "images/arrowtop.svg";

import { ScrollContext, ScrollToLastMessageStrategy, ScrollToMessageStrategy } from "./strategies";

import { MessageInput, SendToDocuTrackEditor } from "../";

import ConversationHeader from "./components/ConversationHeader";

import "./Conversation.scss";

const { PNG, PDF, DOC, DOCX, TIFF, PJPG, JPEG, JPG } = ALLOWED_FILE_FORMATS;

const HISTORY_PADDING_BOTTOM = 40;

const CONVERSATION_VIEWPORT_TOP = {
  DESKTOP: 186,
  TABLET: 141,
  MOBILE: 56,
};

const { MAX_FILE_SIZE_MB } = DOCU_TRACK;

function getFileSizeMB(value) {
  return filesize(value, { output: "object", exponent: 2 }).value;
}

function isAllowedDocuTrackMimeType(type) {
  const allowedMimeTypes = map(
    [PNG, PDF, DOC, DOCX, PJPG, JPEG, JPG],
    (format) => ALLOWED_FILE_FORMAT_MIME_TYPES[format],
  );
  return allowedMimeTypes.includes(type);
}

function getViewportTop() {
  if (window.innerHeight > 1024) {
    return CONVERSATION_VIEWPORT_TOP.DESKTOP;
  }

  if (window.innerHeight > 667) {
    return CONVERSATION_VIEWPORT_TOP.TABLET;
  }

  return CONVERSATION_VIEWPORT_TOP.MOBILE;
}

const messagesAbortController = new AbortionController();

function Conversation({ sid, onClose, className, isLoading }) {
  const { state, actions } = useConversationState();

  const flatListRef = useRef();
  const selectedMessageRef = useRef();

  const [isReplying, setReplying] = useState(false);
  const [selectedMessage, setSelectedMessage] = useState(null);

  const [isNoteEditorOpen, toggleNoteEditor] = useState(false);
  const [isScrollButtonShown, setIsScrollButtonShown] = useState(false);
  const [isEditMessageDialogOpen, toggleEditMessageDialog] = useState(false);
  const [isNoteSaveSuccessDialogOpen, toggleNoteSaveSuccessDialog] = useState(false);
  const [isSendToDocuTrackEditorOpen, toggleSendToDocuTrackEditor] = useState(false);
  const [isDocuTrackFileFormatWarningOpen, toggleDocuTrackFileFormatWarning] = useState(false);
  const [isDocuTrackMaxFileSizeWarningOpen, toggleDocuTrackMaxFileSizeWarning] = useState(false);

  const { error, messages, isFetching, data: conversation } = state;
  const authUser = useAuthUser();

  const users = useSelector((state) => state.conversations.users.data);
  const connectionStatus = useSelector((state) => state.conversations.connectionStatus);
  const unsentMessages = useSelector((state) => state.conversations.unsentMessages.get(sid));

  const { isDocuTrackEnabled } = useSelector((state) => state.auth.login.user.data) ?? {};

  const allMessages = useMemo(() => {
    let all = messages.data;

    unsentMessages?.forEach((a) => {
      const i = all.findIndex((b) => DU.gt(b.dateCreated, a.dateCreated));

      all = all.insert(i >= 0 ? i : all.size, a);
    }) || noop();

    return all;
  }, [messages, unsentMessages]);

  const pulseAuthSession = useBoundActions(authSessionActions.pulse);

  const removeUnsentMessage = useBoundActions(conversationsActions.removeUnsentMessage);

  const { on, off, emit, sendMessage, getMessagesCount, setAllMessagesRead, updateLastReadMessageIndex } =
    useConversations();

  const participants = useParticipants(conversation);

  const shouldRenderMessages = messages.data.first()?.conversationSid === sid;

  const isGroup = participants.length > 2;

  const canSendMessages = isGroup || participants?.every((o) => o.isActive);

  const fetchMessages = useCallback(
    (params) => {
      pulseAuthSession();
      return actions.messages.fetch(conversation, params, {
        signal: messagesAbortController.signal,
      });
    },
    [conversation, actions.messages, pulseAuthSession],
  );

  const clearMessages = useCallback(() => {
    actions.messages.clear();
  }, [actions]);

  const scrollContext = useMemo(
    () =>
      new ScrollContext({
        fetchMessages,
        user: authUser,
        participants,
        conversation,
        ref: flatListRef,
        messages: messages.data,
      }),
    [authUser, messages, conversation, participants, fetchMessages],
  );

  const onMessageAdded = useCallback(
    (data) => {
      let message = MessageEntity(data);

      if (sid === message.conversationSid) {
        actions.messages.add(data);

        scrollContext.executeStrategy(new ScrollToLastMessageStrategy(message));
      }
    },
    [actions.messages, sid, scrollContext],
  );

  const onMessageUpdated = useCallback(
    (event) => {
      let message = MessageEntity(event.message);
      let { lastReadMessageIndex } = conversation;

      actions.messages.update(event.message);

      if (message.index === lastReadMessageIndex) {
        emit("conversationUpdated", {
          updateReasons: ["lastMessageUpdated"],
          conversation: conversation.getProvider(),
        });

        scrollContext.executeStrategy(new ScrollToLastMessageStrategy(message));
      }
    },
    [scrollContext, conversation, actions.messages, emit],
  );

  const onSubmitMessage = useCallback(() => {
    setSelectedMessage(null);
    setReplying(false);
    pulseAuthSession();
  }, [pulseAuthSession]);

  const onCloseNoteEditor = useCallback(() => {
    toggleNoteEditor(false);
  }, []);

  const onNoteSaveSuccess = useCallback(() => {
    toggleNoteEditor(false);
  }, []);

  const onCreateNote = useCallback(() => {
    toggleNoteEditor(true);
  }, []);

  const onCancelMessageEditing = useCallback(() => {
    setSelectedMessage(null);
  }, []);

  const fetchMoreMessages = useCallback(() => {
    getMessagesCount(conversation).then((count) => {
      if (messages.data.size < count) {
        fetchMessages({ from: messages.data.first().index - 1 });
      }
    });
  }, [conversation, messages, fetchMessages, getMessagesCount]);

  function onScrollHistory({ nodes, scrollTop, scrollHeight, clientHeight }) {
    let scrollBottom = scrollHeight - scrollTop - clientHeight;

    const node = flatListRef.current.getNode();
    const viewPortBottom = measure(node).bottom - HISTORY_PADDING_BOTTOM;

    let indexInList = getLastNodeIndexInViewPort(nodes, viewPortBottom);
    let index = messages.data.get(indexInList - (unsentMessages?.size || 0))?.index;

    let lastReadMessageIndex = conversation.lastReadMessageIndex;

    if (index > lastReadMessageIndex) {
      updateLastReadMessageIndex(conversation, index);
    }

    setIsScrollButtonShown(scrollBottom > clientHeight);
  }

  const onScrollToQuotedMessage = useCallback(
    (message) => {
      const { messageSid } = message.attributes.quote;

      scrollContext.executeStrategy(new ScrollToMessageStrategy(messageSid));
    },
    [scrollContext],
  );

  const onSendMessage = useCallback((message) => {
    if (!isAllowedDocuTrackMimeType(message.media.type)) {
      toggleDocuTrackFileFormatWarning(true);
    } else if (getFileSizeMB(message.media.size) > MAX_FILE_SIZE_MB) {
      toggleDocuTrackMaxFileSizeWarning(true);
    } else {
      setSelectedMessage(message);
      toggleSendToDocuTrackEditor(true);
    }
  }, []);

  const onReply = useCallback((message) => {
    setReplying(true);
    setSelectedMessage(message);
  }, []);

  const onCancelReplying = useCallback(() => {
    setReplying(false);
    setSelectedMessage(null);
  }, []);

  const onCloseSendToDocuTrackEditor = useCallback(() => {
    setSelectedMessage(null);
    toggleSendToDocuTrackEditor(false);
  }, []);

  const onResendMessage = useCallback(
    (message) => {
      if (connectionStatus === "connected") {
        removeUnsentMessage(removeUnsentMessage.sid, message.index);
        sendMessage(conversation, message.text || message.media);
      }
    },
    [conversation, sendMessage, connectionStatus, removeUnsentMessage],
  );

  function showEditMessageDialog(message) {
    if (window.innerWidth > 1024 || conversation?.attributes?.disconnected) return;

    toggleEditMessageDialog(true);
    selectedMessageRef.current = message;
  }

  function closeEditMessageDialog() {
    selectedMessageRef.current = null;
    toggleEditMessageDialog(false);
  }

  function editMessage() {
    setSelectedMessage(selectedMessageRef.current);
    closeEditMessageDialog();
  }

  useEffect(() => {
    if (sid) {
      actions.fetch(sid);
    }
  }, [sid, actions]);

  useEffect(() => {
    if (conversation) {
      fetchMessages();

      return () => {
        clearMessages();
        messagesAbortController.abort();
      };
    }
  }, [conversation, fetchMessages, clearMessages]);

  useEffect(() => {
    if (sid) {
      setSelectedMessage(null);
    }
  }, [sid]);

  useEffect(() => {
    const node = flatListRef.current?.getNode();

    if (node) {
      const nodes = flatListRef.current.getItemNodes();
      const viewPortBottom = measure(node).bottom - HISTORY_PADDING_BOTTOM;

      let indexInList = getLastNodeIndexInViewPort(nodes, viewPortBottom);

      let indexOfLast = messages.data.last()?.index;
      let indexOfVisible = messages.data.get(indexInList)?.index;

      if (indexOfLast === indexOfVisible && indexOfLast !== conversation.lastReadMessageIndex) {
        updateLastReadMessageIndex(conversation, indexOfVisible);
      }
    }
  }, [messages, conversation, updateLastReadMessageIndex]);

  useEffect(() => {
    const message = unsentMessages?.last();

    if (message && !message.sid) {
      scrollContext.executeStrategy(new ScrollToLastMessageStrategy(message));
    }
  }, [unsentMessages, scrollContext]);

  useEffect(() => {
    if (conversation) {
      on("messageAdded", onMessageAdded);

      return () => off("messageAdded", onMessageAdded);
    }
  }, [conversation, on, off, onMessageAdded]);

  useEffect(() => {
    if (!messages.isFetching && messages.data.size) {
      on("messageUpdated", onMessageUpdated);

      return () => off("messageUpdated", onMessageUpdated);
    }
  }, [on, off, onMessageUpdated, messages.isFetching, messages.data.size]);

  useEffect(() => {
    if (conversation) {
      setAllMessagesRead(conversation);
    }
  }, [conversation, setAllMessagesRead]);

  const [shouldShowLoader] = useDebounce(
    isLoading || isFetching || (messages.isFetching && messages.data.isEmpty()),
    100,
  );

  if (shouldShowLoader) {
    return (
      <div className={cn("Conversation", className)}>
        <Loader />
      </div>
    );
  }

  return (
    <>
      <div className={cn("Conversation", className)}>
        <ConversationHeader onClose={onClose} conversation={conversation} />

        {messages.isFetching && !messages.data.isEmpty() && <Loader className="Conversation-MessagesLoader" />}

        {!shouldRenderMessages && <div className="Conversation-History"></div>}

        {shouldRenderMessages && (
          <FlatList
            isReversed
            shouldAutoScroll
            ref={flatListRef}
            list={allMessages}
            itemKey="sid"
            className="Conversation-History"
            itemClassName="Conversation-HistoryItem"
            onScroll={onScrollHistory}
            loadMore={fetchMoreMessages}
            shouldLoadMore={!messages.isFetching}
            onEndReachedThreshold={100}
            viewPortTop={getViewportTop()}
          >
            {(message, i) => {
              const participant = users.get(message.author);

              if (!participant && !message.isSystemMessage) return null;

              const isLastMessage = message === allMessages.last();
              const isFirstMessage = message.index === 0;
              const nextMessage = !isLastMessage ? allMessages.get(i + 1) : null;

              const shouldAddDateSeparator = nextMessage && DU.lt(message.dateCreated, nextMessage.dateCreated, "day");

              const isCurrentUser = participant?.employeeId === authUser.id;

              return (
                <>
                  {isFirstMessage && (
                    <DateSeparator
                      date={message.dateCreated}
                      className="Conversation-DateSeparator Conversation-DateSeparator_first"
                    />
                  )}

                  <Message
                    data={message}
                    author={participant}
                    onResend={onResendMessage}
                    canSend={isDocuTrackEnabled && message.media}
                    onTextClick={(message) => (isCurrentUser ? showEditMessageDialog(message) : noop())}
                    onEdit={() => setSelectedMessage(message)}
                    onSend={() => onSendMessage(message)}
                    onReply={onReply}
                    onClickQuote={() => onScrollToQuotedMessage(message)}
                    isCurrentUser={isCurrentUser}
                    isDisconnected={conversation?.attributes?.disconnected}
                    className={cn(
                      "Conversation-Message",
                      { "Conversation-Message_disabled": !canSendMessages },
                      { "Conversation-Message_align_center": message.isSystemMessage },
                      { "Conversation-Message_align_right": isCurrentUser },
                    )}
                  />

                  {shouldAddDateSeparator && (
                    <DateSeparator date={nextMessage.dateCreated} className="Conversation-DateSeparator" />
                  )}
                </>
              );
            }}
          </FlatList>
        )}

        {isScrollButtonShown && (
          <ArrowTop
            onClick={() => flatListRef.current?.scrollToBottom({ behavior: "smooth" })}
            className="Conversation-ScrollBottomBtn ScrollTopBtn-Icon"
          />
        )}

        <MessageInput
          isReplying={isReplying}
          message={selectedMessage}
          onNote={onCreateNote}
          onSubmit={onSubmitMessage}
          conversation={conversation}
          onCancel={onCancelMessageEditing}
          className="Conversation-MessageInput"
          isDisabled={!canSendMessages}
          isDisconnected={conversation?.attributes?.disconnected}
          onCancelReplying={onCancelReplying}
        />
      </div>

      {error && <ErrorViewer isOpen error={error} onClose={actions.clearError} />}

      {isNoteEditorOpen && (
        <NoteEditor
          isOpen
          clientId={null}
          clientName={""}
          onClose={onCloseNoteEditor}
          onSaveSuccess={onNoteSaveSuccess}
        />
      )}

      {isEditMessageDialogOpen && (
        <ModalActionPicker
          options={[
            {
              title: "EDIT",
              onClick: editMessage,
            },
          ]}
          onClose={closeEditMessageDialog}
          className="Conversation-ModalActionPicker"
        />
      )}

      {isNoteSaveSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title="The note has been created."
          buttons={[
            {
              text: "Close",
              outline: true,
              onClick: () => toggleNoteSaveSuccessDialog(false),
            },
          ]}
        />
      )}

      <SendToDocuTrackEditor
        media={selectedMessage?.media}
        isOpen={isSendToDocuTrackEditorOpen}
        onSendSuccessConfirmed={onCloseSendToDocuTrackEditor}
        onClose={onCloseSendToDocuTrackEditor}
      />

      {isDocuTrackMaxFileSizeWarningOpen && (
        <WarningDialog
          isOpen
          title={`The file size exceeds the limit allowed.\n DocuTrack system allows a maximum file size of 10 MB.`}
          buttons={[
            {
              text: "CLOSE",
              onClick: () => toggleDocuTrackMaxFileSizeWarning(false),
            },
          ]}
        />
      )}

      {isDocuTrackFileFormatWarningOpen && (
        <WarningDialog
          isOpen
          title={`The file format should be PDF, Word, PNG or JPEG`}
          buttons={[
            {
              text: "CLOSE",
              onClick: () => toggleDocuTrackFileFormatWarning(false),
            },
          ]}
        />
      )}
    </>
  );
}

export default memo(Conversation);
