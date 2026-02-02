/**
 * Created by stsiushkevich on 19.09.18.
 */

ServicePlanForm.NeedSection.GoalSection = (function () {

    var REMOVE_WARN_TEXT = 'Goal with the corresponding  fields will be deleted';

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

        this.state = {
            data: this.props.data
        }
    }

    Section.prototype = Object.create(Widget.prototype);
    Section.prototype.constructor = Section;

    Section.prototype.getDefaultProps = function () {
        return {
            index: 0,
            data: {}
        };
    };

    Section.prototype.onChange = function (e) {
        var $elem = $(e.target);

        var value = $elem.val();
        var name = $elem.attr('data-name');
        var type = $elem.attr('data-type') || $elem.attr('type');

        if (type === 'date') {
            value = e.date ? e.date.toDate().getTime() : '';
        }

        var data = $.extend({}, this.state.data);
        data[name] = value;

        this.setState({data: data});
    };

    Section.prototype.onRemove = function () {
        var cb = this.props.onRemove;
        var index = this.props.index;

        if (this.isNotEmpty()) {
            var me = this;

            showRemoveConfirm(REMOVE_WARN_TEXT, function () {
                cb && cb(index);
                me.unmount();
            });
        } else {
            cb && cb(index);
            this.unmount();
        }
    };

    Section.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');
        this.dom = this.$element.get(0);

        var me = this;

        this.$element
            .find('input[data-type="date"]')
            .datetimepicker({
                format: 'MM/DD/YYYY HH:mm A [' + new Date().getTimezoneAbbr() + ']'
            })
            .on("dp.change", function (e) {
                me.onChange(e);
            });

        this.$element
            .find('input[data-name="progress"]')
            .on('input', function () {
                var v = $(this).val();

                if (v !== '') {
                    v= +v;
                    if (_.isNaN(v)) v = '';
                    else if (v > 100) v = 100;

                    $(this).prop('value', v);
                }
            });

        var isReadonly = this.props.isReadonly;

        this.$element.find('input, select, textarea').each(function () {
            var $elem = $(this);
            var name = $elem.attr('name');

            if (isReadonly) $elem.attr('disabled', 'true');

            if ($elem.is('textarea[data-autoresizable="true"]')) $elem.autoresize();
        });

        this.addOnChangeHandler(function (e) {
            me.onChange(e);
        })
    };

    Section.prototype.componentDidUpdate = function (prevProps) {
        var index = this.props.index;
        var prevIndex = prevProps.index;

        if (index != prevIndex) {
            this.$element.find('.section__header h5').text('Goal #' + (index + 1));

            this.$element.find('input, textarea').each(function () {
                var $elem = $(this);
                var name = $elem.attr('name');

                var rg = /goal\.\d+/;
                var s = 'goal.' + index;

                $elem.attr('name', name.replace(rg, s));
            });
        }
    };

    Section.prototype.addOnChangeHandler = function (handler) {
        this.$element.on('change', handler)
    };

    Section.prototype.getNeedIndex = function () {
        return this.props.needIndex;
    };

    Section.prototype.getIndex = function () {
        return this.props.index;
    };

    Section.prototype.getData = function () {
        return $.extend({}, this.state.data);
    };

    Section.prototype.isNotEmpty = function () {
        var isNotEmpty = false;
        var data = this.getData();

        $.each(data, function (k, v) {
            if (!isEmpty(v)) isNotEmpty = true;
        });

        return isNotEmpty;
    };

    Section.prototype.isChanged = function () {
        var me = this;
        return _.any(this.state.data, function (v, k) {
            return v !== me.props.data[k];
        })
    };

    Section.prototype.render = function () {
        var me = this;

        var index = this.props.index;
        var needIndex = this.props.needIndex;
        var isReadonly = this.props.isReadonly;

        var data = this.state.data;

        return {'<>': 'div', 'class': 'form-section goal-section', 'html':[
            {'<>': 'div', 'class': 'section__header goal-section__header', 'html':[
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        {'<>': 'h5', 'text': 'Goal #' + (index + 1)}
                    ]},
                    {'<>': 'div', 'class': 'col-md-6', 'html': [
                        !isReadonly ? {
                            '<>': 'a',
                            'class': 'add-button pull-right',
                            'html': [
                                {'<>': 'span', 'text': 'Remove'}
                            ],
                            'onclick': function () {
                                me.onRemove();
                            }
                        } : undefined
                    ]}
                ]}
            ]},
            {'<>': 'div', 'class': 'goal-section__body', 'html':[
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'for': 'createdDate', 'text': 'Goal*'},
                            {
                                '<>': 'textarea',
                                'rows': 1,
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.goal',
                                'data-name': 'goal',
                                'data-autoresizable': true,
                                'data-goal-field': true,
                                'class': 'form-control autoresizable',
                                'text': data.goal || ''
                            }
                        ]}
                    ]}
                ]},
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'for': 'createdDate', 'text': 'Barriers'},
                            {
                                '<>': 'textarea',
                                'rows': 4,
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.barriers',
                                'data-name': 'barriers',
                                'data-autoresizable': true,
                                'data-goal-field': true,
                                'style': 'min-height: 82px',
                                'class': 'form-control autoresizable',
                                'text': data.barriers || ''
                            }
                        ]}
                    ]}
                ]},
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'for': 'createdDate', 'text': 'Intervention / Action'},
                            {
                                '<>': 'textarea',
                                'rows': 4,
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.interventionAction',
                                'data-name': 'interventionAction',
                                'data-autoresizable': true,
                                'data-goal-field': true,
                                'style': 'min-height: 82px',
                                'class': 'form-control autoresizable',
                                'text': data.interventionAction || ''
                            }
                        ]}
                    ]}
                ]},
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-12', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Resource Name'},
                            {
                                '<>': 'textarea',
                                'rows': 1,
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.resourceName',
                                'data-name': 'resourceName',
                                'data-autoresizable': true,
                                'data-goal-field': true,
                                'class': 'form-control autoresizable',
                                'text': data.resourceName || ''
                            }
                        ]}
                    ]}
                ]},
                {'<>': 'div', 'class': 'row', 'html': [
                    {'<>': 'div', 'class': 'col-md-4', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Target Completion Date*'},
                            {
                                '<>': 'input',
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.targetCompletionDate',
                                'data-name': 'targetCompletionDate',
                                'type': 'text',
                                'data-type': 'date',
                                'data-goal-field': true,
                                'class': 'form-control',
                                'placeholder': 'mm/dd/yyyy hh:mm',
                                'value': data.targetCompletionDate && (new Date(data.targetCompletionDate).format('mm/dd/yyyy HH:MM TT') + ' ' + new Date().getTimezoneAbbr())
                            }
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'col-md-4', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Completion Date'},
                            {
                                '<>': 'input',
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.completionDate',
                                'data-name': 'completionDate',
                                'type': 'text',
                                'data-type': 'date',
                                'data-goal-field': true,
                                'class': 'form-control',
                                'placeholder': !isReadonly ? 'mm/dd/yyyy hh:mm' : '',
                                'value': data.completionDate && (new Date(data.completionDate).format('mm/dd/yyyy HH:MM TT') + ' ' + new Date().getTimezoneAbbr())
                            }
                        ]}
                    ]},
                    {'<>': 'div', 'class': 'col-md-4', 'html': [
                        {'<>': 'div', 'class': 'form-group', 'html': [
                            {'<>': 'label', 'text': 'Goal completion, %'},
                            {
                                '<>': 'input',
                                'name': 'need.'+ needIndex +'.goal.'+ index +'.progress',
                                'data-name': 'progress',
                                'data-goal-field': true,
                                'type': 'text',
                                'class': 'form-control',
                                'value': data.progress || ''
                            }
                        ]}
                    ]}
                ]}
            ]}
        ]}
    };

    return Section;
})($);