define(['underscore', 'date-math'], function (_, Dates) {
    var MS_IN_SEC = 1000;
    var MS_IN_MIN = MS_IN_SEC * 60;
    var MS_IN_HOUR = MS_IN_MIN * 60;
    var MS_IN_DAY = MS_IN_HOUR * 24;
    var MS_IN_WEEK = MS_IN_DAY * 7;

    function pad (val, len) {
        val = String(val);
        len = len || 2;
        while (val.length < len) {
            val = '0' + val
        }
        return val
    }

    function getTimeZoneAbbr (date) {
        date = date || new Date();

        if (!(date instanceof Date)) {
            date = new Date(date);
        }

        var timeString = date.toTimeString();
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

        return abbr;
    }

    var DateUtils = _.extend({
        MILLI: {
            second: MS_IN_SEC,
            minute: MS_IN_MIN,
            hour: MS_IN_HOUR,
            day: MS_IN_DAY,
            week: MS_IN_WEEK
        },

        format: function (date, format, utc) {
            var token = /d{1,4}|M{1,4}|YY(?:YY)?|([HhmsAa])\1?|[oS]|[zZ]/g;
            var df = DateUtils.formats;

            date = date || new Date();

            if (!(date instanceof Date)) {
                date = new Date(date)
            }

            if (isNaN(date)) {
                throw TypeError('Invalid date')
            }

            format = String(df[format] || format || df['default']);

            var abbr = getTimeZoneAbbr(date);

            var prf = utc ? 'getUTC' : 'get';
            var d = date[prf + 'Date']();
            var D = date[prf + 'Day']();
            var M = date[prf + 'Month']();
            var Y = date[prf + 'FullYear']();
            var H = date[prf + 'Hours']();
            var m = date[prf + 'Minutes']();
            var s = date[prf + 'Seconds']();
            var o = utc ? 0 : date.getTimezoneOffset();
            var z = abbr.toLowerCase();
            var Z = abbr.toUpperCase();
            var flags = {
                d: d,
                dd: pad(d),
                ddd: DateUtils.i18n.dayNames[D],
                dddd: DateUtils.i18n.dayNames[D + 7],
                M: M + 1,
                MM: pad(M + 1),
                MMM: DateUtils.i18n.monthNames[M],
                MMMM: DateUtils.i18n.monthNames[M + 12],
                YY: String(Y).slice(2),
                YYYY: Y,
                h: H % 12 || 12,
                hh: pad(H % 12 || 12),
                H: H,
                HH: pad(H),
                m: m,
                mm: pad(m),
                s: s,
                ss: pad(s),
                a: H < 12 ? 'a' : 'p',
                aa: H < 12 ? 'am' : 'pm',
                A: H < 12 ? 'A' : 'P',
                AA: H < 12 ? 'AM' : 'PM',
                o: (o > 0 ? '-' : '+') + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
                S: ['th', 'st', 'nd', 'rd'][d % 10 > 3 ? 0 : (d % 100 - d % 10 !== 10) * d % 10],
                z: '(' + z + ')',
                Z: '(' + Z + ')'
            };

            return format.replace(token, function (t) {
                return (t in flags) ? flags[t] : t.slice(1, t.length - 1)
            })
        },

        formats: {
            'default': 'YYYY/MM/dd',
            shortDate: 'MM/dd',
            mediumDate: 'YY/MM/dd',
            time12: 'hh:mm AA',
            americanShortDate: 'M/d/YYYY',
            americanMediumDate: 'MM/dd/YYYY',
            separatedYearDate: 'MMM dd, YYYY',
            separatedYearDateTime: 'MMM dd, YYYY hh:mm AA',
            longDate: 'YYYY/MM/dd',
            longDateTime: 'YYYY/MM/dd HH:mm:ss',
            longDateMediumTime12: 'MM/dd/YYYY hh:mm AA',
            longDateMediumTime12TimeZone: 'MM/dd/YYYY hh:mm AA Z',
            isoDateTime: 'YYYY-MM-ddTHH:mm:ss.000'
        },

        i18n: {
            dayNames: [
                'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat',
                'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'
            ],
            monthNames: [
                'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
                'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'
            ]
        },

        diffDates: function (date1, date2, unit) {
            var diffInMs = Math.abs(+date1 - +date2);
            if (!unit) {
                return diffInMs
            }

            unit = unit.toLowerCase();
            var unitInMs = DateUtils.MILLI[unit];
            if (unitInMs) {
                return diffInMs / unitInMs
            } else if (unit === 'month' || unit === 'year') {
                var date1Less = date1 < date2;
                var start = date1Less ? date1 : date2;
                var end = date1Less ? date2 : date1;
                var years = DateUtils.year(end) - DateUtils.year(start);
                var months = years * 12 + DateUtils.month(end) - DateUtils.month(start);
                var days = DateUtils.date(end) - DateUtils.date(start);
                if (days >= 0) {
                    months++
                }
                return (unit === 'month') ? months : months / 12
            } else {
                return diffInMs
            }
        },

        isTomorrow: function (date) {
            var d1 = DateUtils.startOf(new Date(), 'day');
            var d2 = DateUtils.startOf(date, 'day');
            return (DateUtils.diff(d1, d2, 'day') === 1)
        },

        isToday: function (date) {
            var d1 = DateUtils.startOf(new Date(), 'day');
            var d2 = DateUtils.startOf(date, 'day');
            return (DateUtils.diff(d1, d2, 'day') === 0)
        },

        isYesterday: function (date) {
            var d1 = DateUtils.startOf(new Date(), 'day');
            var d2 = DateUtils.startOf(date, 'day');
            return (DateUtils.diff(d2, d1, 'day') === 1)
        },

        getTimeZoneAbbr: getTimeZoneAbbr
    }, Dates);

    return {
        getProperty: function (o, s) {
            s = (s + '').replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
            s = s.replace(/^\./, ''); // strip a leading dot

            var a = s.split('.');

            for (var i = 0, n = a.length; i < n; ++i) {
                var k = a[i];

                if (k in o) o = o[k];
                else return;
            }
            return o;
        },

        extendProperty: function (target, prop, value) {
            prop = (prop + '').replace(/^\./, ''); // strip a leading dot
            prop = prop.split('.');

            if (target) {
                if (_.isArray(prop)) {
                    if (prop.length > 1) {
                        target[prop[0]] = this.extendProperty(target[prop[0]], prop.slice(1), value);
                    } else {
                        target = this.extendProperty(target, prop[0], value);
                    }
                } else {
                    target[prop] = value;
                }
            } else {
                target = {};
                if (_.isArray(prop)) {
                    var newValue = value;
                    for (var idx = prop.length - 1; idx >= 0; idx--) {
                        target = {};
                        target[prop[idx]] = newValue;
                        newValue = target;
                    }
                } else {
                    target[prop] = value;
                }
            }
            return target;
        },

        emptyPromise: function () {
            return new Promise(function (resolve) {
                resolve(null)
            });
        },

        capitalize: function (str) {
            return str ? str.charAt(0).toUpperCase() + str.slice(1) : str;
        },

        isEmptyStr: function (str) {
            return typeof str === 'string' && str.trim() === '';
        },

        isDateValid: function (date) {
            // An invalid date object returns NaN for getTime()
            // and NaN is the only object not strictly equal to itself
            return date.getTime() === date.getTime();
        },

        getTimeZoneAbbr: getTimeZoneAbbr,

        isNullOrUndefined: function (v) {
            return v === null || v === void 0;
        },

        isNull: function (v) {
            return v === null;
        },

        allAreNull: function () {
            return _.every(arguments, function (o) {
                return this.isNull(o)
            });
        },

        anyIsNull: function () {
            return _.any(arguments, function (o) {
                return this.isNull(o)
            });
        },

        isNotNull: function (v) {
            return !this.isNull(v);
        },

        isNaN: function (v) {
            return _.isNumber(v) && v !== +v;
        },

        isUndefined: function (v) {
            return v === void 0;
        },

        isEmpty: function (v) {
            if (this.isNull(v) || this.isNaN(v) || this.isUndefined(v)) return true;
            if (_.isObject(v)) return _.isEmpty(v);
            return false
        },

        isNotEmpty: function (v) {
            return !this.isEmpty(v);
        },

        camel: function (s) {
            return s.replace(/(?:^\w|[A-Z]|\b\w)/g, function (letter, index) {
                return index === 0 ? letter.toLowerCase() : letter.toUpperCase();
            }).replace(/\s+/g, "");
        },

        defer: function (delay) {
            delay = _.isNumber(delay) ? delay : 0;

            return new Promise(function (resolve) {
                setTimeout(resolve, delay);
            });
        },

        interpolate: function (s) {
            for (var i = 0; i < (arguments.length <= 1 ? 0 : arguments.length - 1); i++) {
                s = s.replace(
                    new RegExp("\\$".concat(i), "g"),
                    i + 1 < 1 || arguments.length <= i + 1 ? undefined : arguments[i + 1]
                );
            }

            return s;
        },

        convertStringToBoolean : function  (s) {
            return s ? s === 'true' || s !== 'false' : false;
        },

        Date: DateUtils
    }
});