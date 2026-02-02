/**
 * Created by stsiushkevich on 12.09.18.
 */

var Modal = (function ($) {

    var context = ExchangeApp.info.context;

    function Modal() {
        Widget.apply(this, arguments);
    }

    Modal.prototype = Object.create(Widget.prototype);
    Modal.prototype.constructor = Modal;

    Modal.prototype.getDefaultProps = function () {
        var me = this;

        return {
            title: '',
            isOpen: false,
            hasCloseBtn: true,
            isScrollable: false,
            hasScrollableBody: true,
            isStatic: false,
            headerHeight: 50,
            bodyExtraHeight: 0,
            className: '',
            getTitle: function () {
                return '';
            },
            renderTitle: function () {
                var title = me.props.getTitle() || me.props.title;
                return {'<>': 'h4', 'class': 'modal-title', 'text': title}
            },
            renderHeader: function () {
                return {'<>': 'div', 'class': 'modal-header', 'html': [
                    me.props.hasCloseBtn ? {
                        '<>': 'button',
                        'type': 'button',
                        'class': 'close',
                        'data-dismiss': 'modal',
                        'aria-label': 'Close',
                        'html': [
                            {'<>': 'img', 'src': context + '/resources/images/cross.svg'}
                        ]
                    } : null,
                    me.props.renderTitle()
                ]};
            },
            renderBody: function () {
                var hasScrollableBody = me.props.hasScrollableBody;

                return {
                    '<>': 'div',
                    'class': 'modal-body ' + (!hasScrollableBody ? ' non-scrollable' : '')
                };
            },
            renderFooter: function () {
                return {'<>': 'div', 'class': 'modal-footer '};
            }
        };
    };

    Modal.prototype.componentDidMount = function () {
        this.$element = $('[cmp-id="'+ this.$$id +'"]');

        this.$content = this.$element.find('.modal-content');
        this.$header = this.$element.find('.modal-header');
        this.$body = this.$element.find('.modal-body');
        this.$footer = this.$element.find('.modal-footer');

        var me = this;

        var hh = this.props.headerHeight;
        var fh = this.props.footerHeight;

        if (hh && fh) {
            this.addOnShowHandler(function () {
                me.updateBodyHeight();
            });
        } else {
            this.addOnShownHandler(function () {
                me.updateBodyHeight();
            });
        }

        this.addOnResizeHandler(function () {
            me.updateBodyHeight();
        });

        if (this.props.isOpen) this.show();
    };

    Modal.prototype.updateBodyHeight = function () {
        var hh =  this.props.headerHeight || this.$header.outerHeight();
        var fh = this.props.footerHeight || this.$footer.outerHeight();
        var eh = this.props.bodyExtraHeight;

        this.$body.css({
            maxHeight: $(window).height() - hh - fh - 30 * 2 - eh
        });
    };

    Modal.prototype.render = function () {
        var isStatic = this.props.isStatic;
        var className = ' ' + this.props.className;
        var isScrollable = this.props.isScrollable;

        return {
            '<>': 'div',
            'class': 'modal fade ' + className + (!isScrollable ? 'non-scrollable' : ''),
            'data-backdrop': isStatic ? 'static' : true,
            'html': [
                {'<>': 'div', 'class': 'modal-dialog', 'style': 'width:1000px;', 'html': [
                    {'<>': 'div', 'class': 'modal-content', 'html': [
                        this.props.renderHeader(),
                        this.props.renderBody(),
                        this.props.renderFooter()
                    ]}
                ]}
            ]
        }
    };

    Modal.prototype.show = function () {
        this.$element.modal('show');
    };

    Modal.prototype.hide = function () {
        this.$element.modal('hide');
    };

    Modal.prototype.addOnShowHandler = function (handler) {
        this.$element.on('show.bs.modal', handler);
    };

    Modal.prototype.addOnShownHandler = function (handler) {
        this.$element.on('shown.bs.modal', handler);
    };

    Modal.prototype.addOnHideHandler = function (handler) {
        this.$element.on('hide.bs.modal', handler);
    };

    Modal.prototype.addOnHiddenHandler = function (handler) {
        this.$element.on('hidden.bs.modal', handler);
    };

    Modal.prototype.addOnResizeHandler = function (handler) {
        $(window).on('resize', handler);
    };

    return Modal;
})($);