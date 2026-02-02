$(document).on('ready', function(){
    function _addLoginFormValidation() {
        return $("#loginForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    company: {required: true},
                    password: {required: true},
                    username: {required: true}
                },
                messages: {
                    company: {
                        required: getErrorMessage("field.empty")
                    },
                    username: {
                        required: getErrorMessage("field.empty")
                    },
                    password: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        );
    };

    function _setEvents() {
        $('#loginForm').submit(function(){
            if (!$(this).valid()) return false;
            return true;
            //$.ajax({
            //    type: "POST",
            //    url: window.auditReportLoginUrl,
            //    data: $('#loginForm').serialize()
            //});
        });
    };

    _addLoginFormValidation();
    _setEvents();
});