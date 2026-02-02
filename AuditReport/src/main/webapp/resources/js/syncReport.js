$(function() {
    $('#to').datepicker({
        showAnim: '',
        firstDay: 1,
        maxDate: 0
    });

    $('#from').datepicker({
        showAnim: '',
        firstDay: 1,
        maxDate: 0,
        numberOfMonths: 2
    });


    $('#reportFilter').validate({
        rules: {
            from: {
                date: true,
                maxDate: true
            },
            to: {
                date: true,
                maxDate: true
            }
        }
    });

    $(".highlight-no-values").each(function () {
        if ($(this).text() === 'No') {
            $(this).parent().addClass('italic');
        }
    });

    $.validator.addMethod("maxDate", function(value, element) {
        var today = new Date();
        today.setHours(23); // make it 23:59
        today.setMinutes(59);
        today.setSeconds(59);
        today.setMilliseconds(999);
        return (value == '' || Date.parse(value) < today);
    }, "Today's date is the maximum selectable date.");

    $.ajax({
        type: 'GET',
        contentType: 'json',
        url: 'minDate',
        success: function(data){
            $("#from").datepicker('option', 'minDate', new Date(data));
        }
    });
});