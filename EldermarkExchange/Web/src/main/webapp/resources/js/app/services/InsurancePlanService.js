var InsurancePlanService = (function ($) {

    var context = ExchangeApp.info.context;

    var auth = ExchangeApp.utils.auth;

    function InsurancePlanService () {
        CURDService.apply(this);
    }

    InsurancePlanService.prototype = Object.create(CURDService.prototype);
    InsurancePlanService.prototype.constructor = InsurancePlanService;

    InsurancePlanService.prototype.find = function (params) {
        var networkId = params.networkId;

        return $.ajax({
            url: context +'/networks/'+ networkId +'/plans',
            type: 'GET',
            contentType: 'json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    return InsurancePlanService;
})($);