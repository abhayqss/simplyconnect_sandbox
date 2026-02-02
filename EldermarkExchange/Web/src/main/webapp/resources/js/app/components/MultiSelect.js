/**
 * Created by stsiushkevich on 22.10.18.
 */

function MultiSelect () {
    Widget.apply(this, arguments);

    this.rows = [];
}

MultiSelect.prototype = Object.create(Widget.prototype);
MultiSelect.prototype.constructor = MultiSelect;

MultiSelect.prototype.getDefaultProps = function () {
    return {
        value: [],
        options: [],
        isMultiple: true
    };
};

MultiSelect.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');

    this.$element.selectpicker();
};

MultiSelect.prototype.setValue = function (value) {
    this.$element.selectpicker('val', value)
};

MultiSelect.prototype.getValue = function () {
    return this.$element.selectpicker('val')
};

MultiSelect.prototype.selectAll = function () {
    return this.$element.selectpicker('selectAll')
};

MultiSelect.prototype.deselectAll = function () {
    return this.$element.selectpicker('deselectAll')
};

MultiSelect.prototype.render = function () {
    var style = this.props.style;
    var className = this.props.className;
    var isMultiple = this.props.isMultiple;

    var options = this.props.options;

    var tmpl = {
        '<>': 'select', 'class': 'multi-select ' + className, 'style': style, 'html': [
            $.map(options, function (o) {
                return {'<>': 'option', 'text': o.text || '', 'value': o.value}
            })
        ]
    }

    if (isMultiple) tmpl['multiple'] = true

    return tmpl
};