/**
 * Created by stsiushkevich on 22.10.18.
 */

function Table () {
    Widget.apply(this, arguments);

    this.rows = [];
}

Table.prototype = Object.create(Widget.prototype);
Table.prototype.constructor = Table;

Table.prototype.getDefaultProps = function () {
    return {
        columns: [],
        hasHeader: true,
        hasFooter: true
    };
};

Table.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"]');

    this.$header = this.$element.find('thead');
    this.$footer = this.$element.find('tfoot');
    this.$body = this.$element.find('tbody');

    var me = this;
    $.each(this.props.data, function (i, o) {
        var row = new Table.Row({
            index: i,
            container: me.$body,
            data: o,
            columns: me.props.columns
        });
        me.rows.push(row);
        row.mount();
    });
};

Table.prototype.componentDidUpdate = function (prevProps) {
    var data = this.props.data;

    if (data !== prevProps.data) {
        var me = this;
        var rows = [];

        $.each(this.props.data, function (i, o) {
            if (me.rows[i]) {
                rows.push(me.rows[i]);
                me.rows[i].update({ data: o });
            } else {
                var row = new Table.Row({
                    container: me.$body,
                    data: o,
                    columns: me.props.columns
                });
                rows.push(row);
                row.mount();
            }
        });

        $.each(this.rows, function (i, row) {
            if (!rows[i]) row.unmount();
        })
    }
};

Table.prototype.render = function () {
    var style = this.props.style;
    var className = this.props.className;

    var columns = this.props.columns;
    var hasHeader = this.props.hasHeader;
    var hasFooter = this.props.hasFooter;

    return {'<>': 'table', 'class': 'table ' + className, 'style': style, 'html': [
        hasHeader ? {'<>': 'thead', 'html': [
            {'<>': 'tr', 'html': [
                $.map(columns, function (col) {
                    var text = col.title !== undefined ? col.title : '';
                    return {'<>': 'th', 'class': col.headClass, 'style': col.headStyle, 'text': text};
                })
            ]}
        ]} : undefined,
        {'<>': 'tbody', 'html': []},
        hasFooter ? {'<>': 'tfoot', 'html': [
            {'<>': 'tr', 'html': [
                $.map(columns, function (col) {
                    var text = col.title !== undefined ? col.title : '';
                    return {'<>': 'th', 'class': col.footClass, 'style': col.footStyle, 'text': text};
                })
            ]}
        ]} : undefined
    ]}
};