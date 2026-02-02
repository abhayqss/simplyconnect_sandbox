import { any, each, extend, keys, noop, omit, some } from "underscore";

import qs from "qs";
import request from "superagent";

import config from "config";

import Converter from "lib/converters/Converter";
import ConverterFactory from "lib/converters/ConverterFactory";

import WebError from "lib/errors/WebError";
import ServerError from "lib/errors/ServerError";
import NetworkError from "lib/errors/NetworkError";
import AuthorizationError from "lib/errors/AuthorizationError";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, SERVER_ERROR_CODES } from "lib/Constants";

import { isEmpty } from "lib/utils/Utils";

import { addAbortAllEventListener, removeAbortAllEventListener } from "lib/utils/AjaxUtils";

// requests, which have not implemented on the backend yet
const notImplementedRequestTemplates = [
  // '/login', // implemented
  // '/logout', // implemented
  /* '/auth/validate-token',*/ // implemented

  /*Directory Service*/
  /*'/states', // implemented
  '/genders', // implemented
  '/marital-status', // implemented
  '/domains', // implemented
  '/priorities', // implemented*/
  /* '/note-types',
   '/note-subtype',
   '/event-types',
   '/additional-services',*/
  //'/community-types', // implemented
  // '/services-treatment-approaches', // implemented
  /*'/system-roles', // implemented
  '/care-team-channels', // implemented
  '/care-team-responsibilities', // implemented
  '/primary-focuses', // implemented
  '/emergency-services', // implemented
  '/additional-services', // implemented
  '/language-services', // implemented*/
  "/organization-types",
  // '/encounter-note-type', // implemented
  //'/insurance/networks', // implemented
  //'/insurance/payment-plans', // implemented
  /*'/organizations', // implemented
  '/organizations/count', // implemented
  '/organizations/:id', // implemented
  '/communities', // implemented
  '/communities/count', // implemented
  '/communities/:id', // implemented*/
  //'/care-levels',
  "/handsets",
  "/handsets/count",
  "/zones",
  "/zones/count",
  "/device-types",
  "/device-types/count",
  "/device-types/:id",
  // '/locations',
  "/locations/count",
  /*'/contacts',// implemented
  '/contacts/count',// implemented
  '/contacts/:id',*/ // implemented
  /*'/clients', // implemented
  '/clients/:id', // implemented
  '/clients/count', // implemented
  '/service-plans', // implemented
  '/service-plans/count', // implemented
  '/service-plans/:id', // implemented*/
  /*'/assessments',
  '/assessments/:id',
  '/assessments/:id/history',
  '/assessment-new',
  '/assessment-survey',
  '/assessment-management',
  '/assessment-count',*/
  /*'/care-team',
  '/care-team/count',*/
  "/alerts",
  "/alerts/count",
  // '/dashboard',
  // '/marketplace/communities', // implemented
  // '/marketplace/communities/:id', // implemented
  /*'/events', // implemented
  '/composed-events-notes', // implemented
  '/events/:id', // implemented
  '/events/:id/notifications', // implemented
  '/events/:id/notes', // implemented
  '/notes', // implemented
  '/notes/:id', // implemented
  '/notes/:id/history', // implemented*/
  /*'/admit-dates',*/
  //'/documents', // implemented
  // '/referrals'
  // '/lab-research/orders/validate-uniq-in-organization'
];

const { responseTimeout } = config;

const { UNAUTHORIZED, CONNECTION_ABORTED, INTERNAL_SERVER_ERROR, NO_CONNECTION_OR_SERVER_IS_NOT_AVAILABLE } =
  SERVER_ERROR_CODES;

const UNAUTHORIZED_ERROR_TEXT = "Authentication is required to access this resource";
const NO_CONNECTION_ERROR_TEXT = "No Internet connection. Make sure that Wi-Fi or cellular mobile data is turned on.";
const INTERNAL_SERVER_ERROR_TEXT =
  "Internal Server Error.\n During the query execution, errors occurred on the server. Please, contact Support.";
const NOT_FOUND_URL_ERROR_TEXT = "Internal Server Error.\n The specified URL not found.";
const SERVER_IS_NOT_AVAILABLE_ERROR_TEXT = "Server is not available. Please, try to connect again later.";

const errors = {
  unauthorized: {
    error: {
      code: UNAUTHORIZED,
      message: UNAUTHORIZED_ERROR_TEXT,
    },
  },
  notFoundUrl: {
    error: {
      code: INTERNAL_SERVER_ERROR,
      message: NOT_FOUND_URL_ERROR_TEXT,
    },
  },
  serverInternal: {
    error: {
      code: INTERNAL_SERVER_ERROR,
      message: INTERNAL_SERVER_ERROR_TEXT,
    },
  },
  noConnectionOrServerIsNotAvailable: {
    error: {
      code: NO_CONNECTION_OR_SERVER_IS_NOT_AVAILABLE,
      message: `HTTP Request has been terminated.\n Possible causes:\n\n - ${NO_CONNECTION_ERROR_TEXT}\n - ${SERVER_IS_NOT_AVAILABLE_ERROR_TEXT}`,
    },
  },
};

const converter = ConverterFactory.getConverter(Converter.types.JS_OBJECT_TO_FORM_DATA);

