/**
 * Created by stsiushkevich on 11.09.18.
 */

function ServicePlanListPanel () {
    Widget.apply(this, arguments);
}

ServicePlanListPanel.prototype = Object.create(Widget.prototype);
ServicePlanListPanel.prototype.constructor = ServicePlanListPanel;

ServicePlanListPanel.prototype.getDefaultProps = function () {
    return {
        onSavePlanSuccess: function () {},
        onSavePlanFailure: function () {}
    };
};

ServicePlanListPanel.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');

    var me = this;

    this.searchPanel = new SearchPanel({
        container: this.$element,
        onSearch: function (e) {
            e.preventDefault();
            me.list.reload();
        }
    });

    this.searchPanel.mount();


    this.list = new ServicePlanList ({
        container: this.$element,
        patientId: this.props.patientId,
        getExtraAjaxData: function () {
            return me.searchPanel.getData()
        },
        onSelect: function (data) {
            me.onViewPlan(data);
        },
        onDownload: function (data) {
            me.onDownloadPlan(data);
        },
        onEdit: function (data) {
            me.onEditPlan(data);
        }
    });

    this.list.mount();
};

ServicePlanListPanel.prototype.componentDidUpdate = function (prevProps, prevState) {
    if(this.list.isEmpty()) this.list.reload();
};

ServicePlanListPanel.prototype.onAddPlan = function () {
    var patientId = this.props.patientId;

    var router = ExchangeApp.routers.ModuleRouter;

    router.route({
        template: 'care-coordination/patients/'+ patientId +'/create-service-plan'
    });

    var me = this;
    this.modal = new ServicePlanModal({
        container: $('body'),
        isOpen: true,
        patientId: patientId,
        onSavePlanSuccess: function () {
            me.onSavePlanSuccess();
        },
        onSavePlanFailure: function () {
            me.onSavePlanFailure();
        },
        onHidden: function () {
            router.back();
        }
    });

    this.modal.mount();
};

ServicePlanListPanel.prototype.onEditPlan = function (data) {
    var patientId = this.props.patientId;

    var router = ExchangeApp.routers.ModuleRouter;

    router.route({
        template: 'care-coordination/patients/'+ patientId +'/edit-service-plan'
    });

    var me = this;
    this.modal = new ServicePlanModal({
        container: $('body'),
        isOpen: true,
        planId: data.id,
        patientId: patientId,
        onLeavePlanUnchanged: function () {
            me.onLeavePlanUnchanged();
        },
        onSavePlanSuccess: function () {
            me.onSavePlanSuccess();
        },
        onSavePlanFailure: function () {
            me.onSavePlanFailure();
        },
        onHidden: function () {
            router.back();
        },
        onLoadPlanFailure: function () {
            setTimeout(function () {
                bootbox.alert('The service plan has not been opened. ' +
                    'Please try again later.', function () {
                    me.modal.hide();
                });
            }, 300);
        }
    });

    this.modal.mount();
};

ServicePlanListPanel.prototype.onViewPlan = function (data) {
    var patientId = this.props.patientId;

    var router = ExchangeApp.routers.ModuleRouter;

    router.route({
        template: 'care-coordination/patients/'+ patientId +'/view-service-plan'
    });

    var me = this;
    this.modal = new ServicePlanModal({
        container: $('body'),
        isOpen: true,
        isReadonlyPlan: true,
        planId: data.id,
        planDateModified: data.dateModified,
        isArchivedPlan: data.isArchived,
        patientId: patientId,
        hasScrollableBody: data.isArchived,
        onHidden: function () {
            router.back();
        },
        onLoadPlanFailure: function () {
            setTimeout(function () {
                bootbox.alert('The service plan has not been opened. ' +
                    'Please try again later.', function () {
                    me.modal.hide();
                });
            }, 300);
        },
        onViewArchivedPlan: function (data) {
            me.onViewPlan($.extend({}, data, {isArchived: true}));
        }
    });

    this.modal.mount();
};

ServicePlanListPanel.prototype.onDownloadPlan = function (data) {
    var context = ExchangeApp.info.context;

    var id = data.id;
    var patientId = this.props.patientId;
    var url = context + '/care-coordination/patients/patient/' + patientId + '/service-plans/' + id + '/pdf';
    var date = new Date();
    var timeZoneOffset = date.getTimezoneOffset();
    url = url + '?timeZoneOffset='+timeZoneOffset;

    $.fileDownload(url, {
        failCallback: function () {
            bootbox.alert('The service plan has not been downloaded. ' +
                'Please try again later.');
        }
    });
};

ServicePlanListPanel.prototype.onLeavePlanUnchanged = function () {
    var me = this;
    me.modal.hide();
};

ServicePlanListPanel.prototype.onSavePlanSuccess = function () {
    var me = this;

    me.modal.hide();

    bootbox.alert('The updates have been saved', function () {
        me.props.onSavePlanSuccess();
        me.list.reload();
    });
};

ServicePlanListPanel.prototype.onSavePlanFailure = function () {
    var me = this;

    me.modal.hide();

    bootbox.alert('The updates have not been saved. ' +
        'Please check the form and try again later.');

    this.props.onSavePlanFailure();
};

ServicePlanListPanel.prototype.render = function () {
    return {
        '<>': 'div',
        'class': 'service-plans',
        'html': [
            {'<>': 'div', 'class': 'row', 'html': []}
        ]
    }
};