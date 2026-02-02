$(document).on('ready', function () {
    function _addFormValidation() {
        return $("#resetPasswordForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    password: {required: true},
                    confirmPassword: {required: true, equalTo: '#password'}

                },
                password: {
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
    function sendNewInvitation($form) {
        //var $form = $("#resetPasswordForm");
        if (!$form.valid()) {
            return false;
        }
        $.ajax({
            url: 'sendNewInvitation',
            type: 'POST',
            data: $form.serialize()
        }).success(function (data) {
            $form.find("#sendNewInvitationBtn").hide();
            $form.find("#message").text("A new invitation link has been sent to your email address.");
        }).error(function (data) {
            $form.find("#message").text("There was an error while sending new invitation!");
            $form.find("#message").css("color","red");
        });
    }


    function resetPassword($form,$alert) {
            //var $form = $("#resetPasswordForm");
            if (!$form.valid()) {
                return false;
            }
            $.ajax({
                url: 'validatePasswordComplexityInvite',
                type: 'POST',
                data: $form.serialize()
            }).success(function (data) {
                if (data==true) {
                    $.ajax({
                        url: 'validatePasswordHistoryInvite',
                        type: 'POST',
                        data: $form.serialize()
                    }).success(function (data) {
                        if (data==true) {
                            $.ajax({
                                type: "POST",
                                url: "invite",
                                data: $form.serialize()
                            }).success(function () {
                                $alert.empty();

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
            return false;
    }

    function _setEvents() {
        var $form = $("#resetPasswordForm");
        var $alert = $form.find(".alert");

        $('#declineInvitationBtn').on('click', function () {
            $.ajax({
                type: "DELETE",
                url: "invite/" + $('#resetPasswordForm').find('[name="token"]').val()

            }).success(function () {
                $form.find('.boxBody').hide();
                $form.find('#acceptInvitationBtn').hide();
                $form.find('#declineInvitationBtn').text("BACK TO LOGIN");

                $('#declineInvitationBtn').off('click').on('click', function () {
                    window.location.href = "../login";
                    return false;
                });

                $alert.html("Invitation was declined.");
                $alert.removeClass("hidden");

            }).error(function () {
                $alert.html("Error !");
                $alert.removeClass("hidden");
            });
            return false;
        });

        $('#acceptInvitationBtn').on('click', function () {
            resetPassword($form,$alert);
        });

        $('#sendNewInvitationBtn').on('click', function () {
            sendNewInvitation($form);
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
