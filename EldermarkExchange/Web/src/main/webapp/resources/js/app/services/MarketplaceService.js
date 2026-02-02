var MarketplaceService = (function ($) {

    var context = ExchangeApp.info.context;

    var auth = ExchangeApp.utils.auth;

    function MarketplaceService () {
        CURDService.apply(this);
    }

    MarketplaceService.prototype = Object.create(CURDService.prototype);
    MarketplaceService.prototype.constructor = MarketplaceService;
    
    MarketplaceService.prototype.communityTypesIds = function (primaryFocusId) {
        return $.ajax({
            url: context + '/marketplace/communityTypes',
            type: 'POST',
            data: JSON.stringify(primaryFocusId),
            contentType: 'application/json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };
    
    MarketplaceService.prototype.serviceTreatmentApproaches = function (primaryFocusId) {
        return $.ajax({
            url: context + '/marketplace/servicesTreatmentApproach',
            type: 'POST',
            data: JSON.stringify(primaryFocusId),
            contentType: 'application/json',
            headers: {
                'X-CSRF-TOKEN': auth.getToken()
            }
        })
    };

    return MarketplaceService;
})($);