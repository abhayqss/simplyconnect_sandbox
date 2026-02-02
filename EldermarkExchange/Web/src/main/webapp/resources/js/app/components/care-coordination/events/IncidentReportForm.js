/**
 * Created by stsiushkevich on 12.09.18.
 */

define(
    [
        'underscore',
        'redux',
        'redux-utils',
        path('app/lib/Utils'),
        path('app/lib/Constants'),

        path('./IncidentLevelReportingSettingsModal'),

        path('app/redux/event/details/eventDetailsActions'),
        path('app/redux/event/incident/report/form/incidentReportFormActions'),
        path('app/redux/event/incident/report/details/incidentReportDetailsActions'),
        path('app/redux/event/incident/report/initialized/incidentReportInitializedActions'),

        path('app/redux/profile/details/profileDetailsActions'),

        path('app/redux/patient/diagnosis/list/patientDiagnosisListActions'),
        path('app/redux/patient/medication/active/list/patientActiveMedicationListActions'),
        path('app/redux/patient/medication/inactive/list/patientInactiveMedicationListActions'),

        path('app/redux/directory/race/list/raceListActions'),
        path('app/redux/directory/state/list/stateListActions'),
        path('app/redux/directory/gender/list/genderListActions'),
        path('app/redux/directory/incident/type/list/incidentTypeListActions'),
        path('app/redux/directory/incident/place/list/incidentPlaceListActions'),
        path('app/redux/directory/classMember/type/list/classMemberTypeListActions'),
        path('app/redux/directory/incident/level/reporting/settings/incidentLevelReportingSettingsActions')
    ],
    function (
        _,
        redux,
        utils,
        U,
        Constants,

        IncidentLevelReportingSettingsModal,

        eventDetailsActions,
        incidentReportFormActions,
        incidentReportDetailsActions,
        incidentReportInitializedActions,

        profileDetailsActions,

        patientDiagnosisListActions,
        patientActiveMedicationListActions,
        patientInactiveMedicationListActions,

        raceListActions,
        stateListActions,
        genderListActions,
        incidentTypeListActions,
        incidentPlaceListActions,
        classMemberTypeListActions,
        incidentLevelReportingSettingsActions
    ) {
        var context = ExchangeApp.info.context;

        var ADD_IMAGE_URL = context + '/resources/images/add-button-rounded.svg';
        var REMOVE_IMAGE_URL = context + '/resources/images/trash.svg';
        var INFO_TIP_IMAGE_URL = context + '/resources/images/info-tip-gray.svg';

        var format = U.Date.format;

        var DATE_TIME_FORMAT = 'MM/dd/YYYY hh:mm';
        var DATE_FORMAT = U.Date.formats.americanMediumDate;

        var INCIDENT_TYPE_LEVELS = [1, 2, 3];

        var PLAIN_FIELDS = [
            'id',
            'clientName',
            'clientClassMemberId',
            'clientRIN',
            'clientBirthDate',
            'clientGenderId',
            'clientRaceId',
            'clientTransitionToCommunityDate',
            'clientClassMemberCurrentAddress',
            'agencyName',
            'agencyAddress',
            'qualityAdministrator',
            'careManagerOrStaffWithPrimServRespAndTitle',
            'careManagerOrStaffPhone',
            'careManagerOrStaffEmail',
            'mcoCareCoordinatorAndAgency',
            'mcoCareCoordinatorPhone',
            'mcoCareCoordinatorEmail',
            'wereIndividualsInvolvedInIncident',
            'incidentDateTime',
            'incidentDiscoveredDate',
            'wasProviderPresentOrScheduled',
            'wasIncidentCausedBySubstance',
            'incidentNarrative',
            'agencyResponseToIncident',
            'reportAuthor',
            'reportCompletedDate',
            'reportDate'
        ];

        function mapStateToProps (state) {
            return {
                event: state.event,
                profile: {
                    details: state.profile.details
                },
                patient: state.patient,
                directory: state.directory
            }
        }

        function mapDispatchToProps(dispatch) {
            return {
                actions: {
                    event: {
                        details: redux.bindActionCreators(eventDetailsActions, dispatch),
                        incident: {
                            report: {
                                form: redux.bindActionCreators(incidentReportFormActions, dispatch),
                                details: redux.bindActionCreators(incidentReportDetailsActions, dispatch),
                                initialized: redux.bindActionCreators(incidentReportInitializedActions, dispatch)
                            }
                        }
                    },
                    profile: {
                        details: redux.bindActionCreators(profileDetailsActions, dispatch)
                    },
                    patient: {
                        diagnosis: {
                            list: redux.bindActionCreators(patientDiagnosisListActions, dispatch)
                        },
                        medication: {
                            active: {
                                list: redux.bindActionCreators(patientActiveMedicationListActions, dispatch)
                            },
                            inactive: {
                                list: redux.bindActionCreators(patientInactiveMedicationListActions, dispatch)
                            }
                        }
                    },
                    directory: {
                        race: {
                            list: redux.bindActionCreators(raceListActions, dispatch)
                        },
                        state: {
                            list: redux.bindActionCreators(stateListActions, dispatch)
                        },
                        gender: {
                            list: redux.bindActionCreators(genderListActions, dispatch)
                        },
                        classMember: {
                            type: {
                                list: redux.bindActionCreators(classMemberTypeListActions, dispatch)
                            }
                        },
                        incident: {
                            type: {
                                list: redux.bindActionCreators(incidentTypeListActions, dispatch)
                            },
                            place: {
                                list: redux.bindActionCreators(incidentPlaceListActions, dispatch)
                            },
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

        function findIncidentTypeById(data, id) {
            var type = null;

            for (var i = 0; i < data.length; i++) {
                var o = data[i];

                if (o.id === id) type = o;

                else if (o['incidentTypes']) {
                    type = findIncidentTypeById(o['incidentTypes'], id);
                }

                if (type) break;
            }

            return type
        }

        function markAsChecked(items, checked, valueProp, subItemsProp) {
            return _.map(items, function (o) {
                var v = o[valueProp];

                o = $.extend({}, o, {
                    isChecked: _.isArray(checked) ? (
                        _.any(checked, function (item) {
                            return item[valueProp] === v
                        })
                    ) : (checked === v)
                });

                var subItems = subItemsProp && o[subItemsProp];
                if (subItems) o[subItemsProp] = markAsChecked(subItems, checked, valueProp, subItemsProp);

                return o;
            })
        }

        function renderCheckbox (props) {
            var cb = props.onChange;

            var input = {
                '<>': 'input',
                'type': 'checkbox',
                'name': props.name,
                'value': props.value,
                'onchange': function (o) {
                    cb && cb(props.value, $(o.event.target).prop('checked'))
                }
            };

            if (props.checked) {
                input['checked'] = true;
            }

            if (props.tooltipPosition) {
                input['data-tooltip-position'] = props.tooltipPosition;
            }

            return input;
        }

        function renderRadio (props) {
            var cb = props.onChange;

            var input = {
                '<>': 'input',
                'type': 'radio',
                'name': props.name,
                'value': props.value,
                'onchange': function (o) {
                    cb && cb(props.name, props.value, $(o.event.target).prop('checked'))
                }
            };

            if (props.checked) {
                input['checked'] = true;
            }

            if (props.tooltipPosition) {
                input['data-tooltip-position'] = props.tooltipPosition;
            }

            return input;
        }

        function renderDiagnosis (v) {
            return {
                '<>': 'div',
                'class': 'incident-report-form__diagnosis',
                'text': v || ''
            }
        }

        function renderMedication (v) {
            return {
                '<>': 'div',
                'class': 'incident-report-form__medication',
                'text': v || ''
            }
        }

        function renderIndividual (props) {
            var index = props.index;
            var onChange = props.onChange;

            return {'<>': 'div', 'class': 'row individual', 'html': [
                {'<>': 'div', 'class': 'col-md-4', 'html': [
                    {'<>': 'div', 'class': 'form-group', 'html': [
                        {'<>': 'label', 'text': "First and last name"},
                        {
                            '<>': 'input',
                            'name': U.interpolate('individual-$0-name', index),
                            'type': 'text',
                            'class': 'form-control',
                            'placeholder': '',
                            'value': props.name || '',
                            'onchange': function (o) {
                                o.event.stopPropagation();
                                onChange(index, 'name', $(o.event.target).val())
                            }
                        }
                    ]}
                ]},
                {'<>': 'div', 'class': 'col-md-4', 'html': [
                    {'<>': 'div', 'class': 'form-group', 'html': [
                        {'<>': 'label', 'text': "Relationship"},
                        {
                            '<>': 'input',
                            'name': U.interpolate('individual-$0-relationship', index),
                            'type': 'text',
                            'class': 'form-control',
                            'placeholder': '',
                            'value': props.relationship || '',
                            'onchange': function (o) {
                                o.event.stopPropagation();
                                onChange(index, 'relationship', $(o.event.target).val())
                            }
                        }
                    ]}
                ]},
                {'<>': 'div', 'class': 'col-md-4', 'html': [
                    {'<>': 'div', 'class': 'form-group', 'html': [
                        {'<>': 'label', 'text': "Phone #"},
                        {
                            '<>': 'input',
                            'name': U.interpolate('individual-$0-phone', index),
                            'type': 'text',
                            'class': 'form-control',
                            'placeholder': '',
                            'value': props.phone || '',
                            'onchange': function (o) {
                                o.event.stopPropagation();
                                onChange(index, 'phone', $(o.event.target).val())
                            }
                        }
                    ]}
                ]},
                {
                    '<>': 'a',
                    'class': 'remove-button individual-remove-button',
                    'onclick': function () {
                        props.onRemove(props.index);
                    },
                    'html': [
                        {'<>': 'img', 'src': REMOVE_IMAGE_URL, 'class': 'individual-remove-button__icon'}
                    ]
                }
            ]};
        }

        /*
        * data: {id, text, isFreeText, name, label, isChecked, onChange}
        * */
        function renderIncidentPlace (props) {
            var id = props.id;
            var isChecked = props.isChecked;

            return {'<>': 'div', 'class': 'checkbox incident-place', 'html': [
                {'<>': 'label', 'html': [
                    renderCheckbox({
                        value: id,
                        name: 'incidentPlaces',
                        checked: isChecked,
                        tooltipPosition: 'right',
                        onChange: function (value, checked) {
                            props.onChange(value, null, checked)
                        }
                    }),
                    {'<>': 'span', 'text': props.label || ''}
                ]},
                props.isFreeText ? {'<>': 'div', 'class': 'form-group incident-place__text', 'html': [
                    {
                        '<>': 'input',
                        'name': 'incidentPlaceText',
                        'type': 'text',
                        'class': 'form-control',
                        'placeholder': '',
                        'value': props.text || '',
                        'onchange': function (o) {
                            props.onChange(id, $(o.event.target).prop('value'), isChecked)
                        }
                    }
                ]} : undefined
            ]}
        }

        /*
        * data: {id, level, text, isFreeText, name, title, incidentTypes, isChecked, onChange}
        * */
        function renderIncidentType (props) {
            var id = props.id;
            var level = props.level;
            var isChecked = props.isChecked;

            var checkbox = {'<>': 'div', 'class': 'checkbox incident-type', 'html': [
                {'<>': 'label', 'html': [
                    renderCheckbox({
                        value: id,
                        name: 'level' + level,
                        checked: isChecked,
                        onChange: function (value, checked) {
                            props.onChange(level, value, null, checked)
                        }
                    }),
                    {'<>': 'span', 'text': props.title}
                ]},
                props.isFreeText ? {'<>': 'div', 'class': 'form-group incident-type__text', 'html': [
                    {
                        '<>': 'input',
                        'name': 'incidentTypeText',
                        'type': 'text',
                        'class': 'form-control',
                        'placeholder': '',
                        'value': props.text || '',
                        'onchange': function (o) {
                            props.onChange(level, id, $(o.event.target).prop('value'), isChecked)
                        }
                    }
                ]} : undefined
            ]};

            return props['incidentTypes'] ? {
                '<>': 'div', 'html': [
                    checkbox,
                    {
                        '<>': 'div',
                        'class': U.interpolate('incident-level-$0-section__specific-incident-types', level),
                        'html': [
                            _.map(props['incidentTypes'], function (o) {
                                return renderIncidentType($.extend({}, o, {
                                    onChange: props.onChange
                                }));
                            })
                        ]
                    }
                ]
            } : checkbox
        }

        function renderIncidentLevelTypesSectionHeader(level, onSettings) {
            return {
                '<>': 'h5',
                'html': [
                    {
                        '<>': 'div',
                        'class': U.interpolate('incident-level-$0-types-section__title', level),
                        'text': [
                            'Level I – Urgent; Critical Incident',
                            'Level II – Serious; Reportable Incident',
                            'Level III – Significant; Reportable Incident'
                        ][level - 1]
                    },
                    {
                        '<>': 'button',
                        'type': 'button',
                        'class': U.interpolate('incident-level-$0-types-section__incident-reporting-settings', level),
                        'html': [
                            {
                                '<>': 'img',
                                'src': INFO_TIP_IMAGE_URL,
                                'class': U.interpolate('incident-level-$0-types-section__incident-reporting-settings-icon', level)
                            }
                        ],
                        'onclick': function () {
                            onSettings(level)
                        }
                    }
                ],
                'class': U.interpolate('section__header incident-level-$0-types-section__header', level)
            }
        }

        function IncidentReportForm() {
            Form.apply(this, arguments);

            this.validator = null;

            var data = this.props.data;

            this.state = {
                count: 0,
                isValid: true,
                isDraft: false,
                isLoading: false,
                isChanged: false,
                isInitialized: false,
                arePlainFieldsUpdated: false,

                areReportDetailsLoaded: false,
                isInitializedReportLoaded: false,
                areClassMemberTypesLoaded: false,
                areGendersLoaded: false,
                areRacesLoaded: false,
                areIncidentPlacesLoaded: false,
                areIncidentTypesLoaded: false,
                areStatesLoaded: false,

                data: $.extend({
                    activeMedications: [],
                    currentDiagnoses: [],
                    wereIndividualsInvolvedInIncident: false,
                    incidentPlaces: [],
                    incidentInvolvedIndividuals: [],
                    level1IncidentTypes: [],
                    level2IncidentTypes: [],
                    level3IncidentTypes: []
                }, data)
            };
        }

        IncidentReportForm.prototype = Object.create(Form.prototype);
        IncidentReportForm.prototype.constructor = IncidentReportForm;

        IncidentReportForm.prototype.getDefaultProps = function () {
            return $.extend(
                {},
                {
                    fields: {},
                    isReadonly: false,
                    currentUser: null,
                    onLoading: function () {}
                }
            );
        };

        IncidentReportForm.prototype.componentDidMount = function () {
            Form.prototype.componentDidMount.apply(this);

            if (this.isNew()) this.loadInitializedReport();
            else this.loadReportDetails();

            this.$element
                .find('input[type="checkbox"], input[type="radio"]')
                .styler();

            var isReadonly = this.props.isReadonly;

            var me = this;
            this.$element.find('input[data-type="date"], input[data-type="datetime"]').each(function () {
                var $input = $(this);
                var type = $input.data('type');
                var name = $input.attr('name');

                var opts = {format: 'MM/DD/YYYY', keepOpen: true};

                if (type === 'datetime') {
                    opts.sideBySide = true;
                    opts.format = 'MM/DD/YYYY hh:mm A';
                }

                if (name === 'clientBirthDate') {
                    opts.maxDate = 'now';
                }

                $input
                    .datetimepicker(opts)
                    .on("dp.change", function (e) {
                        if (me.state.arePlainFieldsUpdated) me.onChange(e);
                    })
            });

            this.$element
                .find('.info-tip')
                .popover();

            this.$element.find('input, select, textarea').each(function () {
                var $elem = $(this);

                if ($elem.is('textarea[data-autoresizable="true"]')) $elem.autoresize();

                if (isReadonly) {
                    $elem.attr($elem.is('select') ? 'disabled' : 'readonly', 'true');
                }
            });

            this.addOnChangeHandler(function (e) {
                me.onChange(e)
            });
        };

        IncidentReportForm.prototype.componentDidUpdate = function (prevProps, prevState) {
            this.updateLoading();

            var isInitialized = this.state.isInitialized;

            if (isInitialized && !prevState.isInitialized) {
                this.updatePlainFields();
            }

            var areReportDetailsLoaded = this.state.areReportDetailsLoaded;
            var isInitializedReportLoaded = this.state.isInitializedReportLoaded;

            if ((areReportDetailsLoaded && ! prevState.areReportDetailsLoaded)
            || (isInitializedReportLoaded && !prevState.isInitializedReportLoaded)) {
                this.init();

                this.loadClassMemberTypes();

                this.loadGenders();

                this.loadRaces();

                this.loadIncidentPlaces();

                this.loadIncidentTypes();

                this.loadStates();
            }

            var data = this.state.data;

            if (!prevState.data.activeMedications.length
                && data.activeMedications.length) {
                this.updateActiveMedicationsPanel();
            }

            if (!prevState.data.currentDiagnoses.length
                && data.currentDiagnoses.length) {
                this.updateCurrentDiagnosesPanel();
            }

            var areClassMemberTypesLoaded = this.state.areClassMemberTypesLoaded;

            if (areClassMemberTypesLoaded && !prevState.areClassMemberTypesLoaded) {
                this.updateClassMemberTypesSection()
            }

            var areStatesLoaded = this.state.areStatesLoaded;

            if (areStatesLoaded && !prevState.areStatesLoaded) {
                this.updateAgencyStateField();
            }

            var areGendersLoaded = this.state.areGendersLoaded;

            if (areGendersLoaded && !prevState.areGendersLoaded) {
                this.updateClientGenderField();
            }

            var areRacesLoaded = this.state.areRacesLoaded;

            if (areRacesLoaded && !prevState.areRacesLoaded) {
                this.updateClientRaceField();
            }

            var areIncidentPlacesLoaded = this.state.areIncidentPlacesLoaded;

            if (areIncidentPlacesLoaded && !prevState.areIncidentPlacesLoaded) {
                this.updateIncidentPlacesField();
            }

            if (data.incidentPlaces !== prevState.data.incidentPlaces) {
                this.updateIncidentPlacesField();
            }

            if (data.wereIndividualsInvolvedInIncident
                && !prevState.data.wereIndividualsInvolvedInIncident) {
                this.setVisibleAddIncidentInvolvedIndividualBtn(true);
                this.setVisibleIncidentInvolvedIndividuals(true);
            }

            if (!data.wereIndividualsInvolvedInIncident &&
                prevState.data.wereIndividualsInvolvedInIncident) {
                this.setVisibleAddIncidentInvolvedIndividualBtn(false);
                this.setVisibleIncidentInvolvedIndividuals(false);
            }

            if (data.incidentInvolvedIndividuals
                !== prevState.data.incidentInvolvedIndividuals) {
                this.updateIncidentInvolvedIndividuals();

                if (data.wereIndividualsInvolvedInIncident) {
                    this.triggerWereIndividualsInvolvedInIncidentField(true);
                }
            }

            var areIncidentTypesLoaded = this.state.areIncidentTypesLoaded;

            if (areIncidentTypesLoaded && !prevState.areIncidentTypesLoaded) {
                var me = this;
                _.each(INCIDENT_TYPE_LEVELS, function (i) {
                    me.updateIncidentLevelSection(i)
                });
            }

            var level1IncidentTypes = data.level1IncidentTypes;

            if (level1IncidentTypes !== prevState.data.level1IncidentTypes) {
                this.updateIncidentLevelSection(1);
            }

            var level2IncidentTypes = data.level2IncidentTypes;

            if (level2IncidentTypes !== prevState.data.level2IncidentTypes) {
                this.updateIncidentLevelSection(2);
            }

            var level3IncidentTypes = data.level3IncidentTypes;

            if (level3IncidentTypes !== prevState.data.level3IncidentTypes) {
                this.updateIncidentLevelSection(3);
            }
        };

        IncidentReportForm.prototype.onChange = function (e) {
            var $elem = $(e.target);

            var type = $elem.attr('data-type') || $elem.attr('type');

            if (!['radio', 'checkbox'].includes(type)) {
                var name = $elem.attr('name');

                var value = $elem.val();

                if (['date', 'datetime'].includes(type)) {
                    value = e.date.toDate().getTime();
                }

                this.changeField(name, value);
            }
        };

        IncidentReportForm.prototype.onSubmit = function (e) {
            this.setDraft(false);

            var me = this;
            this.isValid().then(function (isValid) {
                if (isValid) me.submit();
            });
        };

        IncidentReportForm.prototype.onSaveDraft = function (e) {
            this.setDraft();

            var me = this;
            this.isValid().then(function (isValid) {
                if (isValid) me.saveDraft();
            });
        };

        IncidentReportForm.prototype.onAddIncidentInvolvedIndividual = function () {
            this.addIncidentInvolvedIndividual({});
        };

        IncidentReportForm.prototype.onIncidentLevelReportingSettings = function (level) {
            this.incidentLevelReportingSettingsModal = new IncidentLevelReportingSettingsModal({
                container: document.body,
                level: level,
                isOpen: true
            })

            this.incidentLevelReportingSettingsModal.mount();
        };

        IncidentReportForm.prototype.addIncidentInvolvedIndividual = function (individual) {
            var data = this.state.data;
            var individuals = data.incidentInvolvedIndividuals;

            this.setState({
                data: $.extend({}, data, {
                    incidentInvolvedIndividuals: _.union(individuals, [{
                        index: individuals.length || 0,
                        name: individual.name || '',
                        relationship: individual.relationship || '',
                        phone: individual.phone || ''
                    }])
                })
            });

            this.registerChange();
        };

        IncidentReportForm.prototype.removeIncidentInvolvedIndividual = function (index) {
            var data = $.extend({}, this.state.data);

            var individuals = _.filter(data.incidentInvolvedIndividuals, function (o, i) {
                return i !== index;
            });

            data.incidentInvolvedIndividuals = _.map(individuals, function (o, i) {
                return $.extend({}, o, {index: i});
            });

            this.setState({data: data});
            this.registerChange();
        };

        IncidentReportForm.prototype.changeIncidentInvolvedIndividual = function (index, field, value) {
            var data = $.extend({}, this.state.data);

            var change = {};
            change[field] = value;

            var individuals = data.incidentInvolvedIndividuals;

            data.incidentInvolvedIndividuals = _.map(individuals, function (o, i) {
                return i === index ? $.extend({}, o, change) : o;
            });

            this.setState({data: data});
            this.registerChange();
        };

        IncidentReportForm.prototype.changeIncidentPlace = function (id, text, checked) {
            var data = $.extend({}, this.state.data);

            var places = data.incidentPlaces;

            if (checked || text) {
                var place = _.findWhere(places, { id: id });

                if (place) place.text = text;

                else data.incidentPlaces = _.union(places, [{
                    id: id,
                    text: text || ''
                }])
            }

            else data.incidentPlaces = _.reject(places, function (type) {
                return type.id === id;
            });

            this.setState({data: data});
            this.registerChange();
        };

        IncidentReportForm.prototype.changeIncidentTypesField = function (level, id, text, checked) {
            var data = $.extend({}, this.state.data);

            var field = U.interpolate('level$0IncidentTypes', level);

            var types = data[field];

            if (checked || text) {
                var type = _.findWhere(types, { id: id });

                if (type) type.text = text;

                else data[field] = _.union(types, [{
                    id: id,
                    text: text || ''
                }])
            }

            else data[field] = _.reject(types, function (type) {
                return type.id === id;
            });

            this.setState({data: data});
            this.registerChange();
        };

        IncidentReportForm.prototype.getStore = function () {
            return ExchangeApp.redux.store;
        };

        IncidentReportForm.prototype.addOnChangeHandler = function (handler) {
            this.$element.on('change', handler)
        };

        IncidentReportForm.prototype.changeField = function (name, value) {
            var data = $.extend({}, this.state.data);
            data[name] = value;

            this.setState({data: data});
            this.registerChange();
        };

        IncidentReportForm.prototype.loadReportDetails = function () {
            var me = this;

            this.props
                .actions
                .event
                .incident
                .report
                .details
                .load(this.props.reportId)
                .then(function () {
                    me.setState({areReportDetailsLoaded: true});
                })
        };

        IncidentReportForm.prototype.loadInitializedReport = function () {
            var me = this;

            this.props
                .actions
                .event
                .incident
                .report
                .initialized
                .load(this.props.eventId)
                .then(function () {
                    me.setState({isInitializedReportLoaded: true});
                })
        };

        IncidentReportForm.prototype.changeFields = function (changes) {
            this.setState({
                data: $.extend({}, this.state.data, changes)
            });

            this.registerChange();
        };

        IncidentReportForm.prototype.addValidation = function () {
            var isDraft = this.state.isDraft;

            var rules = {
                clientClassMemberTypeId: {required: true},
                clientRIN: {required: !isDraft, maxlength: 256},
                clientBirthDate: {required: true},
                clientGenderId: {required: true},
                clientRaceId: {required: true},
                clientTransitionToCommunityDate: {required: !isDraft},
                clientClassMemberCurrentAddress: {required: !isDraft, maxlength: 256},
                agencyName: {required: !isDraft, maxlength: 256},
                agencyAddress: {required: !isDraft, maxlength: 256},
                /*agencyStateId: {required: !isDraft},
                agencyCity: {required: !isDraft},
                agencyStreet: {required: !isDraft},
                agencyZipCode: {required: !isDraft, digits: true, minlength: 5, maxlength: 5},*/
                qualityAdministrator: {required: !isDraft, maxlength: 256},
                careManagerOrStaffWithPrimServRespAndTitle: {required: !isDraft, maxlength: 256},
                /*careManagerOrStaffWithPSR: {required: !isDraft, maxlength: 256},
                careManagerOrStaffTitle: {maxlength: 256},*/
                careManagerOrStaffPhone: {required: !isDraft, digits: true, maxlength: 16},
                careManagerOrStaffEmail: {required: !isDraft, emails: true},
                mcoCareCoordinatorAndAgency: {required: !isDraft, maxlength: 256},
                mcoCareCoordinatorPhone: {required: !isDraft, digits: true, maxlength: 16},
                mcoCareCoordinatorEmail: {required: !isDraft, emails: true},
                incidentDateTime: {required: !isDraft},
                incidentDiscoveredDate: {required: !isDraft},
                wasProviderPresentOrScheduled: {required: !isDraft},
                incidentPlaces: {required: !isDraft},
                incidentNarrative: {maxlength: 20000},
                agencyResponseToIncident: {maxlength: 20000},
                reportAuthor: {required: true, maxlength: 256},
                reportCompletedDate: {required: true},
                reportDate: {required: true}
            };

            _.each(this.state.data.incidentInvolvedIndividuals, function (o, i) {
                rules[U.interpolate('individual-$0-name', i)] = {
                    required: true,
                    maxlength: 512
                };

                rules[U.interpolate('individual-$0-relationship', i)] = {
                    required: true,
                    maxlength: 256
                };

                rules[U.interpolate('individual-$0-phone', i)] = {
                    required: true,
                    digits: true,
                    maxlength: 16
                };
            });

            var emptyFieldText = getErrorMessage("field.empty");

            var onlyDigitsText = getErrorMessage("field.digits");
            var emailFieldText = getErrorMessage("field.email");

            var maxLength256Text = getMaxLengthErrorMsg(256);
            var maxLength512Text = getMaxLengthErrorMsg(512);
            var maxLength20000Text = getMaxLengthErrorMsg(20000);

            var minLength16Text = getMinLengthErrorMsg(16);
            var maxLength16Text = getMaxLengthErrorMsg(16);

            var minLength5Text = getMinLengthErrorMsg(5);
            var maxLength5Text = getMaxLengthErrorMsg(5);

            var messages = {
                clientClassMemberTypeId: {required: emptyFieldText},
                clientRIN: {required: emptyFieldText, maxlength: maxLength256Text},
                clientBirthDate: {required: emptyFieldText},
                clientGenderId: {required: emptyFieldText},
                clientRaceId: {required: emptyFieldText},
                clientTransitionToCommunityDate: {required: emptyFieldText},
                clientClassMemberCurrentAddress: {required: emptyFieldText, maxlength: maxLength256Text},
                agencyName: {required: emptyFieldText, maxlength: maxLength256Text},
                agencyAddress: {required: emptyFieldText, maxlength: maxLength256Text},
                /*agencyStateId: {required: emptyFieldText},
                agencyCity: {required: emptyFieldText},
                agencyStreet: {required: emptyFieldText},
                agencyZipCode: {
                    required: emptyFieldText,
                    digits: onlyDigitsText,
                    minlength: equalLength5Text,
                    maxlength: equalLength5Text
                },*/
                qualityAdministrator: {required: emptyFieldText, maxlength: maxLength256Text},
                careManagerOrStaffWithPrimServRespAndTitle: {required: emptyFieldText, maxlength: maxLength256Text},
                /*careManagerOrStaffWithPSR: {required: emptyFieldText, maxlength: maxLength256Text},
                careManagerOrStaffTitle: {maxlength: maxLength256Text},*/
                careManagerOrStaffPhone: {
                    required: emptyFieldText,
                    digits: onlyDigitsText,
                    maxlength: maxLength16Text
                },
                careManagerOrStaffEmail: {required: emptyFieldText, emails: emailFieldText},
                mcoCareCoordinatorAndAgency: {required: emptyFieldText, maxlength: maxLength256Text},
                mcoCareCoordinatorPhone: {
                    required: emptyFieldText,
                    digits: onlyDigitsText,
                    maxlength: maxLength16Text
                },
                mcoCareCoordinatorEmail: {required: emptyFieldText, emails: emailFieldText},
                incidentDateTime: {required: emptyFieldText},
                incidentDiscoveredDate: {required: emptyFieldText},
                wasProviderPresentOrScheduled: {required: emptyFieldText},
                incidentPlaces: {required: emptyFieldText},
                incidentNarrative: {maxlength: maxLength20000Text},
                agencyResponseToIncident: {maxlength: maxLength20000Text},
                reportAuthor: {required: emptyFieldText, maxlength: maxLength256Text},
                reportCompletedDate: {required: emptyFieldText},
                reportDate: {required: emptyFieldText}
            };

            _.each(this.state.data.incidentInvolvedIndividuals, function (o, i) {
                messages[U.interpolate('individual-$0-name', i)] = {
                    required: emptyFieldText,
                    maxlength: maxLength512Text
                };

                messages[U.interpolate('individual-$0-relationship', i)] = {
                    required: emptyFieldText,
                    maxlength: maxLength256Text
                };

                messages[U.interpolate('individual-$0-phone', i)] = {
                    required: emptyFieldText,
                    digits: onlyDigitsText,
                    maxlength: maxLength16Text
                };
            });

            function getLengthEqualErrorMsg(v) {
                return 'Please enter ' + v + ' symbols.'
            }

            function getMinLengthErrorMsg(v) {
                return 'Please enter at least ' + v + ' symbols.'
            }

            function getMaxLengthErrorMsg(v) {
                return 'Please enter no more than ' + v + ' symbols.'
            }

            this.validator = this.$element.validate(
                new ExchangeApp.utils.wgt.Validation({
                    position: 'top', rules: rules, messages: messages
                })
            )
        };

        IncidentReportForm.prototype.updateValidation = function (isDraft) {
            if (this.validator) this.validator.destroy();
            this.addValidation();
        };

        IncidentReportForm.prototype.isNew = function () {
            return U.isEmpty(this.props.reportId)
        };

        IncidentReportForm.prototype.isValid = function () {
            var me = this;
            return U.defer().then(function () {
                me.updateValidation();

                var isValid = me.$element.valid();

                me.setState({
                    isValid: isValid,
                    count: me.state.count + 1
                });

                if (!isValid) me.props.onValidationError();

                return isValid
            });
        };

        IncidentReportForm.prototype.updateLoading = function () {
            var s = this.state;

            var areAllLoaded = (s.areReportDetailsLoaded || s.isInitializedReportLoaded)
                && s.areClassMemberTypesLoaded
                && s.areGendersLoaded
                && s.areRacesLoaded
                && s.areIncidentPlacesLoaded
                && s.areIncidentTypesLoaded
                && s.areStatesLoaded;

            if (s.isLoading && areAllLoaded) {
                this.props.onLoading(false);
                this.setState({ isLoading: false });
            }

            if (!s.isLoading && !areAllLoaded) {
                this.props.onLoading(true);
                this.setState({ isLoading: true });
            }
        };

        IncidentReportForm.prototype.shouldValidate = function () {
            return this.state.count > 0 && !this.state.isValid;
        };

        IncidentReportForm.prototype.scrollToStart = function () {
            $(this.props.container).scrollTo(
                this.$element.find('.start-anchor'),
                500
            );
        };

        IncidentReportForm.prototype.loadStates = function () {
            var me = this;

            this
                .props
                .actions
                .directory
                .state
                .list
                .load()
                .then(function () {
                    me.setState({areStatesLoaded: true})
                });
        };

        IncidentReportForm.prototype.loadIncidentTypes = function () {
            var me = this;

            this
                .props
                .actions
                .directory
                .incident
                .type
                .list
                .load()
                .then(function () {
                    me.setState({areIncidentTypesLoaded: true})
                });
        };

        IncidentReportForm.prototype.loadIncidentPlaces = function () {
            var me = this;

            this
                .props
                .actions
                .directory
                .incident
                .place
                .list
                .load()
                .then(function () {
                    me.setState({areIncidentPlacesLoaded: true})
                });
        };

        IncidentReportForm.prototype.loadClassMemberTypes = function () {
            var me = this;

            this
                .props
                .actions
                .directory
                .classMember
                .type
                .list
                .load()
                .then(function () {
                    me.setState({areClassMemberTypesLoaded: true})
                });
        };

        IncidentReportForm.prototype.loadGenders = function () {
            var me = this;

            this
                .props
                .actions
                .directory
                .gender
                .list
                .load()
                .then(function () {
                    me.setState({areGendersLoaded: true})
                });
        };

        IncidentReportForm.prototype.loadRaces = function () {
            var me = this;

            this
                .props
                .actions
                .directory
                .race
                .list
                .load()
                .then(function () {
                    me.setState({areRacesLoaded: true})
                });
        };

        IncidentReportForm.prototype.updateClassMemberTypesSection = function () {
            var me = this;

            var $section = this.$element.find('.class-member-types-section');
            $section.empty();

            var id = this.state.data.clientClassMemberTypeId;
            var types = this.props.directory.classMember.type.list.dataSource.data;

            _.each(types, function (o) {
                $section.json2html({}, {
                    '<>': 'div', 'class': 'radio', 'html': [
                        {
                            '<>': 'label', 'html': [
                                renderRadio({
                                    value: o.id,
                                    name: 'clientClassMemberTypeId',
                                    checked: id === o.id,
                                    onChange: function (name, value) {
                                        me.changeField(name, value);
                                        if (me.shouldValidate()) me.isValid();
                                    }
                                }),
                                {'<>': 'span', 'text': o.label}
                            ]
                        }
                    ]
                });
            });

            $section
                .find('input[type="checkbox"], input[type="radio"]')
                .styler();
        };

        IncidentReportForm.prototype.updateClientGenderField = function () {
            var genders = this.props.directory.gender.list.dataSource.data;

            if (genders) {
                var clientRaceId = this.state.data.clientGenderId;

                var $select = $(this.getElement('clientGenderId')).empty();

                _.each(_.union([{ label: 'Select' }], genders), function (o) {

                    var option = {'<>': 'option', 'value': o.id, 'text': o.label || ''};

                    if (o.label === 'Select') $.extend(option, {'value': '', 'hidden': true}, !clientRaceId && {'selected': true});
                    if (o.id === clientRaceId) $.extend(option, {'selected': true});

                    $select.json2html({}, option);
                });
            }
        };

        IncidentReportForm.prototype.updateClientRaceField = function () {
            var races = this.props.directory.race.list.dataSource.data;

            if (races) {
                var clientRaceId = this.state.data.clientRaceId;

                var $select = $(this.getElement('clientRaceId')).empty();

                _.each(_.union([{ label: 'Select' }], races), function (o) {

                    var option = {'<>': 'option', 'value': o.id, 'text': o.label || ''};

                    if (o.label === 'Select') $.extend(option, {'value': '', 'hidden': true}, !clientRaceId && {'selected': true});
                    if (o.id === clientRaceId) $.extend(option, {'selected': true});

                    $select.json2html({}, option);
                });
            }
        };

        IncidentReportForm.prototype.updateIncidentPlacesField = function () {
            var places = this.props.directory.incident.place.list.dataSource.data;

            if (places) {
                var $list = this.$element.find('.incident-place-list');
                $list.empty();

                var checked = this.state.data.incidentPlaces;

                var me = this;
                _.each(places, function (o) {
                    var p = _.findWhere(checked, { id: o.id }) || {};

                    $list.json2html({}, renderIncidentPlace($.extend({}, o, p, {
                        isChecked: U.isNotEmpty(p),
                        onChange: function (id, text, checked) {
                            me.changeIncidentPlace(id, text, checked);
                            if (me.shouldValidate()) me.isValid();
                        }
                    })))
                });

                $list
                    .find('input[type="checkbox"], input[type="radio"]')
                    .styler();
            }
        };

        IncidentReportForm.prototype.setVisibleAddIncidentInvolvedIndividualBtn = function (isVisible) {
            var $btn = this.$element.find('.add-incident-involved-individual-btn');
            if (isVisible) $btn.show();
            else $btn.hide();
        };

        IncidentReportForm.prototype.setVisibleIncidentInvolvedIndividuals = function (isVisible) {
            var $individual = this.$element.find('.individual');
            if (isVisible) $individual.show();
            else $individual.hide();
        };

        IncidentReportForm.prototype.updateIncidentInvolvedIndividuals = function () {
            var $section = this.$element.find('.incident-involved-individuals-section');

            $section.find('.individual').remove();

            var me = this;
            _.each(this.state.data.incidentInvolvedIndividuals, function (o, i) {
                $section.json2html({}, renderIndividual($.extend({}, {index: i}, o, {
                    onChange: function (index, field, value) {
                        me.changeIncidentInvolvedIndividual(index, field, value);
                    },
                    onRemove: function (index) {
                        me.removeIncidentInvolvedIndividual(index);
                    }
                })));
            });
        };

        IncidentReportForm.prototype.updateCurrentDiagnosesPanel = function () {
            var diagnoses = this.state.data.currentDiagnoses;

            if (diagnoses) {
                var $panel = this.$element.find('.incident-report-form__diagnoses');

                $panel.empty();

                _.each(diagnoses, function (o) {
                    $panel.json2html({}, renderDiagnosis(o));
                });
            }
        };

        IncidentReportForm.prototype.updateActiveMedicationsPanel = function () {
            var medications = this.state.data.activeMedications;

            if (medications) {
                var $panel = this.$element.find('.incident-report-form__medications');

                $panel.empty();

                _.each(medications, function (o) {
                    $panel.json2html({}, renderMedication(o));
                });
            }
        };

        IncidentReportForm.prototype.updatePlainFields = function () {
            var data = this.state.data;

            var me = this;
            _.each(PLAIN_FIELDS, function (field) {
                me.$element.find(U.interpolate('input[name=$0],textarea[name=$0]', field)).each(function () {
                    var $input = $(this);
                    var value = data[field];

                    var type = $input.attr('data-type') || $input.attr('type');

                    if (['checkbox', 'radio'].includes(type)) {
                        if (U.convertStringToBoolean($input.val()) === value) {
                            $input.prop('checked', true).trigger('refresh');
                        }
                    }

                    else {
                        if (['date', 'datetime'].includes(type)) {
                            $input.data('DateTimePicker').date(new Date(value));
                        }

                        else $input.val(value);
                    }
                });
            });

            this.setState({arePlainFieldsUpdated: true});
        };

        IncidentReportForm.prototype.updateAgencyStateField = function () {
            var states = this.props.directory.state.list.dataSource.data;

            if (states) {
                var agencyStateId = this.state.data.agencyStateId;

                var $select = $(this.getElement('agencyStateId')).empty();

                _.each(_.union([{ name: 'Select' }], states), function (o) {

                    var option = {'<>': 'option', 'value': o.id, 'text': o.name || ''};

                    if (o.name === 'Select') $.extend(option, {'value': '', 'hidden': true}, !agencyStateId && {'selected': true});
                    if (o.id === agencyStateId) $.extend(option, {'selected': true});

                    $select.json2html({}, option);
                });
            }
        };

        IncidentReportForm.prototype.triggerWereIndividualsInvolvedInIncidentField = function (isYes) {
            var selector = 'input[name=wereIndividualsInvolvedInIncident]';

            this
                .$element
                .find(selector + '[value=true]')
                .prop('checked', isYes)
                .trigger('refresh');

            this
                .$element
                .find(selector + '[value=false]')
                .prop('checked', !isYes)
                .trigger('refresh');
        };

        IncidentReportForm.prototype.updateIncidentLevelSection = function (level) {
            level = level || 1;

            var me = this;
            var selector = U.interpolate('.level-$0-incident-types-section', level);

            var $section = this.$element.find(selector);
            $section.empty();

            $section.json2html({}, renderIncidentLevelTypesSectionHeader(level, this.onIncidentLevelReportingSettings));

            var types = this.getIncidentTypes({ level: level });

            var field = U.interpolate('level$0IncidentTypes', level);
            var checked = this.state.data[field];

            types = markAsChecked(types, checked, 'id', 'incidentTypes');

            _.each(checked, function (o) {
                if (o.text) {
                    var type = findIncidentTypeById(types, o.id);
                    if (type) type.text = o.text;
                }
            });

            _.each(types, function (o) {
                $section.json2html({}, renderIncidentType($.extend({}, o, {
                    onChange: function (level, id, text, checked) {
                        me.changeIncidentTypesField(level, id, text, checked);
                        if (me.shouldValidate()) me.isValid();
                    }
                })))
            });

            $section
                .find('input[type="checkbox"], input[type="radio"]')
                .styler();
        };

        IncidentReportForm.prototype.setDraft = function (isDraft) {
            this.setState({ isDraft: isDraft !== false });
        };

        IncidentReportForm.prototype.init = function () {
            var key = this.isNew() ? 'initialized' : 'details'
            var data = this.props.event.incident.report[key].data

            if (!U.isEmpty(data)) {
                var me = this;

                var changes = {
                    reportCompletedDate: new Date().getTime(),
                    wereIndividualsInvolvedInIncident: U.isNotEmpty(data.incidentInvolvedIndividuals)
                };

                _.each(data, function (v, k) {
                    if (!U.isEmpty(v) && !k.includes('IncidentTypes')) {
                        changes[k] = v
                    }
                });

                this.changeFields(changes);

                for (var i = 1; i < 4; i++) {
                    var field = U.interpolate('level$0IncidentTypes', i);

                    if (!U.isEmpty(data[field])) {
                        _.each(data[field], function (o) {
                            me.changeIncidentTypesField(i, o.id, o.text, true);
                        });
                    }
                }
            }

            this.setState({ isInitialized: true });
        };

        IncidentReportForm.prototype.getInvalidFields = function () {
            return this.$element
                .find('input, select, textarea')
                .filter('.error')
                .parent('.form-group');
        };

        IncidentReportForm.prototype.submit = function () {
            var eventId = this.props.eventId;

            var me = this;

            this
                .props
                .actions
                .event
                .incident
                .report
                .form
                .save(eventId, this.getPreparedData())
                .then(function (data) {
                    me.setState({ isChanged: false }, function () {
                        me.props.onSubmitSuccess(data);
                    });
                })
                .fail(function (e) {
                    me.setState({ isChanged: false }, function () {
                        me.props.onSubmitFailure(e);
                    });
                });
        };

        IncidentReportForm.prototype.saveDraft = function () {
            var eventId = this.props.eventId;

            var me = this;

            this
                .props
                .actions
                .event
                .incident
                .report
                .form
                .saveDraft(eventId, this.getPreparedData())
                .then(function (data) {
                    me.setState({ isChanged: false }, function () {
                        me.props.onSaveDraftSuccess(data);
                    });
                })
                .fail(function (e) {
                    me.setState({ isChanged: false }, function () {
                        me.props.onSaveDraftFailure(e);
                    });
                });
        };

        IncidentReportForm.prototype.getPreparedData = function () {
            var data = $.extend({}, this.state.data);

            if (!data.wereIndividualsInvolvedInIncident) {
                data.incidentInvolvedIndividuals = [];
            }

            return data;
        };

        IncidentReportForm.prototype.isChanged = function () {
            return this.state.isChanged;
        };

        IncidentReportForm.prototype.registerChange = function () {
            if (this.state.isInitialized) {
                this.setState({ isChanged: true })
            }
        };

        IncidentReportForm.prototype.getIncidentTypes = function (params) {
            return _.where(
                this.props.directory.incident.type.list.dataSource.data,
                {level: params.level || 1}
            );
        };

        IncidentReportForm.prototype.render = function () {
            var me = this;

            var isReadonly = this.props.isReadonly;

            var data = this.state.data;

            return {
                '<>': 'form',
                'class': 'incident-report-form',
                'html': [
                    {'<>': 'div', 'class': 'form-section client-info-section', 'html': [
                        {'<>': 'h3', 'text': 'Client info', 'class': 'section__header client-info-section__header'},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-8', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'text': "Class Member's Name"},
                                        {
                                            '<>': 'input',
                                            'name': 'clientName',
                                            'type': 'text',
                                            'disabled': true,
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.clientName || ''
                                        }
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group class-member-types-section', 'html': []}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'text': "RIN"},
                                    {
                                        '<>': 'input',
                                        'name': 'clientRIN',
                                        'type': 'text',
                                        'class': 'form-control',
                                        'placeholder': '',
                                        'value': data.clientRIN || ''
                                    }
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group date', 'html': [
                                    {'<>': 'label', 'text': 'Date of Birth'},
                                    {
                                        '<>': 'input',
                                        'name': 'clientBirthDate',
                                        'type': 'text',
                                        'data-type': 'date',
                                        'class': 'form-control',
                                        'placeholder': 'mm/dd/yyyy'
                                    },
                                    {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'text': 'Gender*'},
                                    {
                                        '<>': 'select',
                                        'name': 'clientGenderId',
                                        'data-name': 'clientGenderId',
                                        'class': 'form-control',
                                        'placeholder': 'Select',
                                        'html': []
                                    }
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'text': 'Race*'},
                                    {
                                        '<>': 'select',
                                        'name': 'clientRaceId',
                                        'data-name': 'clientRaceId',
                                        'class': 'form-control',
                                        'placeholder': 'Select',
                                        'html': []
                                    }
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group date', 'html': [
                                    {'<>': 'label', 'text': 'Date of Transition to Community'},
                                    {
                                        '<>': 'input',
                                        'name': 'clientTransitionToCommunityDate',
                                        'type': 'text',
                                        'data-type': 'date',
                                        'class': 'form-control',
                                        'placeholder': 'mm/dd/yyyy'
                                    },
                                    {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-8', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'text': "Class Member's Current Address"},
                                    {
                                        '<>': 'input',
                                        'name': 'clientClassMemberCurrentAddress',
                                        'type': 'text',
                                        'class': 'form-control',
                                        'placeholder': '',
                                        'value': data.clientClassMemberCurrentAddress || ''
                                    }
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-12', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'class': 'incident-report-form__label', 'text': 'Current Diagnoses'},
                                    {'<>': 'div', 'class': 'incident-report-form__diagnoses', 'html': []}
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-12', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'class': 'incident-report-form__label', 'text': 'Current/Active Medications'},
                                    {'<>': 'div', 'class': 'incident-report-form__medications', 'html': []}
                                ]}
                            ]}
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'form-section agency-staff-info-section', 'html': [
                        {'<>': 'h3', 'text': 'Agency & Staff Info', 'class': 'section__header agency-staff-info-section__header'},
                        {'<>': 'div', 'class': 'form-section agency-section', 'html': [
                            {'<>': 'h5', 'text': 'Agency', 'class': 'section__header agency-section__header'},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-8', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Agency Name"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyName',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyName || ''
                                        }
                                    ]}
                                ]},
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-8', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Agency Address"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyAddress',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyAddress || ''
                                        }
                                    ]}
                                ]}
                            ]},
                            /*{'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-12', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Agency Name"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyName',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyName || ''
                                        }
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-8', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Agency Address"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyAddress',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyAddress || ''
                                        }
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': 'State'},
                                        {
                                            '<>': 'select',
                                            'name': 'agencyStateId',
                                            'data-name': 'agencyStateId',
                                            'class': 'form-control',
                                            'placeholder': 'Select',
                                            'html': []
                                        }
                                    ]}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "City"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyCity',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyCity || ''
                                        }
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-8', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Street"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyStreet',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyStreet || ''
                                        }
                                    ]}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Zip Code"},
                                        {
                                            '<>': 'input',
                                            'name': 'agencyZipCode',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.agencyZipCode || ''
                                        }
                                    ]}
                                ]}
                            ]},*/
                        ]},
                        {'<>': 'div', 'class': 'form-section staff-section', 'html': [
                            {'<>': 'h5', 'text': 'Staff', 'class': 'section__header staff-section__header'},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-8', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Quality Administrator"},
                                        {
                                            '<>': 'input',
                                            'name': 'qualityAdministrator',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.qualityAdministrator || ''
                                        }
                                    ]}
                                ]},
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                    {'<>': 'div', 'class': 'col-md-8', 'html': [
                                        {'<>': 'div', 'class': 'form-group', 'html': [
                                            {'<>': 'label', 'text': "Care Manager/Staff with Primary Service Responsibility and Title"},
                                            {
                                                '<>': 'input',
                                                'name': 'careManagerOrStaffWithPrimServRespAndTitle',
                                                'type': 'text',
                                                'class': 'form-control',
                                                'placeholder': '',
                                                'value': data.careManagerOrStaffWithPrimServRespAndTitle || ''
                                            }
                                        ]}
                                    ]}
                                    /*{'<>': 'div', 'class': 'col-md-8', 'html': [
                                        {'<>': 'div', 'class': 'form-group', 'html': [
                                            {'<>': 'label', 'text': "Care Manager/Staff with Primary Service Responsibility"},
                                            {
                                                '<>': 'input',
                                                'name': 'careManagerOrStaffWithPSR',
                                                'type': 'text',
                                                'class': 'form-control',
                                                'placeholder': '',
                                                'value': data.careManagerOrStaffWithPSR || ''
                                            }
                                        ]}
                                    ]},
                                    {'<>': 'div', 'class': 'col-md-4', 'html': [
                                        {'<>': 'div', 'class': 'form-group', 'html': [
                                            {'<>': 'label', 'text': "Title"},
                                            {
                                                '<>': 'input',
                                                'name': 'careManagerOrStaffTitle',
                                                'type': 'text',
                                                'class': 'form-control',
                                                'placeholder': '',
                                                'value': data.careManagerOrStaffTitle || ''
                                            }
                                        ]}
                                    ]}*/
                                ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Phone"},
                                        {
                                            '<>': 'input',
                                            'name': 'careManagerOrStaffPhone',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.careManagerOrStaffPhone || ''
                                        }
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Email"},
                                        {
                                            '<>': 'input',
                                            'name': 'careManagerOrStaffEmail',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.careManagerOrStaffEmail || ''
                                        }
                                    ]}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                                        {'<>': 'div', 'class': 'form-group', 'html': [
                                            {'<>': 'label', 'text': "MCO Care Coordinator & Agency (Colbert only, if applicable)"},
                                            {
                                                '<>': 'input',
                                                'name': 'mcoCareCoordinatorAndAgency',
                                                'type': 'text',
                                                'class': 'form-control',
                                                'placeholder': '',
                                                'value': data.mcoCareCoordinatorAndAgency || ''
                                            }
                                        ]}
                                    ]}
                                ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Phone"},
                                        {
                                            '<>': 'input',
                                            'name': 'mcoCareCoordinatorPhone',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.mcoCareCoordinatorPhone || ''
                                        }
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Email"},
                                        {
                                            '<>': 'input',
                                            'name': 'mcoCareCoordinatorEmail',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.mcoCareCoordinatorEmail || ''
                                        }
                                    ]}
                                ]}
                            ]}
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'form-section incident-info-section', 'html': [
                        {'<>': 'h3', 'text': 'Incident Information', 'class': 'section__header incident-info-section__header'},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group date', 'html': [
                                    {'<>': 'label', 'text': 'Date of Incident*'},
                                    {
                                        '<>': 'input',
                                        'name': 'incidentDateTime',
                                        'type': 'text',
                                        'data-type': 'datetime',
                                        'class': 'form-control',
                                        'placeholder': 'mm/dd/yyyy'
                                    },
                                    {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'col-md-4', 'html': [
                                {'<>': 'div', 'class': 'form-group date', 'html': [
                                    {'<>': 'label', 'text': 'Date incident discovered by agency staff*'},
                                    {
                                        '<>': 'input',
                                        'name': 'incidentDiscoveredDate',
                                        'type': 'text',
                                        'data-type': 'date',
                                        'class': 'form-control',
                                        'placeholder': 'mm/dd/yyyy'
                                    },
                                    {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-12', 'html': [
                                {'<>': 'div', 'class': 'form-group date', 'html': [
                                    {'<>': 'label', 'text': 'Did the incident occur when a provider was present or was scheduled to be present?'},
                                    {'<>': 'div', 'class': 'radio', 'html': [
                                        {'<>': 'label', 'html': [
                                            renderRadio({
                                                value: true,
                                                name: 'wasProviderPresentOrScheduled',
                                                tooltipPosition: 'right',
                                                checked: data.wasProviderPresentOrScheduled === true,
                                                onChange: function (name, value) {
                                                    me.changeField(name, value);
                                                    if (me.shouldValidate()) me.isValid();
                                                }
                                            }),
                                            {'<>': 'span', 'text': 'Yes'}
                                        ]}
                                    ]},
                                    {'<>': 'div', 'class': 'radio', 'html': [
                                        {'<>': 'label', 'html': [
                                            renderRadio({
                                                value: false,
                                                name: 'wasProviderPresentOrScheduled',
                                                checked: data.wasProviderPresentOrScheduled === false,
                                                onChange: function (name, value) {
                                                    me.changeField(name, value);
                                                    if (me.shouldValidate()) me.isValid();
                                                }
                                            }),
                                            {'<>': 'span', 'text': 'No'}
                                        ]}
                                    ]}
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-12', 'html': [
                                {'<>': 'div', 'class': 'form-group', 'html': [
                                    {'<>': 'label', 'text': 'Where did the incident take place?'},
                                    {'<>': 'div', 'class': 'incident-place-list', 'html': []}
                                ]}
                            ]}
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'form-section incident-involved-individuals-section', 'html': [
                        {'<>': 'h3', 'text': 'Individuals involved in the incident', 'class': 'section__header incident-involved-individuals__header'},
                        {'<>': 'div', 'class': 'row', 'html': [
                            {'<>': 'div', 'class': 'col-md-6', 'html': [
                                {'<>': 'div', 'class': 'form-group date', 'html': [
                                    {'<>': 'label', 'text': 'Were other individuals involved in the incident?'},
                                    {'<>': 'div', 'class': 'radio', 'html': [
                                        {'<>': 'label', 'html': [
                                            renderRadio({
                                                value: true,
                                                name: 'wereIndividualsInvolvedInIncident',
                                                checked: data.wereIndividualsInvolvedInIncident === true,
                                                onChange: function (name, value) {
                                                    me.changeField(name, value);
                                                    if (me.shouldValidate()) me.isValid();
                                                }
                                            }),
                                            {'<>': 'span', 'text': 'Yes'}
                                        ]}
                                    ]},
                                    {'<>': 'div', 'class': 'radio', 'html': [
                                        {'<>': 'label', 'html': [
                                            renderRadio({
                                                value: false,
                                                name: 'wereIndividualsInvolvedInIncident',
                                                checked: data.wereIndividualsInvolvedInIncident === false,
                                                onChange: function (name, value) {
                                                    me.changeField(name, value);
                                                    if (me.shouldValidate()) me.isValid();
                                                }
                                            }),
                                            {'<>': 'span', 'text': 'No'}
                                        ]}
                                    ]}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'col-md-6', 'html': [
                                {
                                    '<>': 'a',
                                    'class': 'add-button add-incident-involved-individual-btn pull-right',
                                    'style': !data.wereIndividualsInvolvedInIncident ? 'display: none;' : undefined,
                                    'onclick': function () {
                                        me.onAddIncidentInvolvedIndividual();
                                    },
                                    'html': [
                                        {'<>': 'img', 'src': ADD_IMAGE_URL},
                                        {'<>': 'span', 'text': 'Add New'}
                                    ]
                                }
                            ]}
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'form-section incident-levels-events-section', 'html': [
                        {'<>': 'h3', 'text': 'Incident levels and events', 'class': 'section__header incident-levels-events-section__header'},
                        {'<>': 'div', 'class': 'form-section level-1-incident-types-section', 'data-incident-level': '1', 'html': [
                            renderIncidentLevelTypesSectionHeader(1, this.onIncidentLevelReportingSettings)
                        ]},
                        {'<>': 'div', 'class': 'form-section level-2-incident-types-section', 'html': [
                            renderIncidentLevelTypesSectionHeader(2, this.onIncidentLevelReportingSettings)
                        ]},
                        {'<>': 'div', 'class': 'form-section level-3-incident-types-section', 'html': [
                            renderIncidentLevelTypesSectionHeader(3, this.onIncidentLevelReportingSettings)
                        ]},
                        {'<>': 'div', 'class': 'form-section other-information-section', 'html': [
                            {'<>': 'h3', 'text': 'Other Information', 'class': 'section__header other-information-section__header'},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-12', 'html': [
                                    {'<>': 'div', 'class': 'form-group date', 'html': [
                                        {'<>': 'label', 'text': 'Was this incident caused by, or related to, the member\'s substance use or substance abuse disorder diagnosis?'},
                                        {'<>': 'div', 'class': 'radio', 'html': [
                                            {'<>': 'label', 'html': [
                                                renderRadio({
                                                    value: true,
                                                    name: 'wasIncidentCausedBySubstance',
                                                    checked: data.wasIncidentCausedBySubstance === true,
                                                    onChange: function (name, value) {
                                                        me.changeField(name, value);
                                                        if (me.shouldValidate()) me.isValid();
                                                    }
                                                }),
                                                {'<>': 'span', 'text': 'Yes'}
                                            ]}
                                        ]},
                                        {'<>': 'div', 'class': 'radio', 'html': [
                                            {'<>': 'label', 'html': [
                                                renderRadio({
                                                    value: false,
                                                    name: 'wasIncidentCausedBySubstance',
                                                    checked: data.wasIncidentCausedBySubstance === false,
                                                    onChange: function (name, value) {
                                                        me.changeField(name, value);
                                                        if (me.shouldValidate()) me.isValid();
                                                    }
                                                }),
                                                {'<>': 'span', 'text': 'No'}
                                            ]}
                                        ]}
                                    ]}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-12', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': 'Narrative'},
                                        {
                                            '<>': 'textarea',
                                            'rows': 1,
                                            'name': 'incidentNarrative',
                                            'data-name': 'incidentNarrative',
                                            'data-autoresizable': true,
                                            'style': 'min-height: 82px',
                                            'class': 'form-control autoresizable',
                                            'text': data.incidentNarrative || ''
                                        }
                                    ]}
                                ]}
                            ]},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-12', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': "Agency’s Response to Incident"},
                                        {
                                            '<>': 'textarea',
                                            'rows': 1,
                                            'name': 'agencyResponseToIncident',
                                            'data-name': 'agencyResponseToIncident',
                                            'data-autoresizable': true,
                                            'style': 'min-height: 82px',
                                            'class': 'form-control autoresizable',
                                            'text': data.agencyResponseToIncident || ''
                                        }
                                    ]}
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'form-section reporting-section', 'html': [
                            {'<>': 'h3', 'text': 'Reporting', 'class': 'section__header reporting-section__header'},
                            {'<>': 'div', 'class': 'row', 'html': [
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group', 'html': [
                                        {'<>': 'label', 'text': 'Completed by*'},
                                        {
                                            '<>': 'input',
                                            'name': 'reportAuthor',
                                            'type': 'text',
                                            'class': 'form-control',
                                            'placeholder': '',
                                            'value': data.reportAuthor || ''
                                        }
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group date', 'html': [
                                        {'<>': 'label', 'text': 'Report Completed Date*'},
                                        {
                                            '<>': 'input',
                                            'name': 'reportCompletedDate',
                                            'type': 'text',
                                            'data-type': 'date',
                                            'class': 'form-control',
                                            'placeholder': 'mm/dd/yyyy'
                                        },
                                        {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                                    ]}
                                ]},
                                {'<>': 'div', 'class': 'col-md-4', 'html': [
                                    {'<>': 'div', 'class': 'form-group date', 'html': [
                                        {'<>': 'label', 'text': 'Date Completed*'},
                                        {
                                            '<>': 'input',
                                            'name': 'reportDate',
                                            'type': 'text',
                                            'data-type': 'date',
                                            'class': 'form-control',
                                            'placeholder': 'mm/dd/yyyy'
                                        },
                                        {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                                    ]}
                                ]}
                            ]}
                        ]}
                    ]}
                ]
            }
        };

        return utils.connect(mapStateToProps, mapDispatchToProps)(IncidentReportForm);
    }
);
