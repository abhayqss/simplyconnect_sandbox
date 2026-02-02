import React, { useRef, useMemo, useState, useEffect, useCallback } from "react";

import cn from "classnames";

import { any, map, uniq, values, isEmpty, compact, compose, partial, flatten } from "underscore";

import { useDebounce } from "use-debounce";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { useHistory } from "react-router-dom";

import { useScrollable, useLocationState, useMutationWatch } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import {
  useConversations,
  useConversationsState,
  useConversationsContext,
  useParticipatingAccessibilityQuery,
} from "hooks/business/conversations";

import { Loader, SearchField, ErrorViewer, OutsideClickListener } from "components";

import { IconButton } from "components/buttons";

import { ActionPicker, ModalActionPicker } from "components/communication/messenger";

import NoMessagesFallback from "./components/NoMessagesFallback";
import ConversationFallback from "./components/ConversationFallback";

import { authSessionActions, conversationsActions } from "redux/index";

import { ReactComponent as Edit } from "images/edit-1.svg";

import { Message, Conversation as ConversationEntity } from "factories";

import { lc, defer } from "lib/utils/Utils";

import { NO_CONNECTION_ERROR_TEXT, NO_CONNECTION_ERROR_TITLE } from "./Constants";

import { FAILED_SEARCH_REASONS } from "strategies/conversations";

import Conversation from "../Conversation/Conversation";
import ConversationInfoBox from "../ConversationInfoBox/ConversationInfoBox";
import ConnectionErrorViewer from "../ConnectionErrorViewer/ConnectionErrorViewer";

import ConversationParticipantPicker from "../ConversationParticipantPicker/ConversationParticipantPicker";
import GroupConversationParticipantPicker from "../GroupConversationParticipantPicker/GroupConversationParticipantPicker";

import "./Conversations.scss";

const { DOES_NOT_EXIST } = FAILED_SEARCH_REASONS;

const LIST_ITEM_HEIGHT = 80;

function getParticipantNames(participants) {
  return participants.reduce((result, value) => {
    return `${result} ${value.firstName} ${value.lastName}`;
  }, "");
}

function mapStateToProps(state) {
  const { users, isReady, lastMessages, connectionStatus, sidOfLastSelected, liveConversationSids } =
    state.conversations;

  return {
    users,
    isReady,
    lastMessages,
    connectionStatus,
    sidOfLastSelected,
    liveConversationSids,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    reduxActions: {
      ...bindActionCreators(conversationsActions, dispatch),
      auth: {
        session: bindActionCreators(authSessionActions, dispatch),
      },
    },
  };
}

