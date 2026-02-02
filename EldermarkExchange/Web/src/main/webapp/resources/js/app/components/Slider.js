/**
 * Created by stsiushkevich on 22.10.18.
 */

function Slider () {
    Widget.apply(this, arguments);
}

Slider.prototype = Object.create(Widget.prototype);
Slider.prototype.constructor = Slider;

Slider.prototype.getDefaultProps = function () {
    return {
        min: 1,
        max: 5,
        step: 1,
        value: 1,
        onChange: function () {}
    };
};

Slider.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');
    this.$input = this.$element.find('input').bootstrapSlider(this.props);

    var me = this;
    this.addOnChangeListener(function () {
        me.props.onChange(Number($(this).val()));
    });

    setTimeout(function () {
        me.api('relayout');
    }, 300);
};

Slider.prototype.addOnChangeListener = function (handler) {
    this.$input.on('change', handler);
};

Slider.prototype.render = function () {
    var style = this.props.style;
    var className = this.props.className;

    return {'<>': 'div', 'class': 'ldr-slider-wrapper', 'html': [
        {'<>': 'input', 'type': 'text', 'class': className, 'style': style}
    ]};
};

Slider.prototype.api = function (method, params) {
    this.$input.bootstrapSlider(method, params);
};