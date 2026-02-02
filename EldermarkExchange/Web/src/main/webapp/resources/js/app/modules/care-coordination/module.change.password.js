$(document).on('ready', function () {

    var mdlUtils = ExchangeApp.utils.module;

    function _addFormValidation() {
        return $("#changePasswordForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    password: {required: true},
                    newPassword: {
                        required: true,
                    },
                    confirmNewPassword: {required: true, equalTo: '#newPassword'}
                },
                messages: {
                    password: {
                        required: getErrorMessage("field.empty")
                    },
                    newPassword: {
                        required: getErrorMessage("field.empty")
                    },
                    confirmNewPassword: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }

    function _setEvents() {
        $('#changePasswordBtn').on('click', function (event) {
            event.preventDefault()
            var $form = $("#changePasswordForm");
            if (!$form.valid()) {
                return false;
            }
            $.ajax({
                url: 'service/validatePasswordComplexity',
                type: 'POST',
                beforeSend: function(xhr){
                    mdlUtils.csrf(xhr);
                },
                data: $form.serialize()
            }).success(function (data) {
                if (data==true) {
                    $.ajax({
                        url: 'service/validatePasswordHistory',
                        type: 'POST',
                        beforeSend: function(xhr){
                            mdlUtils.csrf(xhr);
                        },
                        data: $form.serialize()
                    }).success(function (data) {
                        if (data==true) {
                            $form.submit();
                        } else {
                            $("#loginError").text('In order to secure your account, please create a unique password you have not used before');
                        }
                    }).fail(function (response) {
                        $("#loginError").text('Internal server error');
                    });
                } else {
                    $("#loginError").text('Password does not meet the password requirements (see below)');
                }
            }).fail(function (response) {
                $("#loginError").text('Internal server error');
            });
        });

    }

    _addFormValidation();
    _setEvents();
});