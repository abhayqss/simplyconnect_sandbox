/**
 * Created by stsiushkevich on 13.09.18.
 */

define(
    [
        'underscore',
        'superagent',
        path('app/lib/Utils'),
        path('app/lib/Constants'),
        path('app/lib/mock/MockData'),
        path('app/lib/errors/ServerError'),
        path('app/lib/errors/NetworkError'),
        path('app/lib/errors/ServerResponseError')
    ],
    function (_, request, U, C, Mock, ServerError, NetworkError, ServerResponseError) {

        var RESPONSE_TIMEOUT = 15000;

        var SERVER_ERROR_TYPES = C.SERVER_ERROR_TYPES;

        var NO_CONNECTION_ERROR_TEXT = 'No Internet connection. Make sure that Wi-Fi or cellular mobile data is turned on.'
        var INTERNAL_SERVER_ERROR_TEXT = 'Internal Server Error.\n During the query execution, errors occurred on the server. Please, contact Support.'
        var SERVER_IS_NOT_AVAILABLE_ERROR_TEXT = 'Server is not available. Please, try to connect again later.'

        var errors = {
            serverInternal: {
                error: {
                    code: SERVER_ERROR_TYPES.INTERNAL_SERVER_ERROR,
                    message: INTERNAL_SERVER_ERROR_TEXT
                }
            },
            noConnectionOrServerIsNotAvailable: {
                error: {
                    code: SERVER_ERROR_TYPES.NO_CONNECTION_OR_SERVER_IS_NOT_AVAILABLE,
                    message: U.interpolate('HTTP Request has been terminated.\n Possible causes:\n\n - $1\n - $2', NO_CONNECTION_ERROR_TEXT, SERVER_IS_NOT_AVAILABLE_ERROR_TEXT)
                }
            }
        };

        // requests, which have not implemented on the backend yet
        var notImplementedRequestTemplates = [
        ];

        var context = ExchangeApp.info.context;

        var auth = ExchangeApp.utils.auth;

        function onSuccess(response) {

            var parsed = null;

            try {
                parsed = JSON.parse(response.text)
            } catch (e) {
                throw new ServerResponseError({
                    status: 'Response parse error',
                    body: response.body
                })
            }

            var body = parsed.body;
            var status = parsed.statusCode;

            if ((status === 200 || status === 201) && body.success !== false) {
                return body
            }

            if (body.crossDomain || body.code === SERVER_ERROR_TYPES.CONNECTION_ABORTED) {
                if (status) throw new ServerError(errors.serverInternal.error);
                throw new NetworkError(errors.noConnectionOrServerIsNotAvailable.error)
            }

            throw new ServerError({status: status, body: body});
        }

        function onFailure(response) {
            if (response.crossDomain) {
                if (response.status) throw new ServerError(errors.serverInternal.error);
                throw new NetworkError(errors.noConnectionOrServerIsNotAvailable.error);
            }
            throw new ServerError({
                body: response && response.body ? response.body : response
            })
        }

        function Service() {
        }

        Service.prototype.request = function (opts) {

            var notImplemented = _.some(notImplementedRequestTemplates, function (t) {
                return opts.url.includes(t)
            });

            opts = _.extend({
                method: 'GET',
                url: null,
                body: null,
                type: 'json',
                params: null,
                callback: null
            }, opts);

            var method = opts.method;

            if (notImplemented) {
                return new Promise(function (resolve, reject) {
                    var url = opts.url;

                    setTimeout(function () {
                        if (/states/.test(url)) {
                            resolve(Mock.findStates())
                        } else if (/genders/.test(url)) {
                            resolve(Mock.findGenders())
                        } else if (/races/.test(url)) {
                            resolve(Mock.findRaces())
                        } else if (/problems/.test(url)) {
                            resolve(Mock.findDiagnoses())
                        } else if (/active-medications/.test(url)) {
                            resolve(Mock.findMedications())
                        } else if (/inactive-medications/.test(url)) {
                            resolve(Mock.findMedications())
                        } else if (/incident-places/.test(url)) {
                            resolve(Mock.findIncidentPlaces())
                        } else if (/incidents/.test(url)) {
                            resolve(Mock.findIncidentTypes(opts.data))
                        } else if (/incident-places/.test(url)) {
                            resolve(Mock.findIncidentPlaces())
                        } else if (/incident-level-reporting-settings/.test(url)) {
                            resolve(Mock.findIncidentLevelReportingSettings())
                        } else if (/incident-reports/.test(url)) {
                            if (method === 'PUT' || method === 'POST') {
                                resolve(Mock.saveIncidentReport(opts.data))
                            }
                        } else if (/incident-report-drafts/.test(url)) {
                            if (method === 'PUT' || method === 'POST') {
                                resolve(Mock.saveIncidentReportDraft(opts.data))
                            }
                        }
                    }, 1000)
                }).then(onSuccess)
            }

            var rqType = opts.type;
            var url = context + opts.url;

            if (method === 'POST' || method === 'PUT') {
                var rq = request(method, url)
                    .withCredentials()
                    .type(rqType)
                    .set('X-CSRF-TOKEN', auth.getToken())
                    .timeout({response: RESPONSE_TIMEOUT});

                if (rqType === 'multipart/form-data') {
                    _.each(opts.body, function (v, k) {
                        if (v instanceof File) {
                            rq = rq.attach(k, v)
                        }

                        else {
                            if (_.isObject(v)) {
                                v = JSON.stringify(v)
                            }

                            rq = rq.field(k, v)
                        }
                    })
                } else {
                    rq = rq.send(opts.body)
                }

                return rq.then(onSuccess, onFailure)
            }

            else if (method === 'DELETE') {
                return request
                    .del(url)
                    .set('X-CSRF-TOKEN', auth.getToken())
                    .timeout({response: RESPONSE_TIMEOUT})
                    .then(onSuccess, onFailure)
            }

            else if (method === 'GET') {
                var rq = request.get(url)
                    .set('X-CSRF-TOKEN', auth.getToken())
                    .timeout({response: RESPONSE_TIMEOUT});

                var headers = opts.headers;
                if (headers) {
                    _.each(_.keys(headers), function (key) {
                        rq.set(key, headers[key])
                    })
                }
                return rq.query(opts.params)
                    .send(opts.body)
                    .then(onSuccess, onFailure)
            }
        };

        return Service;
    }
);