/**
 * Created by stsiushkevich on 22.10.18.
 */

Table.Row = (function ($) {

    function Row () {
        Widget.apply(this, arguments);

        this.cells = [];
    }

    Row.prototype = Object.create(Widget.prototype);
    Row.prototype.constructor = Row;

    Row.prototype.getDefaultProps = function () {
        return {
            index: 0,
            data: [],
            columns: []
        };
    };

    Row.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        var me = this;
        $.each(this.props.columns, function (i, col) {
            var data = me.props.data;

            var cell = new Table.Cell({
                container: me.$element,
                style: col.style,
                getStyle: col.getStyle,
                className: col.className,
                getClassName: col.getClassName,
                rowIndex: me.props.index,
                rowData: data,
                render: col.render,
                data: data[col.name]
            });

            me.cells.push(cell);
            cell.mount();
        })
    };

    Row.prototype.componentDidUpdate = function (prevProps) {
        var data = this.props.data;

        if (data !== prevProps.data) {
            var me = this;
            $.each(this.cells, function (i, cell) {
                var col = me.props.columns[i];
                cell.update({ data: data[col.name] });
            });
        }
    };

    Row.prototype.render = function () {
        return {'<>': 'tr', 'html': []}
    };

    return Row;
})($);