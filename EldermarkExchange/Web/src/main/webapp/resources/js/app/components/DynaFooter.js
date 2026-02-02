/**
 * Created by stsiushkevich on 05.09.18.
 */

function DynaFooter (options) {
    Widget.apply(this, arguments);
}

DynaFooter.prototype = Object.create(Widget.prototype);
DynaFooter.prototype.constructor = DynaFooter;

DynaFooter.prototype.getDefaultProps = function () {
    return {
        hasTopBorder: true,
        hasLeftArrow: false,
        hasBackBtn: false,
        backBtnText: 'Back',
        hasRightArrow: false,
        hasNextBtn: false,
        nextBtnText: 'Next',
        onBack: function () {},
        onNext: function () {},
        rightButtons: null
    }
};

DynaFooter.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');
};

DynaFooter.prototype.render = function () {
    var props = this.props;

    var hasTopBorder = props.hasTopBorder;

    var hasBackBtn = props.hasBackBtn;
    var hasNextBtn = props.hasNextBtn;

    var hasLeftArrow = props.hasLeftArrow;
    var hasRightArrow = props.hasRightArrow;

    var rightButtons = props.rightButtons;

    return {'<>': 'div', 'class': 'dyna-footer', 'html': [
        hasTopBorder ? {'<>': 'div', 'class': 'dyna-footer__top-border'} : undefined,
        {'<>': 'div', 'class': 'dyna-footer__left-buttons', 'html': [
            hasLeftArrow ? {'<>': 'a', 'class': 'dyna-footer__left-arrow', 'onclick': props.onBack} : undefined,
            hasBackBtn ? {'<>': 'a', 'class': 'dyna-footer__back-btn', 'text': props.backBtnText, 'onclick': props.onBack} : undefined,
            hasNextBtn ? {'<>': 'a', 'class': 'dyna-footer__next-btn', 'text': props.nextBtnText, 'onclick': props.onNext} : undefined,
            hasRightArrow ? {'<>': 'a', 'class': 'dyna-footer__right-arrow', 'onclick': props.onNext} : undefined
        ]},
        props.rightButtons ? {'<>': 'div', 'class': 'dyna-footer__right-buttons', 'html': [
            $.map(rightButtons, function (btn) {
                return {
                    '<>': 'a',
                    'class': 'dyna-footer__right-btn btn ' + btn.cssClass,
                    'style': btn.cssStyle,
                    'text': btn.text,
                    'onclick': btn.onClick
                }
            })
        ]}: undefined
    ]};
};