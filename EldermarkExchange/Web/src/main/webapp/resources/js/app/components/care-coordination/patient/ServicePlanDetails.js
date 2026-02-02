/**
 * Created by stsiushkevich on 02.10.18.
 */

var ServicePlanDetails = (function ($) {
    function isEmpty (v) {
        return !v || Object.keys(v).length === 0
    }

    var employeeService = null;
    var servicePlanService = null;

    ServiceProvider
        .getService('ServicePlanService')
        .then(function (service) {
            servicePlanService = service;
        })
        .fail(function (error) {
            alert(error.message);
        });

    ServiceProvider
        .getService('EmployeeService')
        .then(function (service) {
            employeeService = service;
        })
        .fail(function (error) {
            alert(error.message);
        });

    function ServicePlanDetails () {
        Widget.apply(this, arguments);

        this.form = null;

        this.state = {
            data: {},
            isLoading: false,
            currentUser: null
        };
    }

    ServicePlanDetails.prototype = Object.create(Widget.prototype);
    ServicePlanDetails.prototype.constructor = ServicePlanDetails;

    ServicePlanDetails.prototype.getDefaultProps = function () {
        return {
            onSave: function () {},
            onLoadSuccess: function () {},
            onLoadFailure: function () {},
            onValidationError: function () {},
            onSaveSuccess: function () {},
            onSaveFailure: function () {},
            onShowDatePicker: function () {},
            onHideDatePicker: function () {},
            onNeedSectionAdded: function () {},
            onNeedSectionRemoved: function () {},
            onGoalSectionAdded: function () {}
        }
    };

    ServicePlanDetails.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        this.loader = new Loader({
            container: this.$element
        });

        this.loader.mount();

        if (this.props.planId) this.load();

        else {
            var me = this;
            this.form = new ServicePlanForm({
                container: this.$element,
                isNew: true,
                currentUser: this.props.currentUser,
                onShowDatePicker: function () {
                    me.props.onShowDatePicker();
                },
                onHideDatePicker: function () {
                    me.props.onHideDatePicker();
                },
                onNeedSectionAdded: function (index) {
                    me.props.onNeedSectionAdded(index);
                },
                onNeedSectionRemoved: function () {
                    me.props.onNeedSectionRemoved();
                },
                onGoalSectionAdded: function (needIndex, index) {
                    me.props.onGoalSectionAdded(needIndex, index);
                }
            });

            this.form.mount();
        }

        this.loadCurrentUser();
    };

    ServicePlanDetails.prototype.componentDidUpdate = function (prevProps, prevState) {
        var currentUser = this.state.currentUser;

        if (currentUser !== prevState.currentUser) {
            this.form && this.form.update({currentUser: currentUser});
        }

        var data = this.state.data;

        if (!isEmpty(data) && isEmpty(prevState.data)) {
            var me = this;
            this.form = new ServicePlanForm({
                container: this.$element,
                isNew: false,
                isReadonly: this.props.isReadonly,
                data: data,
                currentUser: this.props.currentUser,
                onNeedSectionAdded: function (index) {
                    me.props.onNeedSectionAdded(index);
                },
                onNeedSectionRemoved: function () {
                    me.props.onNeedSectionRemoved();
                },
                onGoalSectionAdded: function (needIndex, index) {
                    me.props.onGoalSectionAdded(needIndex, index);
                }
            });

            this.form.mount();
        }

        var isLoading = this.state.isLoading;

        if (isLoading !== prevState.isLoading) {
            this.setLoading(isLoading);
        }
    };

    ServicePlanDetails.prototype.load = function () {
        if (servicePlanService) {
            this.setState({isLoading: true});

            var planId = this.props.planId;
            var patientId = this.props.patientId;

            var me = this;
            servicePlanService.findById(patientId, planId)
                .then(function (data) {
                    me.props.onLoadSuccess(data);

                    me.setState({
                        isLoading: false,
                        data: data
                    });
                })
                .fail(function (e) {
                    me.props.onLoadFailure(e);
                });
        }
    };

    ServicePlanDetails.prototype.loadCurrentUser = function () {
        if (employeeService) {
            var me = this;

            employeeService
                .find()
                .then(function (data) {
                    me.setState({currentUser: data});
                })
        }
    };

    ServicePlanDetails.prototype.setLoading = function (isLoading) {
        if (isLoading) {
            this.form && this.form.hide(false);
            this.loader.show();
        } else {
            this.loader.hide();
            this.form && this.form.show(false);
        }
    };

    ServicePlanDetails.prototype.isValid = function () {
        this.form.updateValidation();
        var isValid = this.form.isValid();
        if (!isValid) this.props.onValidationError();
        return isValid;
    };

    ServicePlanDetails.prototype.getInvalidItems = function () {
        return this.form.getInvalidFields();
    };

    ServicePlanDetails.prototype.getNeedSectionCount = function () {
        return this.form.getNeedSectionCount();
    };

    ServicePlanDetails.prototype.getNeedSection = function (index) {
        return this.form.getNeedSection(index);
    };

    ServicePlanDetails.prototype.hasScoringSection = function () {
        return this.form.hasScoringSection();
    };

    ServicePlanDetails.prototype.createScoringSection = function (isShowed) {
        this.form.createScoringSection(isShowed);
    };

    ServicePlanDetails.prototype.showScoringSection = function (areOthersHidden) {
        this.form.showScoringSection(areOthersHidden);
    };

    ServicePlanDetails.prototype.hideScoringSection = function (areOthersShowed) {
        this.form.hideScoringSection(areOthersShowed);
    };

    ServicePlanDetails.prototype.removeScoringSection = function () {
        this.form.removeScoringSection();
    };

    ServicePlanDetails.prototype.isChanged = function () {
        return this.form.isChanged();
    };

    ServicePlanDetails.prototype.save = function () {
        var data = this.form.getData();

        var isNew = !data.id;
        var patientId = this.props.patientId;

        var action = isNew ? servicePlanService.add : servicePlanService.update;

        this.setLoading(true);

        var me = this;

        action
            .apply(this, [patientId, data])
            .then(function () {
                me.setLoading(false);
                me.props.onSaveSuccess()
            })
            .fail(function (e) {
                me.setLoading(false);
                me.props.onSaveFailure(e)
            });
    };

    ServicePlanDetails.prototype.render = function () {
        return {'<>': 'div', 'class': 'service-plan-details', 'html': []};
    };

    return ServicePlanDetails;
})($);