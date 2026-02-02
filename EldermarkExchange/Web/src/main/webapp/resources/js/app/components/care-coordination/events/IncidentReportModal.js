/**
 * Created by stsiushkevich on 12.09.18.
 */

define([path('./IncidentReportForm')], function (Form) {

        var ARROW_TOP_IMAGE_URL = ExchangeApp.info.context + '/resources/images/arrow2-top.svg';

        function IncidentReportModal() {
            Modal.apply(this, arguments);

            var isReadonlyReport = this.props.isReadonlyReport;

            this.state = {
                isScrollerShowed: false,
                isReportChangeIgnored: false
            };

            this.scoringTooltip = null;
        }

        IncidentReportModal.prototype = Object.create(Modal.prototype);
        IncidentReportModal.prototype.constructor = IncidentReportModal;

        IncidentReportModal.prototype.getDefaultProps = function () {
            var me = this;

            var props = Modal.prototype.getDefaultProps.apply(this);

            return $.extend({}, props, {
                isStatic: true,
                isReadonlyReport: false,
                footerHeight: 80,
                className: '',
                bodyExtraHeight: 30 * 2,
                getTitle: function () {
                    if (me.props.isReadonlyReport) return 'View Incident Report';
                    if (me.props.reportId >= 0) return 'Edit Incident Report';
                    return 'Create Incident Report'
                },
                renderFooter: function () {
                    return {
                        '<>': 'div', 'class': 'modal-footer scroller-container', 'html': [
                            {
                                '<>': 'button',
                                'class': 'btn btn-default',
                                'style': 'margin-right: 24px;',
                                'text': 'SUBMIT',
                                'onclick': function () {
                                    me.onSubmit();
                                }
                            },
                            {
                                '<>': 'button',
                                'class': 'btn btn-primary save-btn',
                                'text': 'SAVE DRAFT',
                                'onclick': function () {
                                    me.onSaveDraft();
                                }
                            },
                            {
                                '<>': 'div', 'title': 'Back to Top', 'class': 'up-scroller', 'html': [
                                    {'<>': 'img', 'src': ARROW_TOP_IMAGE_URL}
                                ], 'onclick': function () {
                                    me.scrollToStart()
                                }
                            }
                        ]
                    }
                },
                onLeaveReportUnchanged: function () {
                },
                onSaveReportDraftSuccess: function () {
                },
                onSaveReportDraftFailure: function () {
                },
                onSubmitReportSuccess: function () {
                },
                onSubmitReportFailure: function () {
                },
                onLoadReportFailure: function () {
                },
                onHide: function () {
                },
                onHidden: function () {
                }
            });
        };

        IncidentReportModal.prototype.componentDidMount = function () {
            Modal.prototype.componentDidMount.apply(this);

            this.loader = new Loader({
                container: this.$body
            });

            this.loader.mount();

            var me = this;

            var eventId = this.props.eventId;
            var reportId = this.props.reportId;
            var patientId = this.props.patientId;
            var currentUser = this.props.currentUser;
            var reportDateModified = this.props.reportDateModified;
            var isReadonlyReport = this.props.isReadonlyReport;

            this.form = new Form({
                container: this.$body,
                eventId: eventId,
                reportId: reportId,
                patientId: patientId,
                isReadonly: isReadonlyReport,
                currentUser: currentUser,
                onLoading: function (isLoading) {
                    me.setLoading(isLoading);
                },
                onLoadFailure: function (e) {
                    me.props.onLoadReportFailure(e);
                },
                onValidationError: function () {
                    var fields = me.form.getInvalidFields();
                    me.scrollTo(fields.first())
                },
                onSubmitSuccess: function (data) {
                    me.props.onSubmitReportSuccess(data);
                },
                onSubmitFailure: function (e) {
                    me.props.onSubmitReportFailure(e);
                },
                onSaveDraftSuccess: function (data) {
                    me.props.onSaveReportDraftSuccess(data);
                },
                onSaveDraftFailure: function (e) {
                    me.props.onSaveReportDraftFailure(e);
                }
            });

            this.form.mount();

            this.addOnHideHandler(function (e) {
                me.props.onHide(e);
            });

            this.addOnHiddenHandler(function (e) {
                me.props.onHidden(e);
                me.unmount();
            });

            this.addOnScrollHandler(function (e) {
                var top = e.target.scrollTop;
                var h = e.target.clientHeight;

                var isShowed = me.state.isScrollerShowed;

                if (top > h / 2) {
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

        IncidentReportModal.prototype.componentDidUpdate = function (prevProps, prevState) {
            var isScrollerShowed = this.state.isScrollerShowed;

            if (isScrollerShowed !== prevProps.isScrollerShowed) {
                if (isScrollerShowed) this.showUpScroller();
                else this.hideUpScroller();
            }
        };

        IncidentReportModal.prototype.componentWillUnmount = function () {
            this.form.unmount();
        };

        IncidentReportModal.prototype.scrollToStart = function () {
            this.$body.scrollTo(0, 500);
        };

        IncidentReportModal.prototype.scrollTo = function (target) {
            this.$body.scrollTo(target, 500);
        };

        IncidentReportModal.prototype.hideUpScroller = function () {
            this.$element.find('.up-scroller').hide();
        };

        IncidentReportModal.prototype.showUpScroller = function () {
            this.$element.find('.up-scroller').show();
        };

        IncidentReportModal.prototype.onSubmit = function () {
            this.form.onSubmit();
        };

        IncidentReportModal.prototype.onSaveDraft = function () {
            this.form.onSaveDraft();
        };

        IncidentReportModal.prototype.addOnScrollHandler = function (handler) {
            this.$body.on('scroll', handler);
        };

        IncidentReportModal.prototype.isReportChanged = function (target) {
            return this.form.isChanged();
        };

        IncidentReportModal.prototype.isReportChangeIgnored = function (target) {
            return this.state.isReportChangeIgnored;
        };

        IncidentReportModal.prototype.ignoreReportChange = function () {
            return this.setState({isReportChangeIgnored: true});
        };

        IncidentReportModal.prototype.setLoading = function (isLoading) {
            if (isLoading) {
                this.loader.show();
                this.$body.css({overflow: 'hidden'})
            }

            else {
                this.loader.hide();
                this.$body.css({overflow: 'auto'})
            }
        };

        IncidentReportModal.prototype.render = function () {
            var isStatic = this.props.isStatic;

            var isScrollable = this.props.isScrollable;

            var isNew = !this.props.reportId;
            var isReadonlyReport = this.props.isReadonlyReport;

            var className = 'modal fade incident-report-modal ';

            var type = '';
            if (isNew) type += 'new';
            else if (isReadonlyReport) type += 'readonly';
            else type += 'edit';

            if (type) className += type + ' ';

            return {
                '<>': 'div',
                'class': className + this.props.className + (!isScrollable ? 'non-scrollable' : ''),
                'data-backdrop': isStatic ? 'static' : true,
                'html': [
                    {
                        '<>': 'div', 'class': 'modal-dialog', 'style': 'width:1000px;', 'html': [
                            {
                                '<>': 'div', 'class': 'modal-content', 'html': [
                                    this.props.renderHeader(),
                                    this.props.renderBody(),
                                    this.props.renderFooter()
                                ]
                            }
                        ]
                    }
                ]
            }
        };

        return IncidentReportModal;
    }
);