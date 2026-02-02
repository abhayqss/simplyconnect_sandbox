define(
    [
        'underscore',
        path('./BaseService'),
        path('app/lib/Utils')
    ],
    function (_, BaseService, U) {
        function MedicationService() {
            BaseService.apply(this);
        }

        MedicationService.prototype = Object.create(BaseService.prototype);
        MedicationService.prototype.constructor = MedicationService;


        MedicationService.prototype.findActive = function (params) {
            return this.request({
                url: U.interpolate(
                    '/care-coordination/patients/patient/$0/active-medications',
                    params.patientId
                ),
                params: _.omit('patientId')
            });
        };

        MedicationService.prototype.findInactive = function (params) {
            return this.request({
                url: U.interpolate(
                    '/care-coordination/patients/patient/$0/inactive-medications',
                    params.patientId
                ),
                params: _.omit('patientId')
            });
        };

        return MedicationService;
    }
);