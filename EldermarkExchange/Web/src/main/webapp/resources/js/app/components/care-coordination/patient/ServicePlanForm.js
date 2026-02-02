/**
 * Created by stsiushkevich on 12.09.18.
 */

var ServicePlanForm = (function ($) {

    var context = ExchangeApp.info.context;

    var ADD_IMAGE_URL = context + '/resources/images/add-button-rounded.svg';

    var fields = [
        'dateCreated',
        'completed',
        'createdBy'
    ];

    var DOMAIN_NAMES_TITLES = {
        employment: 'Employment',
        healthStatus: 'Health Status',
        transportation: 'Transportation',
        support: 'Caregiver Resource / Support',
        housingOnly: 'Housing',
        housing: 'Housing / Home Security & Safety',
        nutritionSecurity: 'Nutrition Security',
        behavioral: 'Behavioral / Spiritual Health',
        socialWellness: 'Social wellness',
        mentalWellness: 'Mental wellness',
        physicalWellness: 'Physical wellness',
        task: 'Relevant Activation or Education Task',
        other: 'Other / Non-Specific',
        legal: 'Legal',
        finances: 'Finances',
        medicalOtherSupply: 'Medical / Other Supply',
        medicationMgmtAssistance: 'Medication Management and Assistance',
        homeHealth: 'Home Health'
    };

    function ServicePlanForm() {
        Form.apply(this, arguments);

        this.needs = [];

        this.validator = null;
        this.scoringSection = null;

        var data = this.props.data;
        var currentUser = this.props.currentUser || {};

        this.state = {
            data: $.extend({}, data, {
                createdBy: currentUser.fullName
            }),
            anchors: [
                {name: 'summary', title: 'Summary'}
            ]
        };
    }

    ServicePlanForm.prototype = Object.create(Form.prototype);
    ServicePlanForm.prototype.constructor = ServicePlanForm;

    ServicePlanForm.prototype.getDefaultProps = function () {
        return {
            data: {},
            isReadonly: false,
            currentUser: null,
            onShowDatePicker: function () {},
            onHideDatePicker: function () {},
            onNeedSectionAdded: function () {},
            onNeedSectionRemoved: function () {},
            onGoalSectionAdded: function () {}
        };
    };

    ServicePlanForm.prototype.componentDidMount = function () {
        Form.prototype.componentDidMount.apply(this);

        this.$element
            .find('input[type="checkbox"]')
            .styler();

        var isNew = this.props.isNew;
        var isReadonly = this.props.isReadonly;

        this.$element.find('input').each(function () {
            var $elem = $(this);
            var name = $elem.attr('name');
            var type = $elem.attr('type');

            if (!isNew && name === 'dateCreated') {
                $elem.attr('readonly', 'true');
            }

            if (isReadonly && name === 'completed') {
                $elem.attr('disabled', 'true')
                    .trigger('refresh');
            }

            if (isReadonly && name === 'dateCompleted') {
                $elem.attr('readonly', 'true');
            }
        });

        if (isReadonly) {
            if (this.hasScoringSection()) {
                this.removeScoringSection()
            }
            this.createScoringSection(true);
        }

        else {
            var me = this;

            this.$element
                .find('input[data-type="date"]')
                .datetimepicker({
                    format: 'MM/DD/YYYY HH:mm A [' + new Date().getTimezoneAbbr() + ']',
                    keepOpen: true
                })
                .on("dp.change", function (e) {
                    me.onChange(e);
                })
                .on('dp.show', function () {
                    me.props.onShowDatePicker();
                })
                .on('dp.hide', function () {
                    me.props.onHideDatePicker();
                });

            this.$element
                .find('.info-tip')
                .popover();

            this.anchorLinkPanel = new AnchorLinkPanel({
                container: this.$element.find('.anchor-link-section'),
                items: this.state.anchors
            });

            this.anchorLinkPanel.mount();

            this.addOnChangeHandler(function (e) {
                me.onChange(e)
            });

            var needs = (this.props.data || {}).needs;

            if (needs) {
                $.each(needs, function (i, n) {
                    var need = new ServicePlanForm.NeedSection({
                        container: me.$element.find('.need-section-list'),
                        index: i,
                        data: n,
                        onRemove: function (index) {
                            me.onRemoveNeedSection(index);
                        },
                        onChangeType: function (type, index) {
                            me.onChangeNeedType(type, index)
                        },
                        onGoalSectionAdded: function () {
                            me.props.onGoalSectionAdded(need.getIndex(), need.goals.length - 1);
                        }
                    });

                    need.mount();

                    me.needs.push(need);

                    me.addAnchorLink('need-'+ i, n.type || 'Health Status');
                });
            }
        }

        if (isNew) {
            this.$element
                .find('input[name="dateCreated"]')
                .data("DateTimePicker").date(new Date());
        }
    };

    ServicePlanForm.prototype.componentDidUpdate = function (prevProps, prevState) {
        var anchors = this.state.anchors;

        if (anchors !== prevState.anchors) {
            this.anchorLinkPanel.update({ items: anchors })
        }

        if (this.state.data !== prevState.data) {
            var data = this.state.data;

            this.$element
                .find('input[name="createdBy"]')
                .val(data.createdBy);
        }

        var isNew = this.props.isNew;
        var currentUser = this.props.currentUser;

        if (isNew && currentUser && currentUser !== prevProps.currentUser) {
            this.setState({
                data: $.extend({}, this.state.data, {
                    createdBy: currentUser.fullName
                })
            })
        }
    };

    ServicePlanForm.prototype.onChange = function (e) {
        var $elem = $(e.target);

        var name = $elem.attr('name');

        if (~fields.indexOf(name)) {
            var value = '';
            var type = $elem.attr('data-type') || $elem.attr('type');

            switch (type) {
                case 'date': value = e.date ? e.date.toDate().getTime() : ''; break;
                case 'checkbox': value = $elem.prop('checked'); break;
                default: value = $elem.val();
            }

            var data = $.extend({}, this.state.data);
            data[name] = value;

            this.setState({ data: data });
        }
    };

    ServicePlanForm.prototype.onChangeNeedType = function (type, index) {
        this.updateAnchorLink('need-'+ index, type);
    };

    ServicePlanForm.prototype.onAddNeedSection = function () {
        this.addNeedSection();
        this.props.onNeedSectionAdded(this.needs.length - 1);
    };

    ServicePlanForm.prototype.onRemoveNeedSection = function (index) {
        this.needs = $.map(this.needs, function (n, i) {
            if (i !== index) return n;
        });

        $.each(this.needs, function (i, n) {
            n.update({ index: i });
        });

        this.removeAnchorLink('need-'+ index);
        this.props.onNeedSectionRemoved()
    };

    ServicePlanForm.prototype.addOnChangeHandler = function (handler) {
        this.$element.on('change', handler)
    };

    ServicePlanForm.prototype.addValidation = function () {
        var rules = { dateCreated: { required: true } };
        var messages = { dateCreated: { required: getErrorMessage("field.empty") } };

        function getMaxLengthErrorMsg (v) {
            return 'The field must have a maximum length of '+ v +' symbols.'
        }

        $.each(this.needs, function (i, need) {
            rules['need.' + i + '.type'] = { required: true };
            rules['need.' + i + '.priority'] = { required: true };
            rules['need.' + i + '.needOpportunity'] = { required: true, maxlength: 256 };
            rules['need.' + i + '.activationOrEducationTask'] = { required: true, maxlength: 256 };
            rules['need.' + i + '.proficiencyGraduationCriteria'] = { maxlength: 5000 };
            rules['need.' + i + '.targetCompletionDate'] = { required: true };

            messages['need.' + i + '.type'] = { required: getErrorMessage("field.empty") };
            messages['need.' + i + '.priority'] = { required: getErrorMessage("field.empty") };

            messages['need.' + i + '.needOpportunity'] = {
                required: getErrorMessage("field.empty"),
                maxlength: getMaxLengthErrorMsg(256)
            };

            messages['need.' + i + '.activationOrEducationTask'] = {
                required: getErrorMessage("field.empty"),
                maxlength: getMaxLengthErrorMsg(256)
            };

            messages['need.' + i + '.proficiencyGraduationCriteria'] = { maxlength: getMaxLengthErrorMsg(5000) };
            messages['need.' + i + '.targetCompletionDate'] = { required: getErrorMessage("field.empty") };

            $.each(need.goals, function (j) {
                rules['need.' + i + '.goal.' + j + '.goal'] = { required: true, maxlength: 256 };
                rules['need.' + i + '.goal.' + j + '.barriers'] = { maxlength: 5000 };
                rules['need.' + i + '.goal.' + j + '.interventionAction'] = { maxlength: 5000 };
                rules['need.' + i + '.goal.' + j + '.resourceName'] = { maxlength: 256 };
                rules['need.' + i + '.goal.' + j + '.targetCompletionDate'] = { required: true };

                messages['need.' + i + '.goal.' + j + '.goal'] = {
                    required: getErrorMessage("field.empty"),
                    maxlength: getMaxLengthErrorMsg(256)
                };

                messages['need.' + i + '.goal.' + j + '.barriers'] = { maxlength: getMaxLengthErrorMsg(5000) };
                messages['need.' + i + '.goal.' + j + '.interventionAction'] = { maxlength: getMaxLengthErrorMsg(5000) };
                messages['need.' + i + '.goal.' + j + '.resourceName'] = { maxlength: getMaxLengthErrorMsg(256) };
                messages['need.' + i + '.goal.' + j + '.targetCompletionDate'] = { required: getErrorMessage("field.empty") };
            })
        });

        this.validator = this.$element.validate(
            new ExchangeApp.utils.wgt.Validation({
                position:'top', rules: rules, messages: messages
            })
        )
    };

    ServicePlanForm.prototype.updateValidation = function () {
        if (this.validator) this.validator.destroy();
        this.addValidation();
    };

    ServicePlanForm.prototype.isValid = function () {
        return this.$element.valid();
    };

    ServicePlanForm.prototype.scrollToStart = function () {
        $(this.props.container).scrollTo(
            this.$element.find('.start-anchor'),
            500
        );
    };

    ServicePlanForm.prototype.getNeedSectionCount = function () {
        return this.needs.length;
    };

    ServicePlanForm.prototype.getNeedSection = function (index) {
        return this.needs[index];
    };

    ServicePlanForm.prototype.getInvalidFields = function () {
        return this.$element
            .find('input, select, textarea')
            .filter('.error')
            .parent('.form-group');
    };

    ServicePlanForm.prototype.addAnchorLink = function (name, title) {
        var anchors = [].concat(this.state.anchors);

        anchors.push({name: name, title: title});

        var filtered = $.map(anchors, function (o) {
            if (o.title.includes(title)) return o;
        });

        if (filtered.length > 1) {
            $.each(filtered, function (i, o) {
                o.title = title + ' #' + (i + 1);
            });
        }

        this.setState({ anchors: anchors })
    };

    ServicePlanForm.prototype.updateAnchorLink = function (name, title) {
        var anchors = [].concat(this.state.anchors);

        var oldTitle = '';

        // find target link and rename it
        $.each(anchors, function (i, anchor) {
            if (anchor.name === name) {
                oldTitle = anchor.title;
                anchor.title = title;
            }
        });

        oldTitle = oldTitle.replace(/\s#\d*/, '');

        // find all links with same titles
        var filtered = $.map(anchors, function (anchor) {
            if (anchor.title.includes(oldTitle)) return anchor;
        });

        var count = filtered.length;

        // update link titles
        if (count === 1) {
            filtered[0].title = oldTitle;
        }

        if (count > 1) {
            $.each(filtered, function (i, anchor) {
                anchor.title = oldTitle + ' #' + (i + 1);
            });
        }

        // find all links with same titles
        filtered = $.map(anchors, function (anchor) {
            if (anchor.title.includes(title)) return anchor;
        });

        if (filtered.length > 1) {
            $.each(filtered, function (i, anchor) {
                anchor.title = title + ' #' + (i + 1);
            });
        }

        this.setState({ anchors: anchors })
    };

    ServicePlanForm.prototype.removeAnchorLink = function (name) {
        var removedAnchor = null;

        var anchors = $.map(this.state.anchors, function (a) {
            if (a.name === name) removedAnchor = a;
            else return a;
        });

        var needAnchors = $.map(anchors, function (a) {
            if (a.name.includes('need')) return a;
        });

        $.each(needAnchors, function (i, a) {
            a.name = 'need-' + i;
        });

        var title = removedAnchor.title.replace(/\s#\d*/, '');

        var filtered = $.map(needAnchors, function (o) {
            if (o.title.includes(title)) return o;
        });

        var count = filtered.length;

        if (count === 1) {
            filtered[0].title = title;
        }

        if (count > 1) {
            $.each(filtered, function (i, o) {
                o.title = title + ' #' + (i + 1);
            });
        }

        this.setState({ anchors: anchors })
    };

    ServicePlanForm.prototype.addNeedSection = function () {
        var me = this;

        var index = this.needs.length;
        var isReadonly = this.props.isReadonly;

        var need = new ServicePlanForm.NeedSection({
            container: me.$element.find('.need-section-list'),
            index: index,
            isReadonly: isReadonly,
            onRemove: function (index) {
                me.onRemoveNeedSection(index);
            },
            onChangeType: function (type, index) {
                me.onChangeNeedType(type, index)
            },
            onGoalSectionAdded: function () {
                me.props.onGoalSectionAdded(need.getIndex(), need.goals.length - 1);
            }
        });

        need.mount();
        me.needs.push(need);

        this.addAnchorLink('need-'+ index, 'Health Status');
    };

    ServicePlanForm.prototype.getData = function () {
        if (this.props.isReadonly) return this.state.data;

        var needs = $.map(this.needs, function (n) {
            var data = n.getData();
            if (!isEmpty(data)) return data;
        });

        return $.extend({},
            this.state.data,
            {needs: needs || []}
        );
    };

    ServicePlanForm.prototype.getScoringSectionData = function () {
        var PRIORITY_RATES = { HIGH: 1, MEDIUM: 2, LOW: 3 };

        var groups = _.groupBy(this.getData().needs, 'type');

        var me = this;
        var domains = _.map(groups, function (needs, title) {
            var name = _.findKey(DOMAIN_NAMES_TITLES, function (v) {
                return v === title;
            });

            if (needs) {
                needs = _.sortBy(needs, function (need) {
                    return PRIORITY_RATES[need.priority.toUpperCase()];
                });

                _.each(needs, function (need) {
                    var goals = need.goals;

                    if (goals) {
                        need.goals = _.sortBy(goals, function (goal) {
                            return goal.targetCompletionDate;
                        })
                    }
                });
            }

            return {
                title: title,
                name: name,
                needs: needs,
                score: me.state.data[name + 'Score'] || 0
            };
        });

        return _.sortBy(domains, 'title');
    };

    ServicePlanForm.prototype.hasScoringSection = function () {
        return this.scoringSection !== null;
    };

    ServicePlanForm.prototype.createScoringSection = function (isShowed) {
        var me = this;
        this.scoringSection = new ServicePlanForm.ScoringSection({
            container: this.$element,
            isShowed: isShowed,
            isDisabled: this.props.isReadonly,
            data: this.getScoringSectionData(),
            onChangeDomainScore: function (domain, score) {
                var data = me.state.data;
                var key = domain.name + 'Score';

                if (data[key] !== score) {
                    var updates = {};
                    updates[key] = score;

                    me.setState({data: $.extend({}, data, updates)});
                }
            }
        });

        this.scoringSection.mount();
    };

    ServicePlanForm.prototype.showScoringSection = function (areOthersHidden) {
        areOthersHidden = areOthersHidden || true;

        if (areOthersHidden) {
            this.$element.find('.anchor-link-section').hide();
            this.$element.find('.summary-section').hide();
            this.$element.find('.form-section-list').hide();
        }

        if (this.scoringSection) this.scoringSection.show();
    };

    ServicePlanForm.prototype.hideScoringSection = function (areOthersShowed) {
        areOthersShowed = areOthersShowed || true;

        if (areOthersShowed) {
            this.$element.find('.anchor-link-section').show();
            this.$element.find('.summary-section').show();
            this.$element.find('.form-section-list').show();
        }

        this.scoringSection.hide();
    };

    ServicePlanForm.prototype.removeScoringSection = function () {
        this.scoringSection.unmount();
    };

    ServicePlanForm.prototype.isChanged = function () {
        var me = this;
        return _.any(this.state.data, function (v, k) {
            if (k === 'needs') {
                if (v) return v.length !== me.needs.length || _.any(me.needs, function (need) {
                    return need.isChanged();
                });
                return me.needs.length > 0;
            }
            return v !== me.props.data[k]
        })
    };

    ServicePlanForm.prototype.render = function () {
        var me = this;

        var isNew = this.props.isNew;
        var isReadonly = this.props.isReadonly;

        var data = this.state.data;

        return {
            '<>': 'form',
            'class': 'service-plan-form',
            'html': [
                !isReadonly ? {'<>': 'a', 'class': 'anchor start-anchor', 'name': 'start'} : undefined,
                !isReadonly ? {'<>': 'div', 'class': 'form-section anchor-link-section'} : undefined,
                !isReadonly ? {'<>': 'a', 'class': 'anchor summary-anchor', 'name': 'summary'} : undefined,
                {'<>': 'div', 'class': 'form-section summary-section', 'html': [
                    {'<>': 'h3', 'text': 'Summary', 'class': 'section__header summary-section__header'},
                    {'<>': 'div', 'class': 'row', 'html': [
                        {'<>': 'div', 'class': 'col-md-6', 'html': [
                            {'<>': 'div', 'class': 'form-group date', 'html': [
                                {'<>': 'label', 'text': 'Date Created*'},
                                {
                                    '<>': 'input',
                                    'name': 'dateCreated',
                                    'type': 'text',
                                    'data-type': 'date',
                                    'class': 'form-control',
                                    'placeholder': 'mm/dd/yyyy hh:mm',
                                    'value': new Date(isNew ? new Date() : (data.dateCreated || new Date())).format('mm/dd/yyyy HH:MM TT') + ' ' + new Date().getTimezoneAbbr()
                                },
                                {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                            ]},
                            {'<>': 'div', 'class': 'checkbox', 'html': [
                                {'<>': 'label', 'html': [
                                    (function () {
                                        var input = {
                                            '<>': 'input',
                                            'type': 'checkbox',
                                            'name': 'completed'
                                        };

                                        if (data.completed) {
                                            input['checked'] = true;
                                        }

                                        return input;
                                    })(),
                                    {'<>': 'span', 'text': 'Mark service plan as completed'}
                                ]}
                            ]}
                        ]},
                        {'<>': 'div', 'class': 'col-md-6', 'html': [
                            {'<>': 'div', 'class': 'form-group', 'html': [
                                {'<>': 'label', 'for': 'createdDate', 'text': 'Created By*'},
                                {
                                    '<>': 'input',
                                    'id': 'createdBy',
                                    'name': 'createdBy',
                                    'type': 'text',
                                    'disabled': true,
                                    'class': 'form-control',
                                    'placeholder': 'Created By',
                                    'value': data.createdBy
                                }
                            ]},
                            isReadonly && data.completed ? {'<>': 'div', 'class': 'form-group date', 'html': [
                                {'<>': 'label', 'text': 'Date Completed'},
                                {
                                    '<>': 'input',
                                    'name': 'dateCompleted',
                                    'type': 'text',
                                    'data-type': 'date',
                                    'class': 'form-control',
                                    'placeholder': 'mm/dd/yyyy hh:mm',
                                    'value': data.dateCompleted && (new Date(data.dateCompleted)).format('mm/dd/yyyy')
                                },
                                {'<>': 'span', 'class': 'glyphicon glyphicon-calendar', 'aria-hidden': 'true'}
                            ]} : undefined
                        ]}
                    ]}
                ]},
                !isReadonly ? {'<>': 'div', 'class': 'form-section-list need-section-list', 'html': [
                    {'<>': 'div', 'class': 'row form-section-list__header', 'html': [
                        {'<>': 'div', 'class': 'col-md-6', 'html': [
                            {'<>': 'h3', 'text': 'Needs / Opportunities'}
                        ]},
                        {'<>': 'div', 'class': 'col-md-6', 'html': [
                            {
                                '<>': 'a',
                                'class': 'add-button pull-right',
                                'html': [
                                    {'<>': 'img', 'src': ADD_IMAGE_URL},
                                    {'<>': 'span', 'text': 'Add a Need / Opportunity'}
                                ],
                                'onclick': function () {
                                    me.onAddNeedSection();
                                }
                            }
                        ]}
                    ]}
                ]} : undefined
            ]
        }
    };

    return ServicePlanForm;
})($);
