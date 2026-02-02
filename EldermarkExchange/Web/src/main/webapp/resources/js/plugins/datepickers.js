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

    $('#exportForPrevMonth').text("Export for ".concat(prevMonthName()));

    $.validator.addMethod("maxDate", function(value, element) {
        var today = new Date();
        today.setHours(23); // make it 23:59
        today.setMinutes(59);
        today.setSeconds(59);
        today.setMilliseconds(999);
        return (value == '' || Date.parse(value) < today);
    }, "Today's date is the maximum selectable date.");

    $('#filterForm').validate({
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
});


function prevMonthName(){
    var monthNames = ["December", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November"];

    var now = new Date();
    return monthNames[now.getMonth()];
}
