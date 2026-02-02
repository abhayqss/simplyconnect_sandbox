$(function() {
    $('tr').click(function() {
        var logId = $(this).children('td:first').text();

        $('tr').each(function() {
            var currLogId = $(this).children('td:first').text();
            if(logId == currLogId && currLogId != '') {
                $(this).addClass('highlighted');
            } else {
                $(this).removeClass('highlighted');
            }
        });
    });


    $(document).mouseup(function (e) {
        var container = $('tr');
        if (!container.is(e.target) && container.has(e.target).length === 0)
        {
            container.removeClass('highlighted');
        }
    });
});
