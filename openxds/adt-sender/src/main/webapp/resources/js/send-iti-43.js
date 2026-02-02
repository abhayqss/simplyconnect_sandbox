$(function() {
    $('#apply-values-43').click(function (e) {
        var host = $('#host-input').val();
        var port = $('#port-input').val();
        var repoUniqueId = $('#repositoryUniqueId-input').val();
        var documentUniqueId = $('#documentId-input').val();

        var params = {
            'host':host,
            'port':port,
            'repositoryUniqueId':repoUniqueId,
            'documentUniqueId':documentUniqueId
        }

        $.get("iti43/applyTemplate", params, function(data) {
            $('#template-input').val(data);
        })
    });
    $('#requestHead').click(function() {
        $('#requestBody').toggle();
    })

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