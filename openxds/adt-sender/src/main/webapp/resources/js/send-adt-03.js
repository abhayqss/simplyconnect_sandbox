$(function() {

    $('#apply-values-8').click(function (e) {

        var paramArr = $('#iti30form').serializeArray();
        var params = new Object();

        for (var i in paramArr) {
            params[paramArr[i].name] = paramArr[i].value;
        }

        $.post("iti30/applyTemplate", params, function(data) {
            $('#template-input').val(data);
        })
    });
    $('#requestHead').click(function() {
        $('#requestBody').toggle();
    })

});