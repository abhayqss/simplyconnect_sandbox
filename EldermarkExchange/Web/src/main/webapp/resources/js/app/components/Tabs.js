/**
 * Created by stsiushkevich on 01.10.18.
 */

function Tabs () {
    Widget.apply(this, arguments);

    this.state = {
        activeIndex: this.props.activeIndex
    };
}

Tabs.prototype = Object.create(Widget.prototype);
Tabs.prototype.constructor = Tabs;

Tabs.prototype.getDefaultProps = function () {
    return {
        activeIndex: 0,
        className: ''
    };
};

Tabs.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');
    this.dom = this.$element.get(0);

    this.$menu = this.$element.find('.tabs__menu');
    this.$body = this.$element.find('.tabs__body');
};

Tabs.prototype.componentDidUpdate = function (prevProps, prevState) {
    var index = this.state.activeIndex;

    if (index !== prevState.activeIndex) {
        this.$menu.find('.tabs__menu__item').each(function (i) {
            $(this).toggleClass('tabs__menu__item_active', i === index);
        });

        this.$body.find('.tab-content').each(function (i) {
            $(this).toggleClass('tab-content_active', i === index);
        });
    }
};

Tabs.prototype.onChangeTab = function (index) {
    this.setState({activeIndex: index});
};

Tabs.prototype.render = function () {

    var me = this;
    return {'<>': 'div', 'class': 'tabs ' + this.props.className, 'html': [
        {'<>': 'ul', 'class': 'tabs__menu', 'html': [
            $.map(this.props.tabs, function (tab, i) {
                return {'<>': 'li', 'class': 'tabs__menu__item ' + (tab.isActive ? 'tabs__menu__item_active' : ''), 'html': [
                    {'<>': 'div', 'class': 'tab-title', 'text': tab.title}
                ], 'onclick': function () { me.onChangeTab(i) }};
            })
        ]},
        {'<>': 'div', 'class': 'tabs__body', 'html': [
            $.map(this.props.tabs, function (tab) {
                var content = tab.content;

                if (content) {
                    var className = 'tab-content ' + (content['class'] || '');

                    if(tab.isActive) className += ' tab-content_active';

                    content['class'] = className;

                    return content
                }

                return {'<>': 'div', 'class': 'tab-content ' + (tab.isActive ? 'tab-content_active' : ''), 'html': []};
            })
        ]}
    ]};
};