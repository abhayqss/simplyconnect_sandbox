/**
 * Created by stsiushkevich on 18.09.18.
 */

var AnchorLinkPanel = (function ($) {

    function renderItem (name, title) {
        return {
            '<>': 'a',
            'href': '#' + name,
            'class': 'anchor-link',
            'text': title
        }
    }

    function AnchorLinkPanel () {
        Widget.apply(this, arguments)
    }

    AnchorLinkPanel.prototype = Object.create(Widget.prototype);
    AnchorLinkPanel.prototype.constructor = AnchorLinkPanel;

    AnchorLinkPanel.prototype.getDefaultProps = function () {
        return { items: {} };
    };

    AnchorLinkPanel.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');
        this.dom = this.$element.get(0);
    };

    AnchorLinkPanel.prototype.componentDidUpdate = function (prevProps) {
        var items = this.props.items;
        var prevItems = prevProps.items;

        if (items !== prevItems) {
            this.$element.empty();

            this.$element.json2html({}, $.map(items, function (o) {
                return renderItem(o.name, o.title)
            }));
        }
    };

    AnchorLinkPanel.prototype.render = function () {
        return {
            '<>': 'div',
            'class': 'anchor-link-panel',
            'html': $.map(this.props.items, function (o) {
                return renderItem(o.name, o.title);
            })
        };
    };

    return AnchorLinkPanel;
})($);