/**
 * Created by stsiushkevich on 21.09.18.
 */

function Loader () {
    Widget.apply(this, arguments);
}

Loader.prototype = Object.create(Widget.prototype);
Loader.prototype.constructor = Loader;

Loader.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');
};

Loader.prototype.show = function () {
    this.$element.css({display: 'flex'});
};

Loader.prototype.hide = function () {
    this.$element.css({display: ''});
};

Loader.prototype.render = function () {
    var context = ExchangeApp.info.context;
    var src = context + '/resources/images/ajax-x-loader.gif';

    return {'<>': 'div', 'class': 'x-loader', 'html': [
            {'<>': 'img', 'class': 'x-loader__icon', 'src': src}
    ]}
};