/**
 * Created by stsiushkevich on 19.09.18.
 */

function SuccessConfirmModal () {
    ConfirmModal.apply(this, arguments);
}

SuccessConfirmModal.prototype = Object.create(Modal.prototype);
SuccessConfirmModal.prototype.constructor = SuccessConfirmModal;

SuccessConfirmModal.prototype.getDefaultProps = function () {
    var me = this;

    var props = ConfirmModal.prototype.getDefaultProps.apply(this);

    return $.extend({}, props, {
        renderFooter: function () {
            return {
                '<>': 'div', 'class': 'modal-footer', 'html': [
                    {
                        '<>': 'button',
                        'class': 'btn btn-primary',
                        'data-dismiss': 'modal',
                        'text': 'OK',
                        'onclick': function () {
                            me.onOk();
                            me.unmount();
                        }
                    }
                ]
            }
        }
    });
};