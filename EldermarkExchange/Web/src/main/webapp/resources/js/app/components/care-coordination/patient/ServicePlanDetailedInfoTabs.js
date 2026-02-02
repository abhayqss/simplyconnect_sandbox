/**
 * Created by stsiushkevich on 01.10.18.
 */
function ServicePlanDetailedInfoTabs () {
    Tabs.apply(this, arguments);

    this.state = {
        activeIndex: 0,
        isDetailsReady: false,
        isChangeHistoryReady: false,
        isScrollerShowed: false
    }
}

ServicePlanDetailedInfoTabs.prototype = Object.create(Tabs.prototype);
ServicePlanDetailedInfoTabs.prototype.constructor = ServicePlanDetailedInfoTabs;

Tabs.prototype.getDefaultProps = function () {
    return {
        maxHeight: 300,
        isScrollable: true,
        className: 'scroller-container',
        tabs: [
            {
                title: 'Details',
                isActive: true,
                content: {'<>': 'div', 'class': 'service-plan-details-tab-content'}
            },
            {
                title: 'Change History',
                content: {'<>': 'div', 'class': 'service-plan-change-history-tab-content'}
            }
        ],
        onLoadPlanFailure: function () {}
    };
};

ServicePlanDetailedInfoTabs.prototype.componentDidMount = function () {
    Tabs.prototype.componentDidMount.apply(this);

    var me = this;

    this.details = new ServicePlanDetails({
        container: this.$body.find('.service-plan-details-tab-content'),
        isReadonly: true,
        planId: this.props.planId,
        patientId: this.props.patientId,
        onLoadSuccess: function () {
            me.updateBodyHeight();
        },
        onLoadFailure: function (e) {
            me.props.onLoadPlanFailure(e);
        }
    });

    this.details.mount();

    this.list = new ServicePlanChangeHistoryList({
        container: this.$body.find('.service-plan-change-history-tab-content'),
        planId: this.props.planId,
        patientId: this.props.patientId,
        planDateModified: this.props.planDateModified,
        onSelect: function (data) {
            me.props.onViewArchivedPlan(data);
        }
    });

    this.list.mount();

    var isScrollable = this.props.isScrollable;

    if (isScrollable) {
        this.$body.addClass('scrollable-y');

        var ARROW_TOP_IMAGE_URL = ExchangeApp.info.context + '/resources/images/arrow2-top.svg';

        this.$element.json2html({}, {
            '<>': 'div',
            'title': 'Back to Top',
            'class': 'up-scroller', 'html': [
                {'<>': 'img', 'src': ARROW_TOP_IMAGE_URL}
            ], 'onclick': function () {
                me.scrollToStart()
            }
        });

        this.addOnScrollHandler(function (e) {
            var top = e.target.scrollTop;
            var h = e.target.clientHeight;

            var isShowed =  me.state.isScrollerShowed;

            if (top > h/2) {
                !isShowed && me.setState({
                    isScrollerShowed: true
                });
            } else {
                isShowed && me.setState({
                    isScrollerShowed: false
                });
            }
        });
    }
};

ServicePlanDetailedInfoTabs.prototype.componentDidUpdate = function (prevProps, prevState) {
    Tabs.prototype.componentDidUpdate.apply(this, arguments);

    var index = this.state.activeIndex;

    if (index !== prevState.activeIndex) {
        if (this.list.isEmpty()) this.list.reload();
    }

    var maxHeight = this.props.maxHeight;

    if (maxHeight !== prevProps.maxHeight) {
        this.updateBodyHeight();
    }

    var isScrollerShowed = this.state.isScrollerShowed;

    if (isScrollerShowed !== prevProps.isScrollerShowed) {
        if (isScrollerShowed) this.showUpScroller();
        else this.hideUpScroller();
    }
};

ServicePlanDetailedInfoTabs.prototype.updateBodyHeight = function () {
    var mh = this.$menu.height();
    this.$body.css({ maxHeight: this.props.maxHeight - mh });
};

ServicePlanDetailedInfoTabs.prototype.scrollToStart = function () {
    this.$body.scrollTo(0, 500);
};

ServicePlanDetailedInfoTabs.prototype.hideUpScroller = function () {
    this.$element.find('.up-scroller').hide();
};

ServicePlanDetailedInfoTabs.prototype.showUpScroller = function () {
    this.$element.find('.up-scroller').show();
};

ServicePlanDetailedInfoTabs.prototype.addOnScrollHandler = function (handler) {
    this.$body.on('scroll', handler);
};