function Conversations({
  users,
  isReady = true,
  lastMessages,
  connectionStatus,
  sidOfLastSelected,
  liveConversationSids,
  reduxActions,
  title = "Chats",
  className,
}) {
  const isMobileView = window.innerWidth <= 667;

  const [searchText, setSearchText] = useState("");
  const [debouncedSearchText] = useDebounce(searchText, 200);

  const [selectedSid, setSelectedSid] = useState(null);
  const [isActionPickerOpen, toggleActionPicker] = useState(false);
  const [isParticipantPickerOpen, toggleParticipantPicker] = useState(false);
  const [isClosingConversation, setIsClosingConversation] = useState(false);
  const [updatingConversationSid, setUpdatingConversationSid] = useState(false);
  const [isModalActionPickerOpen, toggleModalActionPickerOpen] = useState(false);
  const [isMultipleParticipantPickerOpen, toggleMultipleParticipantPicker] = useState(false);

  const lastMessage = useRef();

  const authUser = useAuthUser();
  const currentUser = useMemo(
    () => users.data.find((user) => user.employeeId === authUser.id),
    [users.data, authUser.id],
  );

  const history = useHistory();
  const [, clearLocationState] = useLocationState({ isCached: false });

  const { state, actions, selectors } = useConversationsState();

  const { scroll, Scrollable } = useScrollable();

  const { fetch, clearError } = actions;
  const { sortByUpdated, sortByLive, filterUserDataOut } = selectors;
  const { data, error, selected, isFetching, wasFetched } = state;
  const selectConversations = compose(
    partial(sortByLive, liveConversationSids),
    partial(filterUserDataOut, authUser),
    sortByUpdated,
  );

  const conversations = selectConversations(data);
  const { on, off, getUserByIdentity } = useConversations();

  const { searchDefaultConversation } = useConversationsContext({
    conversations,
    sidOfLastSelected,
    selectedConversation: selected,
  });

  const {
    data: oneToOneAccessibility = {},
    refetch: refreshOneToOneAccessibility,
    isFetching: isFetchingOneToOneAccessibility,
  } = useParticipatingAccessibilityQuery({
    excludeOneToOneParticipants: true,
  });

  const {
    data: groupAccessibility = {},
    refetch: refreshGroupAccessibility,
    isFetching: isFetchingGroupAccessibility,
  } = useParticipatingAccessibilityQuery();

  const canCreateOneToOne = any(values(oneToOneAccessibility), (v) => v);

  const canCreateGroup = any(values(groupAccessibility), (v) => v);

  const canCreateAny = canCreateOneToOne || canCreateGroup;

  const isConversationReady = selected && !users.data.isEmpty() && !users.isFetching;

  const scrollTo = useCallback(
    (cv) => {
      const index = conversations.findIndex((o) => o.sid === cv.sid);

      scroll(index * LIST_ITEM_HEIGHT, 0);
    },
    [scroll, conversations],
  );

  const filterCriteria = useCallback(
    (cv) => {
      let ids = cv.participantIdentities;

      let participants = users.data.filter((u) => ids.includes(u.identity));

      let names = getParticipantNames(participants);

      const lastMessage = lastMessages.get(cv.sid);
      const text = debouncedSearchText.toLowerCase();

      return (
        lc(names).includes(text) ||
        lc(cv.friendlyName || "").includes(text) ||
        lc(lastMessage?.text || "").includes(text)
      );
    },
    [users.data, lastMessages, debouncedSearchText],
  );

  const filteredConversations = useMemo(() => conversations.filter(filterCriteria), [conversations, filterCriteria]);

  const personalConversations = selectors.onlyPersonal(data);

  const personalConversationUserIdentities = useMemo(
    () => uniq(flatten(personalConversations.map((cv) => cv.participantIdentities).toJS())),
    [personalConversations],
  );

  const personalConversationUserIds = useMemo(
    () =>
      compact(
        users.data.map((o) => (personalConversationUserIdentities.includes(o.identity) ? o.employeeId : null)).toJS(),
      ),
    [users, personalConversationUserIdentities],
  );

  const onChangeSearchText = (_, text) => {
    setSearchText(text);
  };

  const onClearSearchText = () => {
    setSearchText("");
  };

  const onActionBtnClick = () => {
    if (canCreateAny) {
      let openPicker = isMobileView ? toggleModalActionPickerOpen : toggleActionPicker;

      openPicker(true);
    }
  };

  const pulseAuthSession = useCallback(() => {
    reduxActions.auth.session.pulse();
  }, [reduxActions]);

  const fetchUsers = useCallback(
    (params) => {
      reduxActions.loadUsers(params);
    },
    [reduxActions],
  );

  const create = useCallback(
    async (params) => {
      let sid = await actions.create(params);

      fetchUsers({ conversationSids: [sid] });

      return sid;
    },
    [actions, fetchUsers],
  );

  const setUserOnline = useCallback(
    (identity, isOnline) => {
      reduxActions.setUserOnline(identity, isOnline);
    },
    [reduxActions],
  );

  const closeActionPicker = useCallback(() => {
    toggleActionPicker(false);
  }, []);

  const closeModalActionPicker = useCallback(() => {
    toggleModalActionPickerOpen(false);
  }, []);

  const onClickNewChat = () => {
    closeActionPicker();
    closeModalActionPicker();
    toggleParticipantPicker(true);
  };

  const onClickNewGroupChat = () => {
    closeActionPicker();
    closeModalActionPicker();
    toggleMultipleParticipantPicker(true);
  };

  const actionOptions = compact([
    canCreateOneToOne && { title: "New chat", onClick: onClickNewChat },
    canCreateGroup && { title: "New group chat", onClick: onClickNewGroupChat },
  ]);

  const onCloseParticipantPicker = useCallback(() => {
    toggleParticipantPicker(false);
  }, []);

  const onCloseMultipleParticipantPicker = useCallback(() => {
    toggleMultipleParticipantPicker(false);
  }, []);

  const onCompleteParticipantPicker = useCallback(
    async (value) => {
      await create({
        participatingClientId: value.clientId,
        employeeIds: [authUser.id, value.contactId],
      });

      actions.select(null);
    },
    [actions, create, authUser],
  );

  const onCompleteParticipantGroupPicker = useCallback(
    async (value) => {
      const { clientId, contactIds, groupName } = value;

      const employeeIds = [authUser.id, ...contactIds];

      await create({
        employeeIds,
        friendlyName: groupName,
        participatingClientId: clientId,
      });

      actions.select(null);
    },
    [actions, create, authUser],
  );

  const onAdded = useCallback(
    async (conversation) => {
      let cv = ConversationEntity(conversation);

      await fetchUsers({ conversationSids: [cv.sid] });

      actions.add(cv);
    },
    [actions, fetchUsers],
  );

  const onMessageAdded = useCallback((data) => {
    let message = Message(data);

    lastMessage.current = message;
  }, []);

  const onUpdated = useCallback(
    ({ conversation, updateReasons }) => {
      let message = lastMessage.current;

      lastMessage.current = null;

      let shouldUpdate =
        updateReasons.includes("lastReadMessageIndex") ||
        updateReasons.includes("lastMessageUpdated") ||
        (updateReasons.includes("lastMessage") && message && message.author !== currentUser.identity);

      if (shouldUpdate) {
        actions.update(conversation);
      }
    },
    [actions, currentUser],
  );

  const onUserUpdated = useCallback(
    ({ user, updateReasons }) => {
      if (updateReasons.includes("reachabilityOnline")) {
        setUserOnline(user.identity, user.isOnline);
      }
    },
    [setUserOnline],
  );

  const onLeft = useCallback(
    async (conversation) => {
      let cv = ConversationEntity(conversation);

      actions.remove(cv);
    },
    [actions],
  );

  const onSelectBySid = useCallback(
    (sid) => {
      let cv = conversations.find((cv) => cv.sid === sid);

      actions.select(cv);
      scrollTo(cv);
    },
    [actions, scrollTo, conversations],
  );

  const onCloseConversation = async () => {
    setIsClosingConversation(true);

    if (isMobileView) {
      cleanUp();
    }

    await defer(300);

    actions.select(null);
    setIsClosingConversation(false);
  };

  const cleanUp = useCallback(() => {
    history.replace();
    clearLocationState();
  }, [clearLocationState, history]);

  useEffect(() => {
    pulseAuthSession();
  }, [pulseAuthSession]);

  useEffect(() => {
    if (isActionPickerOpen) {
      refreshGroupAccessibility();
      refreshOneToOneAccessibility();
    }
  }, [isActionPickerOpen, refreshGroupAccessibility, refreshOneToOneAccessibility]);

  useEffect(() => {
    function onFetchSuccess(conversations) {
      !isEmpty(conversations) &&
        fetchUsers({
          conversationSids: conversations.map((cv) => cv.sid),
        });
    }

    fetch().then(onFetchSuccess);
  }, [fetch, fetchUsers]);

  useEffect(() => {
    if (wasFetched) {
      searchDefaultConversation()
        .then((cv) => {
          actions.select(cv);
          scrollTo(cv);
          cleanUp();
        })
        .catch(async ({ reason, payload }) => {
          if (reason === DOES_NOT_EXIST) {
            cleanUp();

            let sid = await create(payload);

            setSelectedSid(sid);
          }
        });
    }
  }, [actions, wasFetched, create, searchDefaultConversation, scrollTo, cleanUp]);

  useEffect(() => {
    if (selectedSid) {
      setSelectedSid(null);

      actions.select(conversations.find((cv) => cv.sid === selectedSid));
    }
  }, [selectedSid, conversations, actions]);

  useEffect(() => {
    reduxActions.setLastSelected(selected?.sid ?? null);
  }, [selected, reduxActions]);

  useEffect(() => {
    isReady &&
      users.data.forEach((o) => {
        getUserByIdentity(o.identity).then((user) => {
          setUserOnline(user.identity, user.isOnline);
        });
      });
  }, [isReady, users.data, setUserOnline, getUserByIdentity]);

  useMutationWatch(connectionStatus, (prevStatus) => {
    if (prevStatus === "connected") {
      actions.setError({
        message: {
          title: NO_CONNECTION_ERROR_TITLE,
          text: NO_CONNECTION_ERROR_TEXT,
        },
      });
    } else if (connectionStatus === "connected") {
      actions.clearError();
    }
  });

  useEffect(() => {
    on("userUpdated", onUserUpdated);
    return () => off("userUpdated", onUserUpdated);
  }, [on, off, onUserUpdated]);

  useEffect(() => {
    if (wasFetched) {
      on("conversationAdded", onAdded);

      return () => off("conversationAdded", onAdded);
    }
  }, [on, off, onAdded, wasFetched]);

  useEffect(() => {
    if (wasFetched) {
      on("conversationLeft", onLeft);

      return () => off("conversationLeft", onLeft);
    }
  }, [on, off, onLeft, wasFetched]);

  useEffect(() => {
    if (wasFetched) {
      on("messageAdded", onMessageAdded);

      return () => off("messageAdded", onMessageAdded);
    }
  }, [on, off, onMessageAdded, wasFetched]);

  useEffect(() => {
    if (wasFetched) {
      on("conversationUpdated", onUpdated);

      return () => off("conversationUpdated", onUpdated);
    }
  }, [on, off, onUpdated, wasFetched]);

  useEffect(() => {
    on("conversationLoading", setUpdatingConversationSid);

    return () => off("conversationLoading", setUpdatingConversationSid);
  }, [on, off]);

  useEffect(() => {
    on("conversationTurnedIntoGroup", onSelectBySid);

    return () => off("conversationTurnedIntoGroup", onSelectBySid);
  }, [on, off, onSelectBySid]);

  return (
    <>
      <div className="Conversations-Container h-100">
        <div className={cn("Conversations", className)}>
          <div className="Conversations-Header">
            <div className="Conversations-Title">{title}</div>

            <OutsideClickListener className="Conversations-Actions" onClick={closeActionPicker}>
              <IconButton
                size={24}
                Icon={Edit}
                shouldHighLight={false}
                onClick={onActionBtnClick}
                tipText={
                  canCreateAny
                    ? "Start chatting"
                    : `You don't have access to clients or contacts to start a conversation with`
                }
                disabled={
                  !canCreateAny || isFetchingGroupAccessibility || isFetchingOneToOneAccessibility || users.isFetching
                }
                name="conversations-action-btn"
                className="Conversations-ActionBtn"
              />

              {isActionPickerOpen && (
                <ActionPicker right={0} bottom={-50} options={actionOptions} className="Conversations-ActionPicker" />
              )}
            </OutsideClickListener>
          </div>

          {isFetching && <Loader />}

          {!(isFetching || conversations.isEmpty()) && (
            <>
              <div className="ConversationFilter margin-bottom-16">
                <SearchField
                  name="name"
                  className="ConversationFilter-Field"
                  value={searchText}
                  onChange={onChangeSearchText}
                  onClear={onClearSearchText}
                />
              </div>

              <Scrollable className="ConversationList" hasScrollTopBtn={false}>
                {conversations.map((cv) => {
                  return (
                    <ConversationInfoBox
                      key={cv.sid}
                      conversation={cv}
                      isLoading={isFetching || updatingConversationSid === cv.sid}
                      isSelected={selected?.sid === cv.sid}
                      highlightedText={debouncedSearchText}
                      isHighlighted={cv.sid === selected?.sid}
                      onClick={() => actions.select(cv)}
                      className={cn("margin-right-12", {
                        ConversationInfoBox_hidden: !filteredConversations.includes(cv),
                      })}
                    />
                  );
                })}
              </Scrollable>
            </>
          )}

          {!isFetching && conversations.isEmpty() && <NoMessagesFallback />}
        </div>

        {!isFetching && conversations.isEmpty() && (
          <ConversationFallback
            canCreateConversations={canCreateAny}
            onCreateConversation={() => toggleModalActionPickerOpen(true)}
          />
        )}

        {isConversationReady && (
          <Conversation
            sid={selected?.sid}
            isLoading={updatingConversationSid === selected?.sid}
            onClose={onCloseConversation}
            className={cn("Conversations-Conversation", { "Conversations-Conversation_close": isClosingConversation })}
          />
        )}
      </div>

      {isModalActionPickerOpen && (
        <ModalActionPicker
          options={actionOptions}
          onClose={() => toggleModalActionPickerOpen(false)}
          className="Conversations-MobileActionPicker"
        />
      )}

      <ConversationParticipantPicker
        isOpen={isParticipantPickerOpen}
        excludedContactIds={conversations.isEmpty() ? [authUser.id] : personalConversationUserIds}
        onClose={onCloseParticipantPicker}
        onComplete={onCompleteParticipantPicker}
      />
      <GroupConversationParticipantPicker
        isNewConversation
        excludedContactIds={[authUser.id]}
        isOpen={isMultipleParticipantPickerOpen}
        onClose={onCloseMultipleParticipantPicker}
        onComplete={onCompleteParticipantGroupPicker}
      />
      {error && <ErrorViewer isOpen error={error} onClose={clearError} />}
      <ConnectionErrorViewer />
    </>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(Conversations);
