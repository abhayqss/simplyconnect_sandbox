/**
 * Created by stsiushkevich on 19.09.18.
 */

function ConfirmModal () {
    Modal.apply(this, arguments);

    if (!this.props.renderBody) {
        var text = this.props.text || '';

        this.props.renderBody = function () {
            return {'<>': 'div', 'class': 'modal-body', 'text': text};
        };
    }
}

ConfirmModal.prototype = Object.create(Modal.prototype);
ConfirmModal.prototype.constructor = ConfirmModal;

ConfirmModal.prototype.getDefaultProps = function () {
    var me = this;

    var props = Modal.prototype.getDefaultProps.apply(this);

    return $.extend({}, props, {
        renderBody: null,
        renderFooter: function () {
            return {
                '<>': 'div', 'class': 'modal-footer', 'html': [
                    {
                        '<>': 'a',
                        'class': 'btn btn-default',
                        'text': 'CANCEL',
                        'onclick': function () {
                            me.onCancel();
                            me.hide();
                        }
                    },
                    {
                        '<>': 'a',
                        'class': 'btn btn-primary',
                        'text': 'OK',
                        'onclick': function () {
                            me.onOk();
                            me.hide();
                        }
                    }
                ]
            }
        }
    });
};

ConfirmModal.prototype.componentDidMount = function () {
    Modal.prototype.componentDidMount.apply(this);

    var me = this;
    this.addOnHiddenHandler(function () {
        me.unmount();
    });
};

ConfirmModal.prototype.onOk = function () {
    var cb = this.props.onOk;
    cb && cb();
};

ConfirmModal.prototype.onCancel = function () {
    var cb = this.props.onCancel;
    cb && cb();
};