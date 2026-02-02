import request from "superagent";
import { noop } from "underscore";

import { globalEvent } from "@billjs/event-emitter";

import { remote } from "config";

import WebError from "lib/errors/WebError";

export function Response(onSuccess = noop, onFailure = noop, onUnknown = noop) {
  return function (response = {}) {
    const { data, success, error, ...rest } = response || {};

    if (success) return onSuccess({ data, ...rest });
    else if (response instanceof WebError) return onFailure(response);
    else if (error) return onFailure(error);
    else {
      const { success, error } = rest?.body || {};

      return onUnknown(!success && (error || response));
    }
  };
}

export async function handleResponse(response) {
  if (response.type === "application/json") {
    const { body, statusCode: status } = JSON.parse(await response.body.text());

    return { ...body, status };
  }

  return response;
}

export function fireAbortAllEvent() {
  globalEvent.fire("ajax.abortAll");
}

export function addAbortAllEventListener(listener) {
  globalEvent.on("ajax.abortAll", listener);
}

export function removeAbortAllEventListener(listener) {
  globalEvent.off("ajax.abortAll", listener);
}

export function download({ url, path, mimeType }) {
  const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";

  const rq = request
    .get(url ?? remote.url + path)
    .withCredentials()
    // .set("Authorization", AuthorizationData)
    .set("X-Auth-With-Cookies", "no-update")
    .type(mimeType)
    .responseType("blob");

  const abort = () => rq.abort();

  addAbortAllEventListener(abort);

  return rq
    .then((rp) => {
      removeAbortAllEventListener(abort);
      return rp;
    })
    .then((rp) => handleResponse(rp))
    .catch((e) => {
      removeAbortAllEventListener(abort);
      throw e;
    });
}
