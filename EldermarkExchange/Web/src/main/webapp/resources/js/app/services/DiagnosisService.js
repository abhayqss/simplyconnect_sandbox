define(
    [
        'underscore',
        path('./BaseService'),
        path('app/lib/Utils')
    ],
    function (_, BaseService, U) {
        function DiagnosisService() {
            BaseService.apply(this);
        }

        DiagnosisService.prototype = Object.create(BaseService.prototype);
        DiagnosisService.prototype.constructor = DiagnosisService;


        DiagnosisService.prototype.find = function (params) {
            return this.request({
                url: U.interpolate(
                    '/care-coordination/patients/patient/$0/problems',
                    params.patientId
                ),
                params: _.omit('patientId')
            });
        };

        return DiagnosisService;
    }
);