$(function() {

    $('#apply-values-41').click(function (e) {

        var paramArr = $('#iti41form').serializeArray();
        var params = new Object();

        for (var i in paramArr) {
            params[paramArr[i].name] = paramArr[i].value;
        }

        $.post("iti41/applyTemplate", params, function(data) {
            $('#template-input').val(data);
        })
    });
    $('#requestHead').click(function() {
        $('#requestBody').toggle();
    })

    $('#send-iti41upload').click(function() {
        var file = $('#fileInput')[0].files[0];
        var fileData = new FormData();
        fileData.append('file', file);

        $.ajax({
            url: "iti41/uploadDoc",
            type: "POST",
            enctype: 'multipart/form-data',
            /*dataType: 'json',*/
            data: fileData,
            cache: false,
            processData: false,
            contentType: false

        }).success(function (data) {
            console.log(data);
            $('#hash-input').val(data.hash);
            $('#size-input').val(data.size);
            $('#fileName-input').val(data.fileName);
            $('#documentContentEncoded-input').val(data.base64Content);
        });

    });

    var dataFormatted = formatXml($('#responseTextarea').val());
    $('#responseTextarea').val(dataFormatted);


    function formatXml(xml) {
        var formatted = '';
        var reg = /(>)(<)(\/*)/g;
        xml = xml.replace(reg, '$1\r\n$2$3');
        var pad = 0;
        jQuery.each(xml.split('\r\n'), function(index, node) {
            var indent = 0;
            if (node.match( /.+<\/\w[^>]*>$/ )) {
                indent = 0;
            } else if (node.match( /^<\/\w/ )) {
                if (pad != 0) {
                    pad -= 1;
                }
            } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
                indent = 1;
            } else {
                indent = 0;
            }

            var padding = '';
            for (var i = 0; i < pad; i++) {
                padding += '  ';
            }

            formatted += padding + node + '\r\n';
            pad += indent;
        });

        return formatted;
    }


});