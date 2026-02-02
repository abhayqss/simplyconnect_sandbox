/**
 * Created by stsiushkevich on 22.10.18.
 */

Table.Cell = (function ($) {

    function Cell () {
        Widget.apply(this, arguments);
    }

    Cell.prototype = Object.create(Widget.prototype);
    Cell.prototype.constructor = Cell;

    Cell.prototype.getDefaultProps = function () {
        return {
            data: '',
            isHead: false,
            rowIndex: 0,
            rowData: null,
            style: '',
            className: '',
            getStyle: function () { return '' },
            getClassName: function () { return '' },
            render: function (v) {
                return (v === null || v === undefined) ? '' : String(v);
            }
        };
    };

    Cell.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');
    };

    Cell.prototype.componentDidUpdate = function (prevProps) {
        var data = this.props.data;

        if (data !== prevProps.data) {
            this.$element.text(data);
        }
    };

    Cell.prototype.render = function () {
        var data = this.props.data;

        var rowIndex = this.props.rowIndex;
        var rowData = this.props.rowData;

        var isHead = this.props.isHead;

        var style = this.props.style;
        var getStyle = this.props.getStyle;

        var className = this.props.className;
        var getClassName = this.props.getClassName;

        var content = this.props.render(data, rowData, rowIndex);

        var template = {
            '<>': (isHead ? 'th' : 'td'),
            'class': (className + ' ' + getClassName(data, rowData, rowIndex)).trim(),
            'style': (style + ' ' + getStyle(data, rowData, rowIndex)).trim()
        };

        if (_.isString(content)) template['text'] = content;
        else template['html'] = [content];

        return template;
    };

    return Cell;
})($);