/**
 * Created by stsiushkevich on 12.09.18.
 */

define([
        path('app/lib/Utils'),
        path('./IncidentLevelReportingSettingsDetails')
    ],
    function (U, Details) {
    
        var LEVEL_TITLES = ['I', 'II', 'III'];        

        function IncidentLevelReportingSettingsModal() {
            Modal.apply(this, arguments);
        }

        IncidentLevelReportingSettingsModal.prototype = Object.create(Modal.prototype);
        IncidentLevelReportingSettingsModal.prototype.constructor = IncidentLevelReportingSettingsModal;

        IncidentLevelReportingSettingsModal.prototype.getDefaultProps = function () {
            var me = this;

            var props = Modal.prototype.getDefaultProps.apply(this);

            return $.extend({}, props, {
                level: 1,
                footerHeight: 80,
                className: '',
                bodyExtraHeight: 30 * 2,
                getTitle: function () {
                    return U.interpolate(
                        'Level $0 Incident Reporting', 
                        LEVEL_TITLES[me.props.level - 1]
                    )
                },
                renderFooter: function () {
                    return {
                        '<>': 'div', 'class': 'modal-footer', 'html': [
                            {
                                '<>': 'button',
                                'class': 'btn btn-default close-btn',
                                'text': 'CLOSE',
                                'data-dismiss': 'modal'
                            }
                        ]
                    }
                }
            });
        };

        IncidentLevelReportingSettingsModal.prototype.componentDidMount = function () {
            Modal.prototype.componentDidMount.apply(this);

            var me = this;

            var level = this.props.level;

            this.details = new Details({
                container: this.$body,
                level: level
            });

            this.details.mount();

            this.addOnHiddenHandler(function () {
                me.unmount();
            });
        };

        IncidentLevelReportingSettingsModal.prototype.componentDidUpdate = function (prevProps, prevState) {
            var isScrollerShowed = this.state.isScrollerShowed;

            if (isScrollerShowed !== prevProps.isScrollerShowed) {
                if (isScrollerShowed) this.showUpScroller();
                else this.hideUpScroller();
            }
        };

        IncidentLevelReportingSettingsModal.prototype.scrollToStart = function () {
            this.$body.scrollTo(0, 500);
        };

        IncidentLevelReportingSettingsModal.prototype.scrollTo = function (target) {
            this.$body.scrollTo(target, 500);
        };

        IncidentLevelReportingSettingsModal.prototype.hideUpScroller = function () {
            this.$element.find('.up-scroller').hide();
        };

        IncidentLevelReportingSettingsModal.prototype.showUpScroller = function () {
            this.$element.find('.up-scroller').show();
        };

        IncidentLevelReportingSettingsModal.prototype.onSubmit = function () {
            this.form.onSubmit();
        };

        IncidentLevelReportingSettingsModal.prototype.onSaveDraft = function () {
            this.form.onSaveDraft();
        };

        IncidentLevelReportingSettingsModal.prototype.addOnScrollHandler = function (handler) {
            this.$body.on('scroll', handler);
        };

        IncidentLevelReportingSettingsModal.prototype.render = function () {
            var isStatic = this.props.isStatic;
            
            return {
                '<>': 'div',
                'class': 'modal fade incident-level-reporting-settings-modal',
                'data-backdrop': isStatic ? 'static' : true,
                'html': [
                    {
                        '<>': 'div', 'class': 'modal-dialog', 'html': [
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

        return IncidentLevelReportingSettingsModal;
    }
);