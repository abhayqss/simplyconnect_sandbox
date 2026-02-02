import { noop } from "underscore";

import * as Sentry from "@sentry/react";
import { Client } from "@twilio/conversations";

import { promise } from "lib/utils/Utils";

import BaseService from "./BaseService";

let client = null;
let isReady = false;

const baseService = new BaseService();

function getAccessToken() {
  const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
  return baseService.request({
    method: "POST",
    url: "/conversations/access-token",
    headers: {
      // Authorization: AuthorizationData,
      "X-Auth-With-Cookies": "no-update",
    },
  });
}

async function* getPaginator(pageSize) {
  let from = 0;
  let paginator = await client.getSubscribedConversations({ from, pageSize });

  yield paginator.items;

  while (paginator.hasNextPage) {
    paginator = await paginator.nextPage();

    yield paginator.items;
  }
}

export class TwilioConversationService {
  init(options) {
    if (!client) {
      Sentry.captureMessage("Access token request");
      return getAccessToken()
        .then(({ data: token }) => {
          try {
            const _client = new Client(token, options);

            Sentry.captureMessage("TW client is created successfully");
            client = _client;
            isReady = true;

            return _client.user;
          } catch (e) {
            Sentry.captureMessage("TW client creating error");
            Sentry.captureException(e);

            throw e;
          }
        })
        .catch((e) => {
          Sentry.captureMessage("Access token request failure");
          Sentry.captureException(e);
          throw e;
        });
    }

    return promise(client);
  }

  updateToken() {
    Sentry.captureMessage("Access token request");
    return getAccessToken()
      .then(({ data: token }) => {
        Sentry.captureMessage("Access token request success");
        client.updateToken(token);
        return token;
      })
      .catch((e) => {
        Sentry.captureMessage("Access token request failure");
        Sentry.captureException(e);
        throw e;
      });
  }

  shutdown() {
    if (client) {
      Sentry.captureMessage("TW client will be shutdown");
      return client
        .shutdown()
        .then(() => {
          Sentry.captureMessage("TW client shutdown success");
          client = null;
          isReady = false;
        })
        .catch((e) => {
          Sentry.captureMessage("TW client shutdown failure");
          Sentry.captureException(e);
          throw e;
        });
    }

    return promise();
  }

  isReady() {
    return isReady;
  }

  on(type, listener) {
    client?.on(type, listener) || noop();
  }

  off(type, listener) {
    client?.off(type, listener) || noop();
  }

  emit(type, payload) {
    client.emit(type, payload);
  }

  get({ from = 0, order = "asc", pageSize = 50 } = {}) {
    return client.getSubscribedConversations({ from, order, pageSize }).then((paginator) => {
      return {
        data: paginator.items,
        hasNextPage: paginator.hasNextPage,
      };
    });
  }

  async getAll({ pageSize = 20 } = {}) {
    let data = [];

    for await (const page of getPaginator(pageSize)) {
      data.push(...page);
    }

    return data;
  }

  getBySid(sid) {
    return client?.getConversationBySid(sid) || "";
  }

  getByMessage(message) {
    return message.getProvider().getConversation();
  }

  getUserByIdentity(identity) {
    return client.getUser(identity);
  }

  /*
   * from: Index of newest Message to fetch. From the end by default
   * direction: 'backwards' | 'forward' (backwards by default)
   * pageSize: Number of messages to return in single chunk (50 by default)
   * */
  getMessages(cv, { from, direction, pageSize } = {}) {
    return cv
      .getProvider()
      .getMessages(pageSize, from, direction)
      .then((p) => {
        return p.items;
      });
  }

  getMessagesCount(cv) {
    return cv.getProvider().getMessagesCount();
  }

  sendMessage(cv, message, messageAttrs) {
    let data = message;

    if (message instanceof File) {
      data = new FormData();
      data.append("file", message);
    }

    return cv.getProvider().sendMessage(data, messageAttrs);
  }

  updateMessage(cv, message, newMessage) {
    return cv.getProvider().updateMessage(message, newMessage);
  }

  getParticipants(cv) {
    return cv.getProvider().getParticipants();
  }

  getConversationParticipant(cv, identity) {
    return cv.getProvider().getParticipantByIdentity(identity);
  }

  getUnreadMessageCount(cv) {
    return Promise.resolve(cv.lastMessage ? cv.lastMessage.index - cv.lastReadMessageIndex : 0);
  }

  updateLastReadMessageIndex(cv, index) {
    return cv.getProvider().updateLastReadMessageIndex(index);
  }

  setAllMessagesRead(cv) {
    return cv.getProvider().setAllMessagesRead();
  }
}

export default new TwilioConversationService();
