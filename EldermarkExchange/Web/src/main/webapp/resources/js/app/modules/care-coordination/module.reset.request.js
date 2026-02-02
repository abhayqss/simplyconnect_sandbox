$(document).on('ready', function () {
    function _addFormValidation() {
        return $("#resetPasswordRequestForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    email: {required: true}

                },
                password: {
                    company: {
                        required: getErrorMessage("field.empty")
                    },
                    confirmPassword: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    }


    function resetPassword($form,$alert) {
        var mdlUtils = ExchangeApp.utils.module;
        if (!$form.valid()) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: "resetRequest",
            data: $form.serialize(),
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            }
        }).complete(function () {
            $alert.empty();
            $alert.append("The email was sent, please check your email.");

            $form.find("#sendEmailBtn").hide();
            $form.find('.boxBody').hide();
            $alert.removeClass("hidden");
        });
    }

    function _setEvents() {
        var $form = $("#resetPasswordRequestForm");
        var $alert = $form.find(".alert");

        $('#backToLoginBtn').on('click', function () {
            window.location.href = "../login";
            return false;
        });

        $('#sendEmailBtn').on('click', function () {
            resetPassword($form,$alert);
            return false;
        });

        $('#resetPasswordRequestForm input').on('keydown', function(event) {
            if (event.keyCode === 13) {
                resetPassword($form,$alert);
                event.preventDefault();
                return false;
            }
        });
    }


    _addFormValidation();
    _setEvents();
});
