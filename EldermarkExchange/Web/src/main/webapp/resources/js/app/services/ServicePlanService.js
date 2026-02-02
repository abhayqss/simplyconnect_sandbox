/**
 * Created by stsiushkevich on 13.09.18.
 */

var ServicePlanService = (function ($) {

    var context = ExchangeApp.info.context;

    var auth = ExchangeApp.utils.auth;

    function ServicePlanService () {
        CURDService.apply(this);
    }

    ServicePlanService.prototype = Object.create(CURDService.prototype);
    ServicePlanService.prototype.constructor = ServicePlanService;

    ServicePlanService.prototype.add = function (patientId, plan) {
        return $.ajax({
            url: context + '/care-coordination/patients/patient/'+ patientId +'/service-plans',
            type: 'PUT',
            data: JSON.stringify(plan),
            contentType: 'application/json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    ServicePlanService.prototype.update = function (patientId, plan) {
        return $.ajax({
            url: context + '/care-coordination/patients/patient/'+ patientId +'/service-plans',
            type: 'POST',
            data: JSON.stringify(plan),
            contentType: 'application/json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    ServicePlanService.prototype.findById = function (patientId, planId) {
        return $.ajax({
            url: context + '/care-coordination/patients/patient/'+ patientId +'/service-plans/' + planId,
            type: 'GET',
            contentType: 'application/json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    return ServicePlanService;
})($);