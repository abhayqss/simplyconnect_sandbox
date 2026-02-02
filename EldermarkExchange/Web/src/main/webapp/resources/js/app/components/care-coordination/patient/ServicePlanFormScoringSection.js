/**
 * Created by stsiushkevich on 19.09.18.
 */

ServicePlanForm.ScoringSection = (function () {
    function Section () {
        Widget.apply(this, arguments);

        this.domains = [];
    }

    Section.prototype = Object.create(Widget.prototype);
    Section.prototype.constructor = Section;

    Section.prototype.getDefaultProps = function () {
        return {
            data: {},
            isShowed: true,
            isDisabled: false,
            onChangeDomainScore: function () {}
        };
    };

    Section.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        var data = this.props.data;

        var me = this;
        $.each(data, function (i, o) {
            var domain = null;

            if (o.name === 'task') {
                domain = new ServicePlanForm.ScoringSection.TaskDomain({
                    container: me.$element,
                    name: o.name,
                    title: o.title,
                    needs: o.needs
                });
            }

            else  {
                domain = new ServicePlanForm.ScoringSection.Domain({
                    container: me.$element,
                    name: o.name,
                    title: o.title,
                    score: o.score,
                    needs: o.needs,
                    isDisabled: me.props.isDisabled,
                    onChangeScore: function (v) {
                        me.props.onChangeDomainScore(o, v)
                    }
                });
            }

            me.domains.push(domain);
            domain.mount();
        })
    };

    Section.prototype.show = function () {
        this.$element.removeClass('hidden');
    };

    Section.prototype.hide = function () {
        this.$element.addClass('hidden');
    };

    Section.prototype.render = function () {
        var isShowed = this.props.data;
        return {'<>': 'div', 'class': 'form-section scoring-section ' + (!isShowed ? 'hidden' : ''), 'html':[]}
    };

    return Section;
})($);

ServicePlanForm.ScoringSection.Domain = (function ($) {
    function Domain () {
        Widget.apply(this, arguments);

        this.slider = null;
        this.needs = [];
    }

    Domain.prototype = Object.create(Widget.prototype);
    Domain.prototype.constructor = Domain;

    Domain.prototype.getDefaultProps = function () {
        return {
            title: '',
            name: '',
            score: 0,
            needs: [],
            isDisabled: false,
            onChangeScore: function () {}
        };
    };

    Domain.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        var $header = this.$element.find('.domain__header');

        this.$element.$header = $header;
        this.$element.$body = this.$element.find('.domain__body');

        var me = this;
        this.slider = new Slider({
            container: $header.find('.domain__scoring'),
            ticks: [0, 1, 2, 3, 4, 5],
            ticks_labels: ['0', '1', '2', '3', '4', '5'],
            tooltip: 'hide',
            enabled: !this.props.isDisabled,
            value: this.props.score,
            onChange: function (v) {
                me.props.onChangeScore(v)
            }
        });

        this.slider.mount();

        $.each(this.props.needs, function (i, o) {
            var need = new ServicePlanForm.ScoringSection.Domain.NeedDetails({
                container: me.$element.$body,
                index: i,
                data: o
            });

            me.needs.push(need);
            need.mount();
        });
    };

    Domain.prototype.render = function () {
        return {'<>': 'div', 'class': 'domain', 'html': [
            {'<>': 'div', 'class': 'domain__header', 'html': [
                {'<>': 'div', 'class': 'domain__title', 'text': this.props.title },
                {'<>': 'div', 'class': 'domain__scoring', 'html': []}
            ]},
            {'<>': 'div', 'class': 'domain__body', 'html': []}
        ]}
    };

    return Domain;
})($);

