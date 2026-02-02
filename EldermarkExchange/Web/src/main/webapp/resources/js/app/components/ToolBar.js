/**
 * Created by stsiushkevich on 10.09.18.
 */

var ToolBar = (function($){

    var context = ExchangeApp.info.context;

    var ICONS = {
        EDIT: context + '/resources/images/pencil-edit-1.svg',
        DOWNLOAD: context + '/resources/images/download-arrow.svg'
    };

    function ToolBar () {
        Widget.apply(this, arguments)
    }

    ToolBar.prototype = Object.create(Widget.prototype);
    ToolBar.prototype.constructor = ToolBar;

    ToolBar.prototype.getDefaultProps = function () {
        return {
            items: []
        };
    };

    ToolBar.prototype.render = function () {
        return {
            '<>': 'div',
            'class': 'action-bar',
            'html': $.map(this.props.items, function (item) {
                return {
                    '<>': 'a',
                    'type': 'button',
                    'class': 'toolbar__item btn-default ' + item.cssClass,
                    'style': item.cssStyle,
                    'onclick': item.onClick,
                    'html': [
                        {
                            '<>': 'img',
                            'src': ICONS[item.type],
                            'aria-hidden': true
                        }
                    ]
                }
            })
        }
    };

    return ToolBar
})($);