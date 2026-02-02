/**
 * Created by stsiushkevich on 19.09.18.
 */

ServicePlanForm.NeedSection = (function () {

    var context = ExchangeApp.info.context;

    var ADD_IMAGE_URL = context + '/resources/images/add-button-rounded.svg';

    var REMOVE_TASK_WARN_TEXT = 'Relevant Activation or Education Task will be deleted';
    var REMOVE_NEED_OPPORTUNITY_WARN_TEXT = 'Need / Opportunity with the related goals will be deleted';

    function isEmpty (v) {
        return !v || Object.keys(v).length === 0
    }

    function showRemoveConfirm (text, onOk, onCancel) {
        bootbox.confirm({
            message: text,
            buttons: {
                cancel: {
                    label: 'Cancel'
                },
                confirm: {
                    label: 'Ok'
                }
            },
            callback: function (isConfirmed) {
                if (isConfirmed) {
                    onOk && onOk();
                } else {
                    onCancel && onCancel();
                }
            }
        });
    }

    function Section () {
        Widget.apply(this, arguments);

        this.goals = [];

        this.state = {
            data: this.props.data
        }
    }

    Section.prototype = Object.create(Widget.prototype);
    Section.prototype.constructor = Section;

    Section.prototype.getDefaultProps = function () {
        return {
            index: 0,
            data: {},
            isReadonly: false,
            onRemove: function () {},
            onChangeType: function () {},
            onGoalSectionAdded: function () {},
            onGoalSectionRemoved: function () {}
        };
    };

    Section.prototype.onChange = function (e) {
        var $elem = $(e.target);

        var name = $elem.attr('data-name');
        var isNeedField = !!$elem.attr('data-need-field');

        if (isNeedField) {
            var value = $elem.val();

            var data = $.extend({}, this.state.data);
            data[name] = value;

            if (name === 'type') {
                var domain = this.state.data.type;
                var taskType = 'Relevant Activation or Education Task';

                if (value === taskType || domain === taskType) {
                    data = {};
                    data[name] = value;

                    if (this.isNotEmpty({exclude: ['type']})) {
                        var me = this;

                        var text = domain === taskType ?
                            REMOVE_TASK_WARN_TEXT : REMOVE_NEED_OPPORTUNITY_WARN_TEXT;

                        showRemoveConfirm(text, function () {
                            me.goals = [];
                            me.props.onChangeType(value, me.props.index);
                            me.setState({data: data});
                        }, function () {
                            $elem.val(me.state.data.type)
                        });
                    } else {
                        this.goals = [];
                        this.props.onChangeType(value, this.props.index);
                        this.setState({data: data});
                    }
                } else {
                    this.props.onChangeType(value, this.props.index);
                    this.setState({data: data});
                }
            } else {
                var type = $elem.attr('data-type') || $elem.attr('type');

                if (type === 'date') {
                    data[name] = e.date ? e.date.toDate().getTime() : '';
                }

                this.setState({data: data});
            }
        }
    };

    Section.prototype.onRemove = function () {
        var cb = this.props.onRemove;
        var index = this.props.index;

        this.remove(function () {
            cb && cb(index);
        });
    };

    Section.prototype.onAddGoalSection = function () {
        var me = this;

        var goal = new ServicePlanForm.NeedSection.GoalSection({
            container: this.$element.find('.goal-section-list'),
            index: this.goals.length,
            needIndex: this.props.index,
            isReadonly: this.props.isReadonly,
            onRemove: function (index) {
                me.onRemoveGoalSection(index);
            }
        });

        goal.mount();

        this.goals.push(goal);

        this.props.onGoalSectionAdded();
    };

    Section.prototype.onRemoveGoalSection = function (index) {
        this.goals = $.map(this.goals, function (g, i) {
            if (i !== index) return g;
        });

        $.each(this.goals, function (i, g) {
            g.update({ index: i });
        });

        this.props.onGoalSectionRemoved();
    };

    Section.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');
        this.dom = this.$element.get(0);

        var me = this;

        this.$element
            .find('input[data-type="date"]')
            .datetimepicker({
                format: 'MM/DD/YYYY HH:mm',
                widgetPositioning: {
                    vertical: 'bottom'
                }
            })
            .on("dp.change", function (e) {
                me.onChange(e);
            });

        var isReadonly = this.props.isReadonly;

        this.$element.find('input, select, textarea').each(function () {
            var $elem = $(this);
            var name = $elem.attr('name');
            var isNeedField = !!$elem.attr('data-need-field');

            if (isNeedField) {
                if ($elem.is('textarea[data-autoresizable="true"]')) $elem.autoresize();

                if (isReadonly) {
                    $elem.attr($elem.is('select') ? 'disabled' : 'readonly', 'true');
                }
            }
        });

        var goals = this.props.data.goals;

        if (goals) {
            var index = this.props.index;

            $.each(goals, function (i, g) {
                var goal = new ServicePlanForm.NeedSection.GoalSection({
                    container: me.$element.find('.goal-section-list'),
                    index: i,
                    needIndex: index,
                    data: g,
                    isReadonly: isReadonly,
                    onRemove: function (index) {
                        me.onRemoveGoalSection(index);
                    }
                });

                goal.mount();

                me.goals.push(goal)
            })
        }

        this.addOnChangeHandler(function (e) {
            me.onChange(e);
        })
    };

    Section.prototype.componentDidUpdate = function (prevProps, prevState) {
        var index = this.props.index;
        var prevIndex = prevProps.index;

        if (index !== prevIndex) {
            this.$element.find('.anchor').attr('name', 'need-' + index);
            this.$element.find('.section__header h4').text('Need / Opportunity #' + (index + 1));
        }

        var type = this.state.data.type;

        if (type !== prevState.data.type) {
            var taskType = 'Relevant Activation or Education Task';

            if (type === taskType || prevState.data.type === taskType) {
                this.remount();
            }
        }
    };

    Section.prototype.addOnChangeHandler = function (handler) {
        this.$element.on('change', handler)
    };

    Section.prototype.remove = function (cb) {
        if (this.isNotEmpty()) {
            var me = this;

            var type = this.state.data.type;

            var text = type === 'Relevant Activation or Education Task' ?
                REMOVE_TASK_WARN_TEXT : REMOVE_NEED_OPPORTUNITY_WARN_TEXT;

            showRemoveConfirm(text, function () {
                cb && cb();
                me.unmount();
            });
        } else {
            cb && cb();
            this.unmount();
        }
    };

    Section.prototype.getIndex = function () {
        return this.props.index;
    };

    Section.prototype.getGoalSection = function (index) {
        return this.goals[index];
    };

    Section.prototype.getData = function () {
        var goals = $.map(this.goals, function (g) {
            var data = g.getData();
            if (!isEmpty(data)) return data;
        });

        return $.extend({},
            this.state.data,
            {goals: goals || []}
        );
    };

    Section.prototype.isNotEmpty = function (params) {
        var isNotEmpty = false;
        var data = this.getData();
        var excluded = (params || {}).exclude || [];

        $.each(data, function (k, v) {
            if (!~excluded.indexOf(k) && !isEmpty(v)) isNotEmpty = true;
        });

        return isNotEmpty;
    };

    Section.prototype.isChanged = function () {
        var me = this;
        return _.any(this.state.data, function (v, k) {
            if (k === 'goals') {
                if (v) return v.length !== me.goals.length || _.any(me.goals, function (goal) {
                    return goal.isChanged();
                });
                return me.goals.length > 0;
            }
            return v !== me.props.data[k]
        })
    };

    Section.prototype.render = function () {
        var me = this;

        var index = this.props.index;
        var isReadonly = this.props.isReadonly;

        var data = this.state.data;

        var type = data.type;
        var goals = data.goals;
        var priority = data.priority;
        var isTask = type === 'Relevant Activation or Education Task';

        return {'<>': 'div', 'class': 'form-section need-section', 'html': [
            {'<>': 'div', 'class': 'section__header need-section__header', 'html': [
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        {'<>': 'a', 'name': 'need-' + index, 'class': 'anchor'},
                        {'<>': 'h4', 'text': 'Need / Opportunity #' + (index + 1)}
                    ]},
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        !isReadonly ? {
                            '<>': 'a',
                            'class': 'add-button pull-right',
                            'html': [
                                {'<>': 'span', 'text': 'Remove'}
                            ],
                            'onclick': function () {
                                me.onRemove(index);
                            }
                        } : undefined
                    ]}
                ]}
            ]},
            {'<>': 'div', 'class': 'need-section__body', 'html': [
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Domain*'},
                            {
                                '<>': 'select',
                                'name': 'need.'+ index +'.type',
                                'data-name': 'type',
                                'data-need-field': true,
                                'class': 'form-control',
                                'placeholder': 'Select',
                                'html': [
                                    $.map([
                                        'Select',
                                        'Behavioral / Spiritual Health',
                                        'Caregiver Resource / Support',
                                        'Employment',
                                        'Health Status',
                                        'Housing',
                                        'Housing / Home Security & Safety',
                                        'Mental wellness',
                                        'Nutrition Security',
                                        'Other / Non-Specific',
                                        'Physical wellness',
                                        'Relevant Activation or Education Task',
                                        'Social wellness',
                                        'Transportation',
                                        'Legal',
                                        'Finances',
                                        'Medical / Other Supply',
                                        'Medication Management and Assistance',
                                        'Home Health'
                                    ], function (text) {
                                        var option = {'<>': 'option', 'text': text};

                                        if (text === 'Select') $.extend(option, {'value': '', 'hidden': true}, !type && {'selected': true});
                                        if (text === type) $.extend(option, {'selected': true});

                                        return option;
                                    })
                                ]
                            }
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Priority*'},
                            {
                                '<>': 'select',
                                'name': 'need.' + index + '.priority',
                                'data-name': 'priority',
                                'data-need-field': true,
                                'class': 'form-control',
                                'html': [
                                    $.map([
                                        'Select',
                                        'High',
                                        'Medium',
                                        'Low'
                                    ], function (text) {
                                        var option = {'<>': 'option', 'text': text};

                                        if (text === 'Select') $.extend(option, {'value': '', 'hidden': true}, !priority && {'selected': true});
                                        if (text === priority) $.extend(option, {'selected': true});

                                        return option;
                                    })
                                ]
                            }
                        ]}
                    ]}
                ]},
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': isTask ? 'Activation or Education Task*': 'Need / Opportunity*'},
                            {
                                '<>': 'textarea',
                                'rows': 1,
                                'name': 'need.'+ index + (isTask ? '.activationOrEducationTask' : '.needOpportunity'),
                                'data-name': isTask ? 'activationOrEducationTask' : 'needOpportunity',
                                'data-need-field': true,
                                'data-autoresizable': true,
                                'class': 'form-control autoresizable',
                                'text': (isTask ? data.activationOrEducationTask : data.needOpportunity) || ''
                            }
                        ]}
                    ]}
                ]},
                isTask ? {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Target Completion Date*'},
                            {
                                '<>': 'input',
                                'name': 'need.'+ index +'.targetCompletionDate',
                                'data-name': 'targetCompletionDate',
                                'data-need-field': true,
                                'type': 'text',
                                'data-type': 'date',
                                'class': 'form-control',
                                'placeholder': 'mm/dd/yyyy hh:mm',
                                'value': data.targetCompletionDate && (new Date(data.targetCompletionDate)).format('mm/dd/yyyy HH:MM')
                            }
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Completion Date'},
                            {
                                '<>': 'input',
                                'name': 'need.'+ index +'.completionDate',
                                'data-name': 'completionDate',
                                'data-need-field': true,
                                'type': 'text',
                                'data-type': 'date',
                                'class': 'form-control',
                                'placeholder': !isReadonly ? 'mm/dd/yyyy hh:mm' : '',
                                'value': data.completionDate && (new Date(data.completionDate)).format('mm/dd/yyyy HH:MM')
                            }
                        ]}
                    ]}
                ]} : undefined,
                !isTask ? {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'for': 'createdDate', 'text': 'Proficiency / Graduation Criteria'},
                            {
                                '<>': 'textarea',
                                'rows': 4,
                                'name': 'need.'+ index +'.proficiencyGraduationCriteria',
                                'data-name': 'proficiencyGraduationCriteria',
                                'data-need-field': true,
                                'data-autoresizable': true,
                                'style': 'min-height: 82px',
                                'class': 'form-control autoresizable',
                                'text': data.proficiencyGraduationCriteria || ''
                            }
                        ]}
                    ]}
                ]} : undefined,
                !isTask && (!isReadonly || goals) ? {'<>': 'div', 'class': 'form-section-list goal-section-list', 'html': [
                    {'<>': 'div', 'class': 'row form-section-list__header goal-section-list__header', 'html': [
                        {'<>': 'div', 'class': 'col-md-6', 'html': [
                            {'<>': 'h4', 'text': 'Goals'}
                        ]},
                        {'<>': 'div', 'class': 'col-md-6', 'html': [
                            !isReadonly ? {
                                '<>': 'a',
                                'class': 'add-button pull-right',
                                'onclick': function () {
                                    me.onAddGoalSection();
                                },
                                'html': [
                                    {'<>': 'img', 'src': ADD_IMAGE_URL},
                                    {'<>': 'span', 'text': 'Add a Goal'}
                                ]
                            } : undefined
                        ]}
                    ]}
                ]} : undefined
            ]}
        ]};
    };

    return Section;
})($);