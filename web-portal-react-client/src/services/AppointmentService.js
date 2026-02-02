import BaseService from './BaseService'

import {
    PAGINATION,
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION
const { XLSX } = ALLOWED_FILE_FORMATS

export class AppointmentService extends BaseService {
    find({ page = FIRST_PAGE, ...other }) {
        return super.request({
            url: '/appointments',
            params: { page: page - 1, ...other }
        })
    }

    findById(appointmentId) {
        return super.request({
            url: `/appointments/${appointmentId}`,
            response: { extractDataOnly: true }
        })
    }

    findAppointmentHistory({ appointmentId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/appointments/${appointmentId}/history`,
            params: { page: page - 1, ...other },
        })
    }

    findAppointmentUnarchivedId(params) {
        //?appointmentChainId <- for external urls
        return super.request({
            url: `/appointments/unarchived-id`,
            params,
            response: { extractDataOnly: true }
        })
    }

    export(params) {
        return super.request({
            params,
            url: `/appointments/export`,
            type: ALLOWED_FILE_FORMAT_MIME_TYPES[XLSX],
        })
    }

    canView(params, options) {
        return super.request({
            params,
            url: '/appointments/can-view',
            ...options
        })
    }

    canAdd(params, options) {
        return super.request({
            params,
            url: '/appointments/can-add',
            ...options
        })
    }

    save({ clientId, ...appointment }) {
        const isNew = !appointment.id

        return super.request({
            method: isNew ? 'POST' : 'PUT',
            url: `/clients/${clientId}/appointments`,
            body: appointment,
            type: 'json'
        })
    }

    cancel({ id, ...body }) {
        return super.request({
            body,
            type: 'json',
            method: 'POST',
            url: `/appointments/${id}/cancel`
        })
    }

    count(params) {
        return super.request({
            url: '/appointments/count',
            response: { extractDataOnly: true },
            params
        })
    }

    findContacts(params) {
        return super.request({
            url: '/appointments/contacts',
            response: { extractDataOnly: true },
            params
        })
    }

    findParticipation(params) {
        return super.request({
            url: '/appointments/participation',
            response: { extractDataOnly: true },
            params
        })
    }

    checkTimeSLotAvailability(params) {
        return super.request({
            params,
            url: '/appointments/availability',
            response: { extractDataOnly: true }
        })
    }
}

export default new AppointmentService()