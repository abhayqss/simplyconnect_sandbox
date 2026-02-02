/**
 * Created by stsiushkevich on 13.09.18.
 */

define([path('./BaseService')], function (BaseService) {
    function IncidentReportService () {
        BaseService.apply(this);
    }

    IncidentReportService.prototype = Object.create(BaseService.prototype);
    IncidentReportService.prototype.constructor = IncidentReportService;

    IncidentReportService.prototype.save = function (eventId, data) {
        return this.request({
            url: '/ir/events/' + eventId +'/incident-reports',
            method: 'POST',
            body: data
        })
    };

    IncidentReportService.prototype.saveDraft = function (eventId, data) {
        return this.request({
            url: '/ir/events/' + eventId +'/incident-report-drafts',
            method: 'POST',
            body: data
        })
    };

    IncidentReportService.prototype.findById = function (reportId) {
        return this.request({
            url: '/ir/incident-reports/' + reportId
        });
    };

    IncidentReportService.prototype.getInitialized = function (eventId) {
        return this.request({
            url: '/ir/events/' + eventId + '/initialized-incident-report'
        });
    };

    IncidentReportService.prototype.canCreate = function () {
        //todo use /ir/events/{eventId}/can-create-incident-report when this endpoint is needed
        return this.request({
            url: '/ir/can-create-incident-report'
        });
    };

    IncidentReportService.prototype.download = function (eventId, doc) {
        return this.request({
            url: '/ir/events/' + eventId + '/pdf-incident-report'
        });
    };

    return IncidentReportService;
});

