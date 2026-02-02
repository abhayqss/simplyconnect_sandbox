'use strict'

var PatientServicePlansModule = (function ($) {

    var components = [
        'Component',
        'Widget',
        'ToolBar',
        'Grid',
        'Form',
        'SearchPanel',
        'ServicePlanList',
        'care-coordination/patient/PatientServicePlans'
    ];

    var loader = ExchangeApp.loaders.ComponentLoader;

    function PatientServicePlansModule(params) {
        this.$container = $(params.container)
    }

    PatientServicePlansModule.prototype = Object.create(Module.prototype);
    PatientServicePlansModule.prototype.constructor = PatientServicePlansModule;

    PatientServicePlansModule.prototype.init = function () {
        var self = this;

        loader.load(components, function () {
            var servicePlans = new PatientServicePlans({container: self.$container});
            servicePlans.init();
        });
    };

    return PatientServicePlansModule
})($);

ExchangeApp.modules.CareCoordinationPatientServicePlans = new PatientServicePlansModule({
    container: $('#patientServicePlansContent')
});
