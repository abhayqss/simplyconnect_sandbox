$(function() {
    $('#loginForm').validate({
        rules: {
            username: {
                required: true
            },
            password: {
                required: true
            },
            company: {
                required: true
            }
        }
    });
});