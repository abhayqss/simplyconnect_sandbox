import { noop } from "underscore";

import { isEmpty } from "lib/utils/Utils";
import { getUrl } from "lib/utils/UrlUtils";

import BaseService from "./BaseService";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, PAGINATION } from "lib/Constants";

const { FIRST_PAGE } = PAGINATION;
const { PDF } = ALLOWED_FILE_FORMATS;

export class EventNoteService extends BaseService {
  find({ clientId, organizationId, page = FIRST_PAGE, size = 10, filter, ...other }, { getRequest = noop } = {}) {
    return super.request({
      url: getUrl({
        resources: [{ name: "organizations", id: organizationId }, { name: "clients", id: clientId }, "events-&-notes"],
      }),
      use: getRequest,
      params: { page: page - 1, size, ...filter, ...other },
    });
  }

  findEventById({ clientId, organizationId, eventId }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "events", id: eventId },
        ],
      }),
    });
  }

  findOldestDate({ clientId, prospectId, organizationId }, options) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "prospects", id: prospectId },
          { name: "clients", id: clientId },
          "events-&-notes/oldest/date",
        ],
      }),
      ...options,
    });
  }

  findNewestDate({ clientId, prospectId, organizationId }, options) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "prospects", id: prospectId },
          { name: "clients", id: clientId },
          "events-&-notes/newest/date",
        ],
      }),
      ...options,
    });
  }

  findEventNotifications({ clientId, organizationId, eventId, page = FIRST_PAGE, size = 10, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "events", id: eventId },
          "notifications",
        ],
      }),
      params: { page: page - 1, size, ...other },
    });
  }

  findEventPageNumber({ clientId, eventId, organizationId, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "events", id: eventId },
          "page-number",
        ],
      }),
      params: other,
    });
  }

  findNotePageNumber({ clientId, noteId, organizationId, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "notes", id: noteId },
          "page-number",
        ],
      }),
      params: other,
    });
  }

  findEventNotes({ clientId, organizationId, eventId, page = FIRST_PAGE, size = 10, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "events", id: eventId },
          "notes",
        ],
      }),
      params: { page: page - 1, size, ...other },
    });
  }

  findNoteContacts({ clientId, organizationId }) {
    return super.request({
      url: getUrl({
        resources: [{ name: "organizations", id: organizationId }, { name: "clients", id: clientId }, "notes/contacts"],
      }),
      response: { extractDataOnly: true },
    });
  }

  canAddEvent({ clientId, prospectId }) {
    return super.request({
      url: getUrl({
        resources: [{ name: "prospects", id: prospectId }, { name: "clients", id: clientId }, "events/can-add"],
      }),
    });
  }

  canAddEventNote({ clientId, eventId, organizationId }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          `events/${eventId}/notes/can-add`,
        ],
      }),
    });
  }

  downloadIr({ clientId, organizationId, eventId, ...other }) {
    return super.request({
      type: ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "events", id: eventId },
          "incident-report/download",
        ],
      }),
      params: other,
    });
  }

  saveEvent(data, { clientId }) {
    const isNew = isEmpty(data.id);

    return super.request({
      method: isNew ? "POST" : "PUT",
      url: `/clients/${clientId}/events`,
      body: data,
      type: "json",
    });
  }

  findNotViewableEventTypes({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/not-viewable-event-type-ids`,
    });
  }

  findNoteById({ clientId, organizationId, noteId }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "notes", id: noteId },
        ],
      }),
    });
  }

  findNoteHistory({ clientId, organizationId, noteId, page = FIRST_PAGE, size = 10, ...other }) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "clients", id: clientId },
          { name: "notes", id: noteId },
          "history",
        ],
      }),
      params: { page: page - 1, size, ...other },
    });
  }

  canViewNotes({ clientId, organizationId, ...params }) {
    return super.request({
      params,
      url: getUrl({
        resources: [{ name: "organizations", id: organizationId }, { name: "clients", id: clientId }, "notes/can-view"],
      }),
      response: { extractDataOnly: true },
    });
  }

  saveNote({ isAutoSave, ...data }, { clientId, organizationId }) {
    const isNew = isEmpty(data.id);
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
    const headers = !isAutoSave
      ? {}
      : {
          "X-Auth-With-Cookies": "no-update",
          // Authorization: AuthorizationData,
        };

    return super.request({
      method: isNew ? "POST" : "PUT",
      url: getUrl({
        resources: [{ name: "organizations", id: organizationId }, { name: "clients", id: clientId }, "notes"],
      }),
      body: data,
      type: "json",
      headers,
    });
  }

  canAddNote({ clientId, organizationId }) {
    return super.request({
      url: getUrl({
        resources: [{ name: "organizations", id: organizationId }, { name: "clients", id: clientId }, "notes/can-add"],
      }),
    });
  }

  count({ clientId, prospectId, organizationId, filter, ...other }, options) {
    return super.request({
      url: getUrl({
        resources: [
          { name: "organizations", id: organizationId },
          { name: "prospects", id: prospectId },
          { name: "clients", id: clientId },
          "events-&-notes/count",
        ],
      }),
      ...options,
      params: { clientId, prospectId, ...filter, ...other },
    });
  }

  canViewEventsAndNotes({ clientId }) {
    return super.request({
      url: `/clients/${clientId}/events-&-notes/can-view`,
      response: { extractDataOnly: true },
    });
  }
}

export default new EventNoteService();
