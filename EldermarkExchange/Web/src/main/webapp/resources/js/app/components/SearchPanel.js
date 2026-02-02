/**
 * Created by stsiushkevich on 11.09.18.
 */

function SearchPanel() {
    Form.apply(this, arguments);

    this.state = {
        data: {
            search: ''
        }
    }
}

SearchPanel.prototype = Object.create(Form.prototype);
SearchPanel.prototype.constructor = SearchPanel;

SearchPanel.prototype.getDefaultProps = function () {
    return {
        fieldStyle: null,
        btnStyle: null
    };
};

SearchPanel.prototype.componentDidMount = function () {
    this.$element = $('[cmp-id="'+ this.$$id +'"] form');
    this.dom = this.$element.get(0);

    var onSearch = this.props.onSearch;

    if (onSearch) this.addOnSearchHandler(onSearch);

    var me = this;
    this.addOnChangeHandler(function (e) {
        me.onChange(e);
    });
};

SearchPanel.prototype.onChange = function (e) {
    var value = $(e.target).val();
    this.setState({ data: { search: value } });
};

SearchPanel.prototype.addOnChangeHandler = function (handler) {
    this.$element.on('change', handler)
};

SearchPanel.prototype.render = function () {
    return {
        '<>': 'div',
        'class': 'search-panel',
        'html': [
            {
                '<>': 'form',
                'role': 'form',
                'class': 'search-panel__form form-inline',
                'html': [
                    {
                        '<>': 'input',
                        'name': 'search',
                        'class': 'form-control',
                        'style': this.props.fieldStyle
                    },
                    {
                        '<>': 'button',
                        'name': 'searchBtn',
                        'class': 'btn btn-primary',
                        'style': this.props.btnStyle,
                        'text': 'SEARCH'
                    }
                ]
            }
        ]
    }
};

SearchPanel.prototype.addOnSearchHandler = function (handler) {
    this.onSubmit(handler)
};

SearchPanel.prototype.search = function () {
    this.submit()
};

SearchPanel.prototype.getData = function () {
    return this.state.data;
};