function isAllowedFileMimeType(type) {
  return any(ALLOWED_FILE_FORMAT_MIME_TYPES, (t) => t === type);
}

function parseResponseText(text) {
  const { body, statusCode: status } = JSON.parse(text);

  return { body, status };
}

async function onSuccess(response) {
  if (response.xhr?.responseType === "blob") {
    const type = response.headers["content-type"];

    if (isAllowedFileMimeType(type)) {
      const disposition = response.headers["content-disposition"];

      if (disposition === undefined) {
        throw ServerError({
          message:
            "'Content-Disposition' header is missing. Possible solution: Backend has to explicitly set 'Access-Control-Expose-Headers'",
        });
      }

      return {
        data: response.body,
        name: /filename="(.*(\.\w+)?)"/.exec(disposition)[1] || "unnamed.txt",
      };
    }

    const text = await response.body.text();
    const { body, status } = parseResponseText(text);

    throw new ServerError({ status, ...body.error });
  }

  let { body, status } = parseResponseText(response.text);

  if ((status === 200 || status === 201) && body.success !== false) {
    return body;
  }

  if (body.crossDomain || body.code === CONNECTION_ABORTED) {
    if (status) throw new ServerError(errors.serverInternal.error);
    throw new NetworkError(errors.noConnectionOrServerIsNotAvailable.error);
  }

  throw new ServerError({ status, ...body.error });
}

function onFailure(e) {
  if (e instanceof WebError) throw e;

  let { code, status, response, crossDomain } = e;

  if (status === 401) {
    // 检查当前是否在登录页，假设登录页URL是 '/login'
    if (window.location.pathname !== "/web-portal/home") {
      throw new AuthorizationError(errors.unauthorized.error);
    }
  }
  if (!response?.text) {
    console.error(e);
    return e;
  }

  if (status === 404) {
    throw new NetworkError(errors.notFoundUrl.error);
  }

  if (code === "ABORTED" || crossDomain) {
    if (status) throw new ServerError(errors.serverInternal.error);
    throw new NetworkError(errors.noConnectionOrServerIsNotAvailable.error);
  }

  let { body } = JSON.parse(response.text) || {};

  if (!body && response.error) {
    body = response.body;
  } else if (!body) body = {};

  if (!((body && body.error) || status === 500)) {
    throw new AuthorizationError(errors.unauthorized.error);
  }

  let error = errors.serverInternal.error;

  throw new ServerError({
    body,
    status,
    ...error,
    ...body.error,
  });
}

export default class BaseService {
  USER_ID_TEMPLATE = "{userId}";

  request(opts) {
    opts = extend(
      {
        method: "GET",
        url: null,
        use: noop,
        body: null,
        type: "json",
        params: null,
        callback: null,
        hasEmptyParams: false,
        response: {
          extract: (o) => o,
          extractDataOnly: false,
        },
      },
      opts,
    );

    const { method } = opts;

    const { remote } = config;

    const isNotImplemented = some(notImplementedRequestTemplates, (t) => {
      return opts.url.includes(t);
    });

    if (!remote.isEnabled || isNotImplemented) {
      return new Promise((resolve) => resolve());

      /*return mockServer.service({
          ...opts,
          params: {
              ...opts.params,
              ...opts.mockParams
          }
      }).then(onSuccess)*/
    }

    const url = `${config.remote.url}${opts.url}`;
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
    let rq = request(method, url)
      .withCredentials()
      // .set("Authorization", AuthorizationData)
      .set("X-Auth-With-Cookies", "no-update")
      .set("TimezoneOffset", new Date().getTimezoneOffset())
      .timeout({ response: opts.responseTimeout || responseTimeout });

    const { headers } = opts;

    if (headers) {
      each(keys(headers), (key) => {
        rq.set(key, headers[key]);
      });
    }

    if (method === "DELETE" && opts.body) {
      rq = rq.type(opts.type).send(opts.body);
    } else if (["GET", "DELETE"].includes(method)) {
      rq = rq.query(
        qs.stringify(
          opts.hasEmptyParams ? opts.params : omit(opts.params, (v) => isEmpty(v, { allowEmptyBool: false })),
          { arrayFormat: "comma" },
        ),
      );

      if (isAllowedFileMimeType(opts.type)) {
        rq = rq.responseType("blob").timeout({ response: 5 * 60 * 1000 });
      }
    }

    if (["PUT", "POST"].includes(method)) {
      if (opts.type === "multipart/form-data") {
        rq.send(converter.convert(opts.body));
      } else if (opts.type === "application/x-www-form-urlencoded") {
        rq.type(opts.type).send(qs.stringify(opts.body, { arrayFormat: "repeat" }));
      } else rq = rq.type(opts.type).send(opts.body);
    }

    const abort = () => rq.abort();

    addAbortAllEventListener(abort);

    return rq
      .use(opts.use)
      .then((rp) => {
        removeAbortAllEventListener(abort);
        return rp;
      })
      .then(onSuccess)
      .then((rp) => {
        const { extract, extractDataOnly } = opts.response;

        return extractDataOnly ? rp.data : extract(rp);
      })
      .catch((e) => {
        removeAbortAllEventListener(abort);
        throw e;
      })
      .catch(onFailure);
  }
}
