<%@ tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="date" required="true" rtexprvalue="true" type="java.util.Date" %>
<%@ attribute name="pattern" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="cssStyle" required="false" rtexprvalue="true" %>

<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 1000000000) %></c:set>

<span ${not empty id ? 'id=' + id : ''} class="local-date ${cssClass}" style="${cssStyle}" data-id="${rand}">

</span>

<script type="application/javascript">
    (function ($) {
        var datetime = ${date.time};
        var formatPattern = '${pattern}';
        if (formatPattern.includes('z') || formatPattern.includes('Z')) {
            formatPattern = formatPattern.replace('Z', '').replace('z','');
            var dateToParse = datetime;
            if (!(dateToParse instanceof Date)) {
                dateToParse = new Date(dateToParse)
            }
            var timeString = dateToParse.toTimeString();
            var abbr = timeString.match(/\([a-z ]+\)/i);
            if (abbr && abbr[0]) {
                // 17:56:31 GMT-0600 (CST)
                // 17:56:31 GMT-0600 (Central Standard Time)
                abbr = abbr[0].match(/[A-Z]/g);
                abbr = abbr ? abbr.join('') : undefined;
            } else {
                // 17:56:31 CST
                // 17:56:31 GMT+0800 (台北標準時間)
                abbr = timeString.match(/[A-Z]{3,5}/g);
                abbr = abbr ? abbr[0] : undefined;
            }
            if (abbr == 'MST') {
                abbr = 'MSK';
            }
            console.log(abbr);
            var result = moment(datetime).format(formatPattern) + ' ' + abbr + ' ' + moment(datetime).format('Z');
            //result = result + ' ' + abbr;
            //result = result + ' ' +  moment(datetime).format('Z');
            $('[data-id=${rand}]').text(result);
        } else {
            var result = moment(datetime).format('${pattern}');
            $('[data-id=${rand}]').text(result);
        }
    })($);
</script>