ServicePlanForm.ScoringSection.TaskDomain = (function ($) {

    var PRIORITY_COLORS = {
        high: 'red',
        medium: 'yellow',
        low: 'green'
    };

    function Domain () {
        Widget.apply(this, arguments);

        this.list = null;
    }

    Domain.prototype = Object.create(Widget.prototype);
    Domain.prototype.constructor = Domain;

    Domain.prototype.getDefaultProps = function () {
        return {
            title: '',
            name: '',
            needs: []
        };
    };

    Domain.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        this.$element.$header = this.$element.find('.domain__header');
        this.$element.$body = this.$element.find('.domain__body');

        this.list = new Table({
            container: this.$element.$body,
            className: 'info-table task-info-table',
            hasFooter: false,
            data: this.props.needs,
            columns: [
                {
                    name: 'priority',
                    title: '',
                    style: 'width: 38px',
                    getClassName: function (v) {
                        var color = PRIORITY_COLORS[v.toLowerCase()];
                        return 'indicator indicator_' + color;
                    },
                    render: function (v, rowData, rowIndex) {
                        return String(rowIndex + 1);
                    }
                },
                {
                    name: 'activationOrEducationTask',
                    title: 'Relevant Activation or Education Task'
                },
                {
                    name: 'targetCompletionDate',
                    title: 'Target Completion Date',
                    style: 'width: 112px',
                    render: function (v) {
                        return v ? (new Date(v)).format('mm/dd/yyyy') : ''
                    }
                },
                {
                    name: 'completionDate',
                    title: 'Completion Date',
                    style: 'width: 112px',
                    render: function (v) {
                        return v ? (new Date(v)).format('mm/dd/yyyy') : ''
                    }
                }
            ]
        });

        this.list.mount()
    };

    Domain.prototype.render = function () {
        return {'<>': 'div', 'class': 'domain', 'html': [
            {'<>': 'div', 'class': 'domain__header', 'html': [
                {'<>': 'div', 'class': 'domain__title', 'text': this.props.title },
                {'<>': 'div', 'class': 'domain__scoring', 'html': []}
            ]},
            {'<>': 'div', 'class': 'domain__body', 'html': []}
        ]}
    };

    return Domain;
})($);

ServicePlanForm.ScoringSection.Domain.NeedDetails = (function ($) {

    var PRIORITY_COLORS = {
        high: 'red',
        medium: 'yellow',
        low: 'green'
    };

    function Details () {
        Widget.apply(this, arguments);

        this.goalList = null;
    }

    Details.prototype = Object.create(Widget.prototype);
    Details.prototype.constructor = Details;

    Details.prototype.getDefaultProps = function () {
        return {
            index: 0,
            data: {}
        };
    };

    Details.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        var goals = this.props.data.goals;

        if (goals && goals.length > 0) {
            this.goalList = new Table({
                container: this.$element,
                className: 'info-table goal-list',
                data: goals,
                hasFooter: false,
                columns: [
                    {
                        title: 'Goal',
                        name: 'goal'
                    },
                    {
                        title: '%',
                        name: 'progress',
                        style: 'width: 67px; font-weight: bold'
                    },
                    {
                        title: 'Barriers',
                        name: 'barriers'
                    },
                    {
                        title: 'Intervention / Action',
                        name: 'interventionAction'
                    },
                    {
                        title: 'Resource Name',
                        name: 'resourceName',
                        style: 'width: 130px'
                    },
                    {
                        title: 'Target Completion Date',
                        name: 'targetCompletionDate',
                        style: 'width: 112px',
                        render: function (v) {
                            return v ? (new Date(v)).format('mm/dd/yyyy') : ''
                        }
                    },
                    {
                        title: 'Completion Date',
                        name: 'completionDate',
                        style: 'width: 112px',
                        render: function (v) {
                            return v ? (new Date(v)).format('mm/dd/yyyy') : ''
                        }
                    }
                ]
            });
            this.goalList.mount();
        }
    };

    Details.prototype.getData = function () {
        return this.props.data;
    };

    Details.prototype.render = function () {
        var index = this.props.index;

        var data = this.props.data;

        var priority = (data.priority || 'low').toLowerCase();
        var color = PRIORITY_COLORS[priority];

        var needOpportunity = data.needOpportunity;
        var proficiencyGraduationCriteria = data.proficiencyGraduationCriteria;

        return {'<>': 'div', 'class': 'need-details', 'html': [
            {'<>': 'div', 'class': 'need-description description', 'html': [
                {'<>': 'div', 'class': 'need-description__priority-indicator indicator indicator_' + color, 'html': [
                    {'<>': 'div', 'class': 'indicator__badge', 'text': String(index + 1)}
                ]},
                {'<>': 'div', 'class': 'need-description__fields description__fields', 'html': [
                    needOpportunity ? {'<>': 'div', 'class': 'need-description__field description__field need-opportunity field', 'html': [
                        {'<>': 'div', 'class': 'field__label', 'text': 'Need / Opportunity'},
                        {'<>': 'div', 'class': 'field__value', 'text': needOpportunity}
                    ]} : undefined,
                    proficiencyGraduationCriteria ? {'<>': 'div', 'class': 'need-description__field description__field proficiency-graduation-criteria field', 'html': [
                        {'<>': 'div', 'class': 'field__label', 'text': 'Proficiency / Graduation Criteria'},
                        {'<>': 'div', 'class': 'field__value', 'text': proficiencyGraduationCriteria}
                    ]} : undefined
                ]}
            ]}
        ]}
    };

    return Details;
})($);