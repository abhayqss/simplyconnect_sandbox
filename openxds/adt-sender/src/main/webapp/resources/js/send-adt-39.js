$(function() {

    $('#apply-values-8').click(function (e) {

        var paramArr = $('#iti8form').serializeArray();
        var params = new Object();

        for (var i in paramArr) {
            params[paramArr[i].name] = paramArr[i].value;
        }

        $.post("iti8adt39/applyTemplate", params, function(data) {
            $('#template-input').val(data);
        })
    });
    $('#requestHead').click(function() {
        $('#requestBody').toggle();
    })

});