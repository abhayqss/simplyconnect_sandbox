/**
 * Created by stsiushkevich on 12.09.18.
 */

define([
        'classnames',

        'redux',
        'redux-utils',

        path('app/redux/directory/incident/level/reporting/settings/incidentLevelReportingSettingsActions')
    ],
    function (
        cn,
        redux,
        utils,
        incidentLevelReportingSettingsActions
    ) {
        function renderTimeLinesSection (timeLines) {
            return {
                '<>': 'div', 'class': 'incident-level-reporting-settings-details_time-lines-section', 'html': [
                    {'<>': 'div', 'class': 'incident-level-reporting-settings-details_time-lines-section-title', 'text': 'Reporting timeline'},
                    {'<>': 'div', 'class': 'incident-level-reporting-settings-details_time-lines', 'text': timeLines}
                ]
            }
        }

        function renderRequirementsSection (requirements) {
            return {
                '<>': 'div', 'class': 'incident-level-reporting-settings-details_requirements-section', 'html': [
                    {'<>': 'div', 'class': 'incident-level-reporting-settings-details_requirements-section-title', 'text': 'Follow up requirements'},
                    {'<>': 'div', 'class': 'incident-level-reporting-settings-details_requirements', 'text': requirements}
                ]
            }
        }

        function mapStateToProps (state) {
            return {
                directory: state.directory
            }
        }

        function mapDispatchToProps(dispatch) {
            return {
                actions: {
                    directory: {
                        incident: {
                            level: {
                                reporting: {
                                    settings: redux.bindActionCreators(incidentLevelReportingSettingsActions, dispatch)
                                }
                            }
                        }
                    }
                }
            }
        }

        function IncidentLevelReportingSettingsDetails () {
            Widget.apply(this, arguments);
        }

        IncidentLevelReportingSettingsDetails.prototype = Object.create(Widget.prototype);
        IncidentLevelReportingSettingsDetails.prototype.constructor = IncidentLevelReportingSettingsDetails;

        IncidentLevelReportingSettingsDetails.prototype.getDefaultProps = function () {
            var props = Modal.prototype.getDefaultProps.apply(this);

            return $.extend({}, props, {
                level: 1
            });
        };

        IncidentLevelReportingSettingsDetails.prototype.componentDidMount = function () {
            this.$element = $('[cmp-id="'+ this.$$id +'"]');

            this.loader = new Loader({
                container: this.$element
            });

            this.loader.mount();

            this.load();
        };

        IncidentLevelReportingSettingsDetails.prototype.componentDidUpdate = function (prevProps, prevState) {
            var data = this.props.directory.incident.level.reporting.settings.data;
            var isLoading = this.props.directory.incident.level.reporting.settings.isFetching;
            var isLoadingPrev = prevProps.directory.incident.level.reporting.settings.isFetching;

            if (isLoading) this.loader.show();
            else this.loader.hide();

            if (isLoadingPrev && !isLoading && data) this.updateSections();
        };

        IncidentLevelReportingSettingsDetails.prototype.getStore = function () {
            return ExchangeApp.redux.store;
        };

        IncidentLevelReportingSettingsDetails.prototype.setLoading = function (isLoading) {
            if (isLoading) this.loader.show();
            else this.loader.hide();
        };

        IncidentLevelReportingSettingsDetails.prototype.load = function () {
            this
                .props
                .actions
                .directory
                .incident
                .level
                .reporting
                .settings
                .load({ level: this.props.level });
        };

        IncidentLevelReportingSettingsDetails.prototype.updateSections = function () {
            var data = this.props.directory.incident.level.reporting.settings.data;
            this.$element.json2html({}, renderTimeLinesSection(data.timeLines));
            this.$element.json2html({}, renderRequirementsSection(data.requirements));
        };

        IncidentLevelReportingSettingsDetails.prototype.render = function () {
            return {
                '<>': 'div',
                'class': cn('incident-level-reporting-settings-details'),
                'html': []
            }
        };

        return utils.connect(mapStateToProps, mapDispatchToProps)(IncidentLevelReportingSettingsDetails);
    }
);