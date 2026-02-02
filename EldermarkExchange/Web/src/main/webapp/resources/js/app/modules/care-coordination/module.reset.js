$(document).on('ready', function () {

    function _addFormValidation() {
        return $("#resetPasswordForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    password: {required: true},
                    confirmPassword: {required: true, equalTo: '#password'}
                },
                messages: {
                    password: {
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
        //var $form = $("#resetPasswordForm");
        if (!$form.valid()) {
            return false;
        }
        $.ajax({
            url: 'validatePasswordComplexityReset',
            type: 'POST',
            data: $form.serialize()
        }).success(function (data) {
            if (data==true) {
                $.ajax({
                    url: 'validatePasswordHistoryReset',
                    type: 'POST',
                    data: $form.serialize()
                }).success(function (data) {
                    if (data==true) {
                        $.ajax({
                            type: "POST",
                            url: "reset",
                            data: $form.serialize()
                        }).success(function () {
                            $alert.empty();

                            $alert.append("Password has been set. ");
                            $alert.append("Now you can login to ");
                            $alert.append($('#careCoordinationUrl'));

                            $alert.append(". Use your email as Login and '");
                            $alert.append($('#organizationCode').val());
                            $alert.append("' as Company ID.");
                            $form.find('.boxBody').hide();
                            $form.find('.btn').hide();
                            $alert.removeClass("hidden");
                        }).error(function () {
                            $alert.html("Error !");
                            $alert.removeClass("hidden");
                        });
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


    }

    function _setEvents() {
        var $form = $("#resetPasswordForm");
        var $alert = $form.find(".alert");

        $('#declineResetBtn').on('click', function () {
            $.ajax({
                type: "DELETE",
                url: "reset/" + $('#resetPasswordForm').find('[name="token"]').val()

            }).success(function () {
                //$form.find('.boxBody').hide();
                //$form.find('.btn').hide();
                //
                //$alert.html("Reset password was declined");
                //$alert.removeClass("hidden");
                window.location.href = "../login";
                return false;

            }).error(function () {
                $alert.html("Error !");
                $alert.removeClass("hidden");
            });
            return false;
        });

        $('#acceptResetBtn').on('click', function () {
            resetPassword($form,$alert);
            return false;
        });

        $('#resetPasswordForm input').on('keydown', function(event) {
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
