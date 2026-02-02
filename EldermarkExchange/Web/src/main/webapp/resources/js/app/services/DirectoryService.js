define(['./BaseService'], function (BaseService) {
    function DirectoryService () { BaseService.apply(this); }

    DirectoryService.prototype = Object.create(BaseService.prototype);
    DirectoryService.prototype.constructor = DirectoryService;

    DirectoryService.prototype.find = function (entity, params) {
        switch (entity) {
            case 'STATE': return this.findStates();
            case 'RACE': return this.findRaces();
            case 'GENDER': return this.findGenders();
            case 'INCIDENT_PLACE': return this.findIncidentPlaces();
            case 'INCIDENT_TYPE': return this.findIncidentTypes(params);
            case 'CLASS_MEMBER_TYPE': return this.findClassMemberTypes();
            case 'INCIDENT_LEVEL_REPORTING_SETTINGS': return this.findIncidentLevelReportingSettings(params);
        }
    };

    DirectoryService.prototype.findClassMemberTypes = function () {
        return this.request({ url: '/ir/directory/class-member-types' });
    };

    DirectoryService.prototype.findStates = function () {
        return this.request({ url: '/ir/directory/states' });
    };

    DirectoryService.prototype.findGenders = function () {
        return this.request({ url: '/ir/directory/genders' });
    };

    DirectoryService.prototype.findRaces = function () {
        return this.request({ url: '/ir/directory/races' });
    };

    DirectoryService.prototype.findIncidentPlaces = function () {
        return this.request({ url: '/ir/directory/incident-places' });
    };

    DirectoryService.prototype.findIncidentTypes = function (params) {
        return this.request({ url: '/ir/directory/incident-types', params: params });
    };

    DirectoryService.prototype.findIncidentLevelReportingSettings = function (params) {
        return this.request({
            url: '/ir/directory/incident-level-reporting-settings',
            params: params
        });
    };

    return DirectoryService;
});