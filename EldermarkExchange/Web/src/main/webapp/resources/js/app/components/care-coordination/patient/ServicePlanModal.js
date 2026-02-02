/**
 * Created by stsiushkevich on 12.09.18.
 */

var ServicePlanModal = (function ($) {

    var ARROW_TOP_IMAGE_URL = ExchangeApp.info.context + '/resources/images/arrow2-top.svg';
    var SCORING_TOOLTIP_IMAGE_URL = ExchangeApp.info.context + '/resources/images/info-tip-white.svg';

    var SCORE_GUIDELINES = [
        'Client has no capability or resource in the area; requires immediate intervention or correction; client is at serious risk if not addressed',
        'Client has limited capabilities in this area or current or historical resources are unreliable or inconsistent',
        'Client some resources or capability in this area; has developed workarounds or relies on caregivers/others for some assistance on a regular or consistent basis; could benefit from self-management development',
        'Client is generally capable of self-management or accomplishing issues but could benefit from tweaking or additional services/education; caregiver or external assistance is minimal or minor',
        'Does not apply or Client is fully capable of self-management or can accomplish all issues on their own'
    ];

    function ServicePlanModal() {
        Modal.apply(this, arguments);

        var isReadonlyPlan = this.props.isReadonlyPlan;

        this.state = {
            bodyMinHeight: null,
            isScrollerShowed: false,
            isCloseBtnShowed: isReadonlyPlan,
            isCancelBtnShowed: !isReadonlyPlan,
            isNextBtnShowed: false,
            isBackBtnShowed: false,
            isSaveBtnShowed: !isReadonlyPlan
        };

        this.scoringTooltip = null;
    }

    ServicePlanModal.prototype = Object.create(Modal.prototype);
    ServicePlanModal.prototype.constructor = ServicePlanModal;

    ServicePlanModal.prototype.getDefaultProps = function () {
        var me = this;

        var props = Modal.prototype.getDefaultProps.apply(this);

        return $.extend({}, props, {
            isStatic: true,
            isReadonlyPlan: false,
            footerHeight: 80,
            className: '',
            bodyExtraHeight: 30 * 2,
            getTitle: function () {
                if (me.props.isReadonlyPlan) return 'View Service Plan';
                if (me.props.planId >= 0) return 'Edit Service Plan';
                return 'Create Service Plan'
            },
            renderTitle: function () {
                var title = me.props.getTitle() || me.props.title;
                return {'<>': 'h4', 'class': 'modal-title', 'html': [
                    {'<>': 'div', 'class': 'modal-title__text', 'text': title},
                    {'<>': 'div', 'class': 'tooltip-btn hidden', 'html': [
                        {'<>': 'img', 'class': 'tooltip-btn__image', 'src': SCORING_TOOLTIP_IMAGE_URL}
                    ]}
                ]};
            },
            renderFooter: function () {
                return {
                    '<>': 'div', 'class': 'modal-footer scroller-container', 'html': [
                        {
                            '<>': 'button',
                            'class': 'btn btn-default close-btn ' + (!me.state.isCloseBtnShowed ? 'hidden' : ''),
                            'style': 'margin-right: 24px; ',
                            'data-dismiss': 'modal',
                            'text': 'CLOSE'
                        },
                        {
                            '<>': 'button',
                            'class': 'btn btn-default cancel-btn ' + (!me.state.isCancelBtnShowed ? 'hidden' : ''),
                            'style': 'margin-right: 24px; ',
                            'data-dismiss': 'modal',
                            'text': 'CANCEL'
                        },
                        {
                            '<>': 'button',
                            'class': 'btn btn-default back-btn ' + (!me.state.isBackBtnShowed ? 'hidden' : ''),
                            'style': 'margin-right: 24px',
                            'text': 'BACK',
                            'onclick': function () {
                                me.onBack();
                            }
                        },
                        {
                            '<>': 'button',
                            'class': 'btn btn-primary next-btn ' + (!me.state.isNextBtnShowed ? 'hidden' : ''),
                            'text': 'NEXT',
                            'onclick': function () {
                                me.onNext();
                            }
                        },
                        {
                            '<>': 'button',
                            'class': 'btn btn-primary save-btn ' + (!me.state.isSaveBtnShowed ? 'hidden' : ''),
                            'text': 'SAVE',
                            'onclick': function () {
                                me.onSave();
                            }
                        },
                        {'<>': 'div', 'title': 'Back to Top', 'class': 'up-scroller', 'html': [
                            {'<>': 'img', 'src': ARROW_TOP_IMAGE_URL}
                        ], 'onclick': function () { me.scrollToStart() }}
                    ]
                }
            },
            onLeavePlanUnchanged: function () {},
            onSavePlanSuccess: function () {},
            onSavePlanFailure: function () {},
            onLoadPlanFailure: function () {},
            onHidden: function () {}
        });
    };

    ServicePlanModal.prototype.componentDidMount = function () {
        Modal.prototype.componentDidMount.apply(this);

        var me = this;

        var planId = this.props.planId;
        var patientId = this.props.patientId;
        var planDateModified = this.props.planDateModified;

        var isReadonlyPlan = this.props.isReadonlyPlan;
        var isArchivedPlan = this.props.isArchivedPlan;

        this.scoringTooltip = new Popover({
            element: this.$header.find('.tooltip-btn'),
            className: 'custom-tooltip scoring-tooltip',
            isHtmlUsed: true,
            trigger: 'hover',
            viewport: {selector: this.$content.selector, padding: 10},
            onShow: function () {
                me.setState({bodyMinHeight: 400})
            },
            onHidden: function () {
                me.setState({bodyMinHeight: ''})
            },
            renderTitle: function () {
                return [
                    {'<>': 'div', 'class': 'scoring-tooltip__title-item score', 'text': 'Score'},
                    {'<>': 'div', 'class': 'scoring-tooltip__title-item parameters', 'text': 'Guidelines/Parameters'}
                ]
            },
            renderContent: function () {
                return [
                    _.map(SCORE_GUIDELINES, function (text, i) {
                        return {'<>': 'div', 'class': 'scoring-tooltip__content-row', 'html': [
                            {'<>': 'div', 'class': 'scoring-tooltip__content-cell score', 'text': String(SCORE_GUIDELINES.length - i)},
                            {'<>': 'div', 'class': 'scoring-tooltip__content-cell parameters', 'text': text}
                        ]};
                    })
                ]
            }
        });

        this.scoringTooltip.mount();

        if (isReadonlyPlan && !isArchivedPlan) {
            this.tabs = new ServicePlanDetailedInfoTabs({
                container: this.$body,
                planId: planId,
                patientId: patientId,
                planDateModified: planDateModified,
                currentUser: this.props.currentUser,
                onLoadPlanFailure: function (e) {
                    me.props.onLoadPlanFailure(e);
                },
                onViewArchivedPlan: function (data) {
                    me.props.onViewArchivedPlan(data)
                }
            });

            this.tabs.mount();

            this.addOnShownHandler(function () {
                var mh = parseInt(me.$body.css('max-height'));
                me.tabs.update({ maxHeight: mh });
            });

            this.addOnResizeHandler(function () {
                var mh = parseInt(me.$body.css('max-height'));
                me.tabs.update({ maxHeight: mh });
            });
        }

        else {
            this.details = new ServicePlanDetails({
                container: this.$body,
                planId: planId,
                patientId: patientId,
                isReadonly: isReadonlyPlan,
                currentUser: this.props.currentUser,
                onLoadSuccess: function (data) {
                    if (!isReadonlyPlan) {
                        if (data.needs && data.needs.length > 0) {
                            me.setState({
                                isNextBtnShowed: true,
                                isSaveBtnShowed: false
                            });
                        } else {
                            me.setState({
                                isSaveBtnShowed: true,
                                isNextBtnShowed: false
                            });
                        }
                    }
                },
                onLoadFailure: function (e) {
                    me.props.onLoadPlanFailure(e);
                },
                onValidationError: function () {
                    var items = me.details.getInvalidItems();
                    me.scrollTo(items.first())
                },
                onSaveSuccess: function () {
                    me.props.onSavePlanSuccess();
                },
                onSaveFailure: function () {
                    me.props.onSavePlanFailure();
                },
                onShowDatePicker: function () {
                    me.setState({bodyMinHeight: 510});
                },
                onHideDatePicker: function () {
                    me.setState({bodyMinHeight: ''});
                },
                onNeedSectionAdded: function (index) {
                    var need = me.details.getNeedSection(index);
                    me.scrollTo(need.$element);

                    if (!me.state.isNextBtnShowed) {
                        me.setState({
                            isNextBtnShowed: true,
                            isSaveBtnShowed: false
                        });
                    }
                },
                onNeedSectionRemoved: function () {
                    if (me.details.getNeedSectionCount() === 0) {
                        me.setState({
                            isNextBtnShowed: false,
                            isSaveBtnShowed: true
                        });
                    }
                },
                onGoalSectionAdded: function (needIndex, index) {
                    var need = me.details.getNeedSection(needIndex);
                    var goal = need.getGoalSection(index);
                    me.scrollTo(goal.$element);
                }
            });

            this.details.mount();
        }

        this.addOnHiddenHandler(function () {
            me.props.onHidden();
            me.unmount();
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
    };

    ServicePlanModal.prototype.componentDidUpdate = function (prevProps, prevState) {
        var bodyMinHeight = this.state.bodyMinHeight;

        if (bodyMinHeight !== prevState.bodyMinHeight) {
            this.$body.css({minHeight: bodyMinHeight});
        }

        var isScrollerShowed = this.state.isScrollerShowed;

        if (isScrollerShowed !== prevProps.isScrollerShowed) {
            if (isScrollerShowed) this.showUpScroller();
            else this.hideUpScroller();
        }

        var isCancelBtnShowed = this.state.isCancelBtnShowed;
        var isNextBtnShowed = this.state.isNextBtnShowed;

        var isBackBtnShowed = this.state.isBackBtnShowed;
        var isSaveBtnShowed = this.state.isSaveBtnShowed;

        if (isCancelBtnShowed !== prevState.isCancelBtnShowed) {
            var $btn = this.$footer.find('.cancel-btn');

            if (isCancelBtnShowed) $btn.removeClass('hidden');
            else $btn.addClass('hidden');
        }

        if (isNextBtnShowed !== prevState.isNextBtnShowed) {
            var $btn = this.$footer.find('.next-btn');

            if (isNextBtnShowed) $btn.removeClass('hidden');
            else $btn.addClass('hidden');
        }

        if (isBackBtnShowed !== prevState.isBackBtnShowed) {
            var $btn = this.$footer.find('.back-btn');

            var $text = this.$header.find('.modal-title__text');
            var $tooltipBtn = this.$header.find('.tooltip-btn');

            if (isBackBtnShowed) {
                $btn.removeClass('hidden');

                $text.text('Scoring');
                $tooltipBtn.removeClass('hidden');
            }
            else {
                $btn.addClass('hidden');

                var title = this.props.getTitle() || this.props.title;

                $text.text(title);
                $tooltipBtn.addClass('hidden');
            }
        }

        if (isSaveBtnShowed !== prevState.isSaveBtnShowed) {
            var $btn = this.$footer.find('.save-btn');

            if (isSaveBtnShowed) $btn.removeClass('hidden');
            else $btn.addClass('hidden');
        }
    };

    ServicePlanModal.prototype.scrollToStart = function () {
        this.$body.scrollTo(0, 500);
    };

    ServicePlanModal.prototype.scrollTo = function (target) {
        this.$body.scrollTo(target, 500);
    };

    ServicePlanModal.prototype.hideUpScroller = function () {
        this.$element.find('.up-scroller').hide();
    };

    ServicePlanModal.prototype.showUpScroller = function () {
        this.$element.find('.up-scroller').show();
    };

    ServicePlanModal.prototype.onBack = function () {
        this.setState({
            isCancelBtnShowed: true,
            isNextBtnShowed: true,
            isBackBtnShowed: false,
            isSaveBtnShowed: false
        });

        this.details.hideScoringSection();
        this.details.removeScoringSection();
    };

    ServicePlanModal.prototype.onNext = function () {
        if (this.details.isValid()) {
            this.setState({
                isCancelBtnShowed: false,
                isNextBtnShowed: false,
                isBackBtnShowed: true,
                isSaveBtnShowed: true
            });

            this.details.createScoringSection(false);
            this.details.showScoringSection();
        }
    };

    ServicePlanModal.prototype.onSave = function () {
        if (this.details.hasScoringSection()) {
            if (this.details.isChanged()) this.details.save();
            else this.props.onLeavePlanUnchanged();
        }

        else if (this.details.isValid()) {
            if (this.details.isChanged()) this.details.save();
            else this.props.onLeavePlanUnchanged();
        }
    };

    ServicePlanModal.prototype.addOnScrollHandler = function (handler) {
        this.$body.on('scroll', handler);
    };

    ServicePlanModal.prototype.render = function () {
        var isStatic = this.props.isStatic;

        var isScrollable = this.props.isScrollable;

        var isNew = !this.props.planId;
        var isReadonlyPlan = this.props.isReadonlyPlan;
        var isArchivedPlan = this.props.isArchivedPlan;

        var className = 'modal fade service-plan-modal ';

        var type = '';
        if (isNew) type += 'new';
        else if (isArchivedPlan) type += 'archived';
        else if (isReadonlyPlan)  type += 'readonly';
        else type += 'edit';

        if (type) className += type + ' ';

        return {
            '<>': 'div',
            'class': className + this.props.className + (!isScrollable ? 'non-scrollable' : ''),
            'data-backdrop': isStatic ? 'static' : true,
            'html': [
                {'<>': 'div', 'class': 'modal-dialog', 'style': 'width:1000px;', 'html': [
                    {'<>': 'div', 'class': 'modal-content', 'html': [
                        this.props.renderHeader(),
                        this.props.renderBody(),
                        this.props.renderFooter()
                    ]}
                ]}
            ]
        }
    };

    return ServicePlanModal;
})($);