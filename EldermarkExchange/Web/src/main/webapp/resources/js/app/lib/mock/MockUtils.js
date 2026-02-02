define(['underscore', path('../Utils')], function (_, Utils) {

    var DateUtils = Utils.Date;

    var add = DateUtils.add;
    var format = DateUtils.format;
    var formats = DateUtils.formats;

    var ISO_DATE_TIME = formats.isoDateTime;

    function getRandomTimeObj() {
        var hStart =
            arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 8;
        var hEnd =
            arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 18;
        var mArray = arguments.length > 2 ? arguments[2] : undefined;
        var sArray = arguments.length > 3 ? arguments[3] : undefined;
        var hoursDirty = getRandomInt(hStart, hEnd);
        var hours = Math.min(hoursDirty, 23);
        var minutes = mArray ? getRandomArrayElement(mArray) : getRandomInt(0, 59);
        var seconds = sArray ? getRandomArrayElement(sArray) : getRandomInt(0, 59);
        return {
            hours: hours,
            minutes: minutes,
            seconds: seconds
        };
    }

    function getRandomDay(month, year, endDay) {
        var startDay =
            arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : 1;
        endDay = endDay || daysInMonth(month, year);
        return getRandomInt(startDay, endDay);
    }

    function getDateObjFromISO(isoStr) {
        return {
            year: Number(isoStr.substring(0, 4)),
            month: Number(isoStr.substring(5, 7)) - 1,
            day: Number(isoStr.substring(8, 10))
        };
    }

    function parseISO(isoStr) {
        var d = getDateObjFromISO(isoStr);
        return new Date(d.year, d.month, d.day);
    }

    function getDateObjFromPeriod(startDate, endDate) {
        var date = parseISO(startDate);
        var end = parseISO(endDate);
        var diff = end - date;
        var offset = getRandomInt(0, diff);
        date.setTime(date.getTime() + offset);
        return {
            day: date.getDate(),
            month: date.getMonth(),
            year: date.getFullYear()
        };
    }

    function daysInMonth(month, year) {
        return new Date(year, month, 0).getDate();
    }

    function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    function genRandomArray(length, limit) {
        var arr = [];

        var _limit = limit || length;

        for (var x = 0; x < length; x++) {
            arr.push({
                idx: x,
                rnd: getRandomInt(0, 9999)
            });
        }

        arr = _.sortBy(arr, "rnd");
        var a = [];

        _.each(arr, function (obj, index) {
            if (index < _limit) {
                a.push(obj.idx);
            }
        });

        return a;
    }

    function getRandomArrayElement(arr) {
        var idx = getRandomInt(0, arr.length - 1);
        return arr[idx];
    }

    function getRandomDateTimeStr(startDate, endDate) {
        var zone =
            arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : "Z";
        var d = getDateObjFromPeriod(startDate, endDate);
        var t = getRandomTimeObj();
        var date = new Date(d.year, d.month, d.day, t.hours, t.minutes, t.seconds);
        return format(date, ISO_DATE_TIME) + zone;
    }

    function getDateTimeFromStr(dateTimeStr) {
        var date = Date.parse(dateTimeStr);
        return isNaN(date) ? new Date() : date;
    }

    function getWeeklyDateTimeStr(endDate) {
        var zone =
            arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : "Z";
        var d = getDateObjFromISO(endDate);
        var t = getRandomTimeObj();
        var date = new Date(d.year, d.month, d.day, t.hours, t.minutes, t.seconds);
        var a = [];

        for (var i = 0; i < 7; i++) {
            a.push(format(date, ISO_DATE_TIME) + zone);
            date = add(date, -1, "day");
        }

        return a;
    }

    return {
        getRandomInt: getRandomInt,
        genRandomArray: genRandomArray,
        getRandomArrayElement: getRandomArrayElement,
        getRandomDateTimeStr: getRandomDateTimeStr,
        getDateTimeFromStr: getDateTimeFromStr,
        getWeeklyDateTimeStr: getWeeklyDateTimeStr
    }
});