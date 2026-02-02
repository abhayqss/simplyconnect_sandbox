/**
 * Created by stsiushkevich on 25.10.18.
 */

function Popover() {
    Widget.apply(this, arguments);
}

Popover.prototype = Object.create(Widget.prototype);
Popover.prototype.constructor = Popover;

Popover.prototype.getDefaultProps = function () {
    return {
        title: '',
        className: '',
        trigger: 'click',
        isHtmlUsed: false,
        onShow: function () {},
        onShown: function () {},
        onHide: function () {},
        onHidden: function () {},
        renderTitle: function () { return '' },
        renderContent: function () { return '' }
    };
};

Popover.prototype.mount = function () {
    var template = this.render();

    if (template) {
        template['cmp-id'] = this.$$id;

        this.$target = $(this.props.element).popover({
            html: this.props.isHtmlUsed,
            trigger: this.props.trigger,
            title: json2html.transform({}, this.props.renderTitle()),
            content: json2html.transform({}, this.props.renderContent()),
            template: json2html.transform({}, template),
            viewport: this.props.viewport
        })
    }

    this.componentDidMount();
};

Popover.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');

    this.$element.$title = this.$element.find('.popover-title');
    this.$element.$content = this.$element.find('.popover-content');

    this.addOnShowHandler(this.props.onShow);
    this.addOnShownHandler(this.props.onShown);

    this.addOnHideHandler(this.props.onHide);
    this.addOnHiddenHandler(this.props.onHidden);
};

Popover.prototype.unmount = function () {
    this.componentWillUnmount();
    this.$target.popover('destroy');
};

Popover.prototype.render = function () {
    var className = this.props.className;

    return {'<>': 'div', 'class': 'popover ' + className, 'role': 'tooltip', 'html': [
        {'<>': 'div', 'class': 'arrow'},
        {'<>': 'h3', 'class': 'popover-title'},
        {'<>': 'div', 'class': 'popover-content'}
    ]}
};

Popover.prototype.show = function () {
    this.$target.popover('show');
};

Popover.prototype.hide = function () {
    this.$target.popover('hide');
};

Popover.prototype.addOnShowHandler = function (handler) {
    this.$target.on('show.bs.popover', handler);
};

Popover.prototype.addOnShownHandler = function (handler) {
    this.$target.on('shown.bs.popover', handler);
};

Popover.prototype.addOnHideHandler = function (handler) {
    this.$target.on('hide.bs.popover', handler);
};

Popover.prototype.addOnHiddenHandler = function (handler) {
    this.$target.on('hidden.bs.popover', handler);
};