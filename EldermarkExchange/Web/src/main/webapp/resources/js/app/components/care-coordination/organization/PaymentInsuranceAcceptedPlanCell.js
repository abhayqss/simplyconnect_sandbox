PaymentInsuranceAcceptedTable.Cell = (function ($) {

    var insurancePlanService = null;

    InsurancePlanService
        .getService('InsurancePlanService')
        .then(function (service) {
            insurancePlanService = service;
        })
        .fail(function (error) {
            alert(error.message);
        });

    function Cell () {
        Table.Cell.apply(this, arguments);
    }

    Cell.prototype = Object.create(Table.Cell.prototype);
    Cell.prototype.constructor = Cell;

    Cell.prototype.componentDidMount = function () {
        Table.Cell.prototype.componentDidMount.apply(this);

        if (insurancePlanService) {
            var plans = this.props.data;
            var networkId = (this.props.getExtraProps() || {}).networkId;

            var me = this;
            insurancePlanService.find({
                networkId: networkId
            }).then(function (data) {
                if (data && data.length) {
                    var select = new MultiSelect({
                        container: me.$element,
                        options: $.map(data, function (o) {
                            return {text: o.name, value: o.id}
                        }),
                        value: plans
                    });

                    me.select = select;
                    select.mount();
                }
            }).fail(function (e) {
                console.error(e);
            })
        }
    }

    return Cell;
})($);