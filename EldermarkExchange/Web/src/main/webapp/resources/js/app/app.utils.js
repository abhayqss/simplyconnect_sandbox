
$.isNumeric()


ExchangeApp.utils.common = (function ($) {
    return {
        /*
         Gets nested properties from JavaScript object
         *
         * examples:
         * var prop = getProperty(obj, "prop1.nestedProp1");
         * var prop = getProperty(obj, "prop1.[0].nestedProp1");
         * */

        getProperty: function (o, path) {
            path = path.replace(/^\./, ''); // strip a leading dot
            path = (path + '').replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties

            var a = path.split('.');

            for (var i = 0, n = a.length; i < n; ++i) {
                var k = a[i];

                if (k in o) o = o[k];
                else return
            }
            return o
        }
    }
})($);

ExchangeApp.utils.auth = (function ($) {
    return {
        getToken: function () {
            return $("meta[name='_csrf']").attr("content");
        }
    }
})($);

ExchangeApp.utils.module = (function () {
    return {
        clone: function (target) {
            return $.extend(true, {}, target);
        },

        rand: function () {
            return Math.random() * 10000000000000000000;
        },

        randomize: function ($target, random, clone) {
            if (!random) return false;

            var $fragment = clone ? $target.clone() : $target;
            if ($fragment && $fragment.length) {

                $fragment.find('[id]').each(function (index, elem) {
                    var id = $(elem).attr('id');
                    if (id) {
                        if (id.indexOf(random) < 0) {
                            $(elem).attr('id', id + random);
                        }

                        $fragment.find('[href="#' + id + '"]').each(function (i, e) {
                            var href = $(e).attr('href');
                            if (href.indexOf(random) < 0) {
                                $(e).attr('href', href + random);
                            }
                        });
                    }
                });

                $fragment.find('[data-target]').each(function (i, e) {
                    var dataTarget = $(e).attr('data-target');
                    if (dataTarget && dataTarget.indexOf(random) < 0) {
                        $(e).attr('data-target', dataTarget + random);
                    }
                });
            }
            return $fragment;
        },

        find: function ($fragment, random, selector) {
            if (typeof selector === 'string') {
                var ids = selector.match(/#[\w\\.]+/g) || [];
                $.each(ids, function (i, e) {
                    selector = selector.replace(e, e + random);
                });
            }
            return $fragment.find(selector);
        },

        stringifyUrl: function (url, excludeOptions) {
            var template = url.template;
            if ((!excludeOptions || !excludeOptions.variables) && url.variables) {
                for (var key in url.variables) {
                    template = url.template.replace('{' + key + '}', url.variables[key]);
                }
            }

            if ((!excludeOptions || !excludeOptions.params) && url.params) {
                var splitter = '?';
                for (var key in url.params) {
                    template += splitter + key + '=' + url.params[key];
                    splitter = '&';
                }
            }

            return template;
        },

        getUrl: function (template, varsStr, paramsStr) {
            var url = {};
            url.template = template;

            if (varsStr) {
                url.variables = {};
                $.each(varsStr.split('&'), function (index, varible) {
                    //key-value
                    var kv = varible.split('=');
                    url.variables[kv[0]] = kv[1];
                });
            }

            if (paramsStr) {
                url.params = {};
                $.each(paramsStr.split('&'), function (index, param) {
                    //key-value
                    var kv = param.split('=');
                    url.params[kv[0]] = kv[1];
                });
            }
            return url;
        },

        csrf: function (xhr) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            xhr.setRequestHeader(header,token);
        },

        noneOptionHandler: function (e, clickedIndex, newValue, oldValue) {
            var option = e.currentTarget[clickedIndex];
            var lastOption = e.currentTarget[e.currentTarget.options.length - 1];
            if (newValue) {
                if (option && option.label.toLowerCase().startsWith('none') && "-1" === option.value) {
                    $(this).selectpicker('deselectAll');
                    $(option).prop('selected', true);
                    $(this).selectpicker('refresh');
                } else {
                    if (lastOption && lastOption.label.toLowerCase().startsWith('none') && $(lastOption).prop('selected') && "-1" === lastOption.value) {
                        $(lastOption).prop('selected', false);
                        $(this).selectpicker('refresh');
                    }
                }
            }

            $.each($(this), function(index, select) {
                $.each(select, function(optionIndex, option) {
                    if ($(option).val() != -1 && $(option).text().toLowerCase() === 'none'){
                        $(option).parent().prev().find('ul').children().eq(optionIndex).css('display', 'none');
                    }
                })
            });
        },

        allOptionHandler: function (e, clickedIndex, newValue, oldValue) {
            var option = e.currentTarget[clickedIndex];
            var zeroOption = e.currentTarget[0];
            var firstOption = e.currentTarget[1];
            if (newValue) {
                if (option && (option.label.toLowerCase() === 'all' || "0" === option.value)) {
                    $(this).selectpicker('deselectAll');
                    $(option).prop('selected', true);
                    $(this).selectpicker('refresh');
                } else {
                    if (zeroOption && (zeroOption.label.toLowerCase() === 'all' || "0" === zeroOption.value) && $(zeroOption).prop('selected')) {
                        $(zeroOption).prop('selected', false);
                        $(this).selectpicker('refresh');
                    }
                    if (firstOption && (firstOption.label.toLowerCase() === 'all' || "0" === firstOption.value) && $(firstOption).prop('selected')) {
                        $(firstOption).prop('selected', false);
                        $(this).selectpicker('refresh');
                    }
                }
            }

            $.each($(this), function(index, select) {
                $.each(select, function(optionIndex, option) {
                    if ($(option).val() != -1 && $(option).text().toLowerCase() === 'none'){
                        $(option).parent().prev().find('ul').children().eq(optionIndex).css('display', 'none');
                    }
                })
            });
        },

        findAjaxUrls: function ($fragment, random) {
            var urls = [];
            var self = this;
            self.find($fragment, random, '[data-ajax-load="true"]').each(function () {
                var tmpl = $(this).attr('data-ajax-url-tmpl');
                var varsStr = $(this).attr('data-ajax-url-vars');
                var paramsStr = $(this).attr('data-ajax-url-params');

                if (tmpl) {
                    var url = self.getUrl(tmpl, varsStr, paramsStr);
                    urls.push(url);
                }
            });
            return urls;
        },

        onAjaxError: function (error, errorHandler) {
            var statusCode = {
                // Set up a global AJAX error handler to handle the 401 unauthorized responses.
                401: function ajaxSessionTimeout() {
                    var pathContext = location.pathname.match(/\/\w+/);
                    var context = (pathContext) ? pathContext[0] : "";
                    window.location.href = context + '/login?invalid-session=true';
                },
                // Failed assertion of the expected hashKey value and value of hashKey parameter provided in request.
                422: function invalidHashKeyError() {

                    var router = ExchangeApp.routers.ModuleRouter;
                    var mdlUtils = ExchangeApp.utils.module;

                    var url = mdlUtils.getUrl('patient-search', null, 'hashKey=error');
                    router.route(url);
                },
                403: function ajaxForbidden() {
                    var pathContext = location.pathname.match(/\/\w+/);
                    var context = (pathContext) ? pathContext[0] : "";
                    window.location.href = context + '/login?invalid-session=true';
                }
            };

            if (statusCode[error.status]) {
                statusCode[error.status]();
            } else if (errorHandler) {
                errorHandler(error);
            }
        },

        showLoader: function() {
            $(".loader").show();
            // $("#content").fadeTo(0, 0.5);
        },

        hideLoader: function() {
            $(".loader").hide();
            // $("#content").fadeTo(0, 1.0);
        },

        initAjaxLoader: function($container, callbacks) {
            var $loaderDiv = $('<div style="z-index: 9999" class="modal-ajax-loader">');

            var loaderWillBeShown = callbacks && callbacks.loaderWillBeShown;
            var loaderShown = callbacks && callbacks.loaderShown;
            var loaderWillBeHidden = callbacks && callbacks.loaderWillBeHidden;
            var loaderHidden = callbacks && callbacks.loaderHidden;

            return {
                show: function () {
                    loaderWillBeShown && loaderWillBeShown();
                    $container.prepend($loaderDiv);
                    loaderShown && loaderShown();
                },
                hide: function () {
                    loaderWillBeHidden && loaderWillBeHidden();
                    $loaderDiv.remove();
                    loaderHidden && loaderHidden();
                },
                setLoaderHiddenCallback: function (callback) {
                    loaderHidden = callback;
                }
            }
        }
    }
})();

ExchangeApp.utils.wgt = (function () {
    function _find($fragment, random, selector) {
        var ids = selector.match(/#[\w\\.]+/g) || [];
        $.each(ids, function (i, e) {
            selector = selector.replace(e, e + random);
        });

        return $fragment.find(selector);
    }

    function _breakWord(string, max, length) {
        var result = '';
        if (string && string.length > max) {
            var lines = string.split('\n');
            $.each(lines, function (index, line) {
                var words = line.split(' ');
                $.each(words, function (index, word) {
                    if (word.length > max) {
                        var summary = '';
                        var reg = new RegExp(".{1," + length + "}", "g");
                        var parts = word.match(reg);
                        for (var i = 0; i < parts.length; i++) {
                            summary += parts[i] + '<wbr>';
                        }
                        result += result.length ? ' ' + summary : summary;
                    } else {
                        result += result.length ? ' ' + word : word;
                    }
                });
                result += '\n';
            });
            return result.length ? result.slice(0, -1) : string;
        }
        return string;
    }

    function _formatFileSize(size) {
        if (!size) return '';
        var mb = 1024 * 1024, kb = 1024;
        if (size > mb) {
            return (size / mb).toFixed(2) + ' MB';
        } else if (size > kb) {
            return (size / kb).toFixed(2) + ' KB';
        } else {
            return size + ' B';
        }
    }

    return {
        grid: function ($fragment, random, settings) {
            var tableId = settings.tableId;
            var $grid = _find($fragment, random, "#" + tableId);
            var $grid_paginate = _find($fragment, random, "#" + tableId + "_paginate");

            if (!$grid.length) return;

            /*init columns*/
            var columns = [];
            var colIds = $grid.attr('data-col-ids').split(',');
            var colFormats = $grid.attr('data-col-formats').split(',');
            var checkboxColIds = [];
            var fakeColIds = [];

            var aoColumnDefs = [];
            var colSorting = [];
            $.each(colIds, function (i, e) {
                var colFormat = $.trim(colFormats[i]);
                var colId = $.trim(e);
                var col = {mDataProp: colId, sName: colId};

                col.mRender = function (data) {
                    return _breakWord(data, 15, 10);
                };

                switch (colFormat) {
                    case 'fake' :
                    {
                        fakeColIds.push(colId);
                        aoColumnDefs.push({orderable: false, aTargets: [i], sName: colId});
                        break;
                    }
                    case 'ssn' :
                    {
                        col.mRender = function(data) {
                            if (!data) {
                                return "";
                            }
                            return data.substring(0, 3) + "-" + data.substring(3, 5) + "-" + data.substring(5, 9);
                        };
                        break;
                    }
                    case 'checkbox':
                    {
                        col.mRender = function (data, type, row) {
                            if (type === 'display') {
                                return '<input type="checkbox" class="editor-active">';
                            }
                            return data;
                        };
                        col.className = "checkbox-col";
                        col.sWidth = "10px";
                        checkboxColIds.push(colId);
                        aoColumnDefs.push({orderable: false, aTargets: [i], sName: colId});
                    }
                        break;
                    case 'filesize':
                    {
                        col.mRender = _formatFileSize;
                    }
                        break;
                    case 'date':
                    {
                        col.mRender = function(data) {
                            if (!data) {
                                return "";
                            } else {
                                var date = new Date(data);
                                var month = date.getMonth()<9 ? '0'+(date.getMonth()+1) : (date.getMonth()+1);
                                var day = date.getDate()<10 ? '0'+date.getDate() : date.getDate();
                                return month + '/' + day + '/' +  date.getFullYear();
                            }
                        }
                    }
                        break;
                    case 'localDateTime':
                    {
                        col.mRender = function(datetime) {
                            if (!datetime) {
                                return "";
                            } else {
                                var formatPattern = 'MM/DD/YYYY hh:mm A Z';
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
                                var result = moment(datetime).format(formatPattern) + ' ' + abbr + ' ' + moment(datetime).format('Z');
                                return result;
                            }
                        }
                    }
                        break;
                    case 'localDate':
                    {
                        col.mRender = function(datetime) {
                            if (!datetime) {
                                return "";
                            } else {
                                var formatPattern = 'MM/DD/YYYY';
                                var result = moment(datetime).format(formatPattern);
                                return result;
                            }
                        }
                    }
                        break;
                    case 'custom':
                    {
                        col.mRender = settings.colSettings[colId].customRender;
                    }
                        break;
                }

                if (settings.colSettings && colId in settings.colSettings) {
                    $.extend(col, settings.colSettings[colId]);

                    var order = settings.colSettings[colId].order;
                    if (order) {
                        colSorting.push([i, order]);
                    }
                }
                columns.push(col);
            });

            /*init table*/
            var totalDisplayRows = settings.totalDisplayRows ? settings.totalDisplayRows : 7;
            var enableSorting = settings.sort !== undefined ? settings.sort : true;
            var enablePagination = settings.paginate !== undefined ? settings.paginate : true;

            $.fn.DataTable.ext.pager.numbers_length
                = settings.numbersLength ? settings.numbersLength : 5;

            var pathContext = location.pathname.match(/\/\w+/);
            var context = (pathContext) ? pathContext[0] : "";
            var tblOptions = {
                sAjaxSource: $grid.attr('data-url'),

                destroy:true,

                sAjaxDataProp: 'content',
                aoColumns: columns,
                aoColumnDefs: aoColumnDefs,
                iDisplayLength: totalDisplayRows,
                iDisplayStart: 0,
                bServerSide: true,
                bProcessing: true,
                sort: enableSorting,
                paginate: enablePagination,
                order: settings.order ? settings.order : [],
                language: {
                    paginate: {
                        next: "<div/>",
                        previous: "<div/>"
                    },
                    sZeroRecords: 'No results found.',
                    sProcessing: "<img src='" + context + "/resources/images/ajax-loader.gif'>",
                    sInfoEmpty: '',
                    sInfoFiltered: '',
                    sInfo: ''
                },
                store: new ItemsStore(),
                fnServerData: function (sSource, aoData, fnCallback) {

                    $grid_paginate.hide();

                    //extract name/value pairs into a simpler map for use later
                    var paramMap = {};
                    for (var i = 0; i < aoData.length; i++) {
                        paramMap[aoData[i].name] = aoData[i].value;
                    }

                    //page calculations
                    var pageSize = paramMap.iDisplayLength;
                    var start = paramMap.iDisplayStart;
                    var pageNum = (start === 0) ? start : (start / pageSize); // pageNum is 0 based

                    // extract sort information
                    var sortCol = [paramMap.iSortCol_0, paramMap.iSortCol_1, paramMap.iSortCol_2, paramMap.iSortCol_3, paramMap.iSortCol_4, paramMap.iSortCol_5, paramMap.iSortCol_6, paramMap.iSortCol_7];
                    var sortDir = [paramMap.sSortDir_0, paramMap.sSortDir_1, paramMap.sSortDir_2, paramMap.sSortDir_3, paramMap.sSortDir_4, paramMap.sSortDir_5, paramMap.sSortDir_6, paramMap.sSortDir_7];
                    var sortName = [
                        paramMap['mDataProp_' + sortCol[0]],
                        paramMap['mDataProp_' + sortCol[1]],
                        paramMap['mDataProp_' + sortCol[2]],
                        paramMap['mDataProp_' + sortCol[3]],
                        paramMap['mDataProp_' + sortCol[4]],
                        paramMap['mDataProp_' + sortCol[5]],
                        paramMap['mDataProp_' + sortCol[6]],
                        paramMap['mDataProp_' + sortCol[7]]
                    ];

                    //if we are searching by name, override the url and add the name parameter
                    var url = sSource;

                    if (pageSize !== -1) {
                        url += url.indexOf('?') > -1 ? "&" : "?";
                        url += "page=" + pageNum + "&size=" + pageSize;
                    }

                    for (var i = 0, len = sortName.length; i < len; i += 1) {
                        if (sortName[i]) {
                            url += url.indexOf('?') > -1 ? "&" : "?";
                            url += "sort=" + sortName[i];

                            if (sortDir[i]) {
                                url += "," + sortDir[i];
                            }
                        }
                    }

                    var requestMethod = $grid.attr('data-request-method');
                    if (!requestMethod) requestMethod='POST';

                    if (settings.fetchServerData) {
                        settings.fetchServerData(url, requestMethod, fnCallback);
                        return;
                    }

                    var ajaxOptions = {
                        dataType: 'json',
                        type: requestMethod,
                        url: url,
                        beforeSend: function(xhr){
                            var token = $("meta[name='_csrf']").attr("content");
                            var header = $("meta[name='_csrf_header']").attr("content");
                            xhr.setRequestHeader(header,token);
                        },
                        success: function (response) {
                            response.iTotalDisplayRecords = response.totalElements;

                            /*init checkbox columns*/
                            $.each(response.content, function (i, rowData) {
                                $.each(checkboxColIds, function (j, colId) {
                                    rowData[colId] = false;
                                });
                                $.each(fakeColIds, function (j, colId) {
                                    rowData[colId] = false;
                                });
                            });

                            if (settings.callbacks && settings.callbacks.successCallback) {
                                settings.callbacks.successCallback(response);
                            }

                            fnCallback(response);
                        },
                        error: function (response) {
                            ExchangeApp.utils.module.onAjaxError(response, function () {
                                response.iTotalDisplayRecords = 0;
                                response.content = [];

                                if (settings.callbacks && settings.callbacks.errorCallback) {
                                    settings.callbacks.errorCallback(response);
                                }

                                fnCallback(response);
                            });
                        }
                    };

                    /*clear the table*/
                    $grid.find('tbody tr').remove();

                    var $searchForm = _find($fragment, random, "#" + settings.searchFormId);
                    if ($searchForm.length > 0) {
                        if ($searchForm.valid()) {
                            /*clear the table*/
                            ajaxOptions.data = $searchForm.serialize();
                            $.ajax(ajaxOptions);
                        } else {
                            fnCallback({
                                iTotalDisplayRecords: 0,
                                content: []
                            });
                        }
                    } else {
                        $.ajax(ajaxOptions);
                    }
                }
            };

            $.extend(true, tblOptions.language, settings.language);
            $.extend(true, tblOptions, settings.scroll);
            $.extend(true, tblOptions, settings.callbacks);

            tblOptions.drawCallback = function (clbSettings) {

                if (clbSettings.aoData.length == 0) {
                    $(clbSettings.nTHead).hide();
                    $(clbSettings.nTFoot).hide();
                    $grid_paginate.hide();
                } else {
                    $(clbSettings.nTHead).show();
                    $(clbSettings.nTFoot).show();
                    $grid_paginate.show();
                }

                /*row selection feature*/
                if (settings.selectable) {
                    $grid.find('input:checkbox').styler();

                    /* mark items from store as selected*/
                    if (tblOptions.store.items.length > 0) {
                        $grid.find('tbody tr').each(function (index, tr) {
                            var tableApi = $grid.dataTable().api();

                            var data = tableApi.row(index).data();
                            if (data) {
                                if (tblOptions.store.hasItem(data.id)) {
                                    var $tr = $(tr);

                                    if (settings.selectable.type === 'multiple') {
                                        updateRow($tr, true);
                                    }

                                    updateCheckbox($tr, true);
                                }
                            }
                        });
                    }

                    /* rows selection handling*/
                    var clickableAreaSelector = 'tr td';
                    /*
                    if (settings.selectable.ignoreRowClicks) {
                        clickableAreaSelector = 'tr td:has(input:checkbox)';
                    }*/
                    $grid.find('tbody').off('click').on('click', clickableAreaSelector, function (e) {
                        var tableApi = $grid.dataTable().api();

                        var $tr = $(e.currentTarget).parent();

                        var index = tableApi.row($tr).index();
                        var data = tableApi.row(index).data();

                        if (data === undefined)
                            return;

                        $tr.toggleClass('selected');

                        if (settings.selectable.type === 'multiple') {
                            var addOrRemove = $tr.hasClass('selected');

                            updateStore(data, addOrRemove);

                            updateCheckbox($tr, addOrRemove);

                            if (settings.selectable.boxForStore) {
                                updateBox(data.id, data[settings.selectable.boxForStore.dataColumn], addOrRemove)
                            }
                        }

                        if (settings.selectable.type === 'single') {
                            $tr.siblings().each(function (i, tr) {
                                $(tr).removeClass('selected');
                            });
                        }

                        if (settings.selectable.callback) {
                            settings.selectable.callback.call(self, $tr, data, index);
                        }
                    });

                    if (settings.selectable.ignoreRowClicks) {
                        $grid.find('tbody').off('click');
                    }

                    /* checkboxes selection handling*/
                    $grid.find('tbody tr').off('change').on('change', 'input:checkbox', function (e) {
                        var tableApi = $grid.dataTable().api();

                        var $tr = $(e.target).closest('tr');

                        var index = tableApi.row($tr).index();
                        var data = tableApi.row(index).data();

                        var addOrRemove = $(e.target).is(':checked');

                        updateStore(data, addOrRemove);

                        if (settings.selectable.type === 'multiple') {
                            updateRow($tr, addOrRemove);
                        }

                        if (settings.selectable.boxForStore) {
                            updateBox(data, data[settings.selectable.boxForStore.dataColumn], addOrRemove)
                        }

                        if (settings.selectable.callback) {
                            settings.selectable.callback.call(self, $tr, data, index);
                        }
                    });

                    function updateStore(data, addOrRemove) {
                        if (addOrRemove) {
                            tblOptions.store.addItem(data.id);
                        } else {
                            tblOptions.store.removeItem(data.id);
                        }
                    }

                    function updateCheckbox($tr, addOrRemove) {
                        var $checkbox = $tr.find('input:checkbox');
                        if ($checkbox) {
                            $checkbox.prop('checked', addOrRemove);
                            $checkbox.trigger('refresh');
                        }
                    }

                    function updateRow($tr, addOrRemove) {
                        $tr.toggleClass('selected', addOrRemove);
                    }

                    function updateBox(id, dataToShow, addOrRemove) {
                        if (addOrRemove) {
                            addToBox(id, dataToShow);
                        } else {
                            removeFromBox(id);
                        }
                    }

                    function addToBox(id, dataToShow) {
                        var $itemBox = $('<div/>');
                        $itemBox.append($('<span>' + dataToShow + '</span>'));

                        $itemBox.attr('id', 'chosed-row-' + id);
                        $itemBox.addClass('chosed-row');

                        /* box items selection handling */

                        var $closeBtn = $('<button></button>');

                        var $icon = $('<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>');
                        $closeBtn.append($icon);

                        $closeBtn.on('click', function () {
                            tblOptions.store.removeItem(id);

                            $itemBox.remove();

                            $grid.find('tbody tr').each(function (index, tr) {
                                var $tr = $(tr);

                                var tableApi = $grid.dataTable().api();
                                var rowData = tableApi.row(index).data();

                                if (rowData.id === id) {
                                    updateCheckbox($tr.find('input:checkbox'), false);

                                    if (settings.selectable.type === 'multiple') {
                                        updateRow($tr, false);li
                                    }
                                }
                            })
                        });
                        $itemBox.append($closeBtn);

                        $('.chose-rows-pnl').append($itemBox);
                    }

                    function removeFromBox(id) {
                        $('.chose-rows-pnl').find('#chosed-row-' + id).remove();
                    }
                }

                /* custom draw callback*/
                if (settings.callbacks && settings.callbacks.drawCallback) {
                    settings.callbacks.drawCallback.call(self, clbSettings);
                }
            };

            var grid = $grid.dataTable(tblOptions);

            if (colSorting.length) {
                grid.api().order(colSorting);
            }

            return grid;
        },

        wizard: function ($fragment, random, settings) {
            return _find($fragment, random, '#' + settings.wizardId).bootstrapWizard(settings)[0];
        },

        alert: function ($fragment, random, options) {
            switch (options.action) {
                case 'add':
                    var $place = options.placeSelector ? _find($fragment, random, options.placeSelector) : options.place;

                    var $alert = $('<div>');
                    $alert.addClass('ldr-ui-layout alert alert-dismissible');

                    var type = options.type ? options.type : 'alert-warning';
                    $alert.addClass(type);

                    if (options.closable) {
                        if (options.closable.btn) {
                            var $closeBtn = $('<button><span>&times;</span></button>');
                            $closeBtn.addClass('close').attr('type', 'button');
                            $closeBtn.on('click', function () {
                                $(this).parent('.alert').remove();
                            });
                            $alert.append($closeBtn);
                        }
                        if (options.closable.timer) {
                            window.setTimeout(function () {
                                if (options.closable.callback) {
                                    options.closable.callback.call($alert[0]);
                                }
                                $alert.remove();
                            }, options.closable.timer);
                        }
                    }

                    if (options.alertId) $alert.attr('id', options.alertId);

                    $alert.append(options.message);

                    $place.find('.alert.alert-dismissible').remove();

                    if (options.poz == 'bottom') {
                        $place.append($alert);
                    } else {
                        $place.prepend($alert);
                    }
                    return $alert;
                case 'remove':
                    $(options.alertSelector).remove();
            }
        },

        Validation: function (config) {
            config.ignore = config.ignore || $.validator.defaults.ignore;

            var _showErrorTooltip = function (self, errorMap, errorList) {
                for (var formElementName in errorMap) {
                    var $elem = $(self.currentForm).find("[name='" + formElementName + "']");
                    var errMessage = errorMap[formElementName];
                    var $target = $elem;
                    var position;

                    var placement = $elem.attr('data-tooltip-position') || position;

                    if ($elem.attr('type') == 'radio') {
                        $target = $(self.currentForm).find('[name=' + formElementName + ']').parents('label');
                    }

                    if ($elem.attr('type') == 'checkbox') {
                        $target = $(self.currentForm).find('[name=' + formElementName + ']').parents('label');
                    }

                    if ($elem.attr('type') == 'file') {
                        $target = $elem.siblings('.bootstrap-filestyle');
                        placement = 'top';
                    }

                    if ($elem.attr('id') == 'subjective') {
                        placement = 'top';
                    }

                    $target.parents('.form-group').addClass('has-warning');

                    var isValidated = $target.attr('data-validated');
                    if (!isValidated) {
                        $target.attr('data-validated', true);
                        $target.attr('data-toggle', 'tooltip');
                        $target.attr('data-placement', placement);
                        $target.attr('title', errMessage);
                        $target.tooltip();
                    }

                    if ($target.attr('data-original-title') != errMessage) {
                        $target.tooltip('hide')
                            .attr('data-original-title', errMessage)
                            .tooltip('fixTitle')
                            .tooltip('show');
                    }
                }

                self.defaultShowErrors();
                $('label[class="error"]').remove();
            };

            var _recheck = function (elem, event) {
                var $elem = $(elem), $target = $elem;

                if ($elem.attr('type') === 'radio') {
                    var name = $elem.attr('name');
                    $target = $('[name=' + name + ']').parents('label');
                }

                // bootstrap-select
                if ($elem.attr('type') === 'button') {
                    var id = $elem.attr('data-id');
                    $target = $elem.siblings('#' + id);
                }

                if ($elem.attr('type') === 'file') {
                    $target = $elem.siblings('.bootstrap-filestyle');
                }

                if ($elem.valid()) {
                    $elem.parents('.form-group').removeClass('has-warning');
                    $target.tooltip('destroy');
                    $target.removeAttr('data-validated');
                }
            };

            $.fn.resetForm = function() {
                this.find('.form-group').removeClass('has-warning');
            };

            return {
                ignore: config.ignore,
                rules: config.rules,
                messages: config.messages,
                focusInvalid: !config.scrollToFirstInvalid,
                invalidHandler: !config.scrollToFirstInvalid ? undefined : function (event, validator) {
                    if (!validator.numberOfInvalids()) return;
                    var $element;
                    for (var i = 0; i < validator.errorList.length; i += 1) {
                        var $el = $(validator.errorList[i].element);
                        if ($el.is(":visible")) {
                            $element = $el;
                            break;
                        }
                    }
                    if (!$element) return;

                    if (!config.container.scrollInProgress) {
                        config.container.scrollInProgress = true;
                        config.container.scrollTo($element, config.scrollingDuration || 500, function () {
                            config.container.scrollInProgress = false;
                        });
                    }
                },
                showErrors: function (errorMap, errorList) {
                    _showErrorTooltip(this, errorMap, errorList);
                },
                onfocusout: function (elem, event) {
                    _recheck(elem, event);
                },
                onkeyup: function (elem, event) {
                    _recheck(elem, event);
                },

                onchange: false
            }
        },

        lifeSearch: function($fragment, random, options) {
            var selectSelector = options.select;
            var $select = _find($fragment, random, selectSelector);
            var $dropdown = $select.closest('.dropdown');
            var $searchInput = _find($fragment, random, options.searchInput);
            var $searchIcon = _find($dropdown, random, '.glyphicon-search');
            var $deleteIcon = _find($dropdown, random, '.delete-text');
            var delay = options.delay || 500

            var _showDeleteIcon = function () {
                $deleteIcon.css({display: ''});
                $searchIcon.css({display: 'none'});
            };

            var _showSearchIcon = function () {
                $deleteIcon.css({display: 'none'});
                $searchIcon.css({display: ''});
            };

            //influencing 'open' class as there is no way to show or hide dropdown via bootstrap api.
            var _openDropdown = function () {
                $dropdown.addClass('open');
            };

            var _closeDropdown = function () {
                $dropdown.removeClass('open');
            };

            var _setSelectValue = function(value, displayName) {
                $select
                    .find('option')
                    .remove()
                    .end()
                    .append('<option value="' + value + '">'+displayName+'</option>')
                    .val(value);
                $select.trigger('change');
                $searchInput.val(displayName);
                $searchInput.trigger('change');
            };

            $dropdown.on('show.bs.dropdown', function(e) {
                //prevent dropdown from being shown when search input is empty
                if ($(e.relatedTarget).val() === '' || $(e.relatedTarget).val() === null || $(e.relatedTarget).val() === undefined) {
                    return false;
                }
            });

            $dropdown.on('hide.bs.dropdown', function(e) {
                //clear search if nothing was selected
                if ($select.val() === '' || $select.val() === null || $select.val() === undefined) {
                    $searchInput.val('')
                }
            });

            //prevent dropdown from being closed on clicking inside
            $dropdown.click(function (e) {
                e.stopPropagation();
                e.preventDefault();
            });

            //ensure that dropdown is closed on clicking outside
            $('body').click(function () {
                $dropdown.removeClass('open');
                $dropdown.trigger('hide.bs.dropdown');
            });

            var lastChangeTime = new Date().getTime();

            $searchInput.on('keyup', function() {
                //todo arrow and enter keys
                if ($(this).val() === '' || $(this).val() === null || $(this).val() === undefined) {
                    //close dropdown if user deleted all the text in search input
                    _closeDropdown();
                    _showSearchIcon();
                } else {
                    var NOW = new Date().getTime();
                    var canSearch = (NOW - lastChangeTime) > delay;

                    lastChangeTime = NOW;

                    //show dropdown if user has entered some text
                    _openDropdown();
                    _showDeleteIcon();

                    $select.val('');
                    $select.trigger('change');

                    if (canSearch && options.notEmptyKeyUpCallBack !== undefined) {
                        var me = this;
                        var interval = setInterval(function () {
                            if (((new Date().getTime()) - lastChangeTime) > delay) {
                                options.notEmptyKeyUpCallBack.call(this, $(me).val());
                                clearInterval(interval);
                            }
                        }, 100);
                    }
                }
            });

            $searchInput.on('click', function() {
                if ($(this).val() !== '' && $(this).val() !== null && $(this).val() !== undefined) {
                    _openDropdown();
                    if (options.searchInputNotEmptyClickCallback)
                        options.searchInputNotEmptyClickCallback.call(this, $(this).val());
                }
            });

            _showSearchIcon();

            $deleteIcon.on('click', function() {
                _setSelectValue('','');
                _closeDropdown();
                _showSearchIcon();
            });

            if ($select.val() !== '' && $select.val() !== null && $select.val() !== undefined) {
                $searchInput.val($select.find(':selected').text());
            }

            return {
                open: function() {
                    _openDropdown();
                },
                close: function () {
                    _closeDropdown();
                },
                setSelectValue: function(value, displayName) {
                    _setSelectValue(value, displayName)
                }
            }
        }
    };
})();

/* Add custom validation methods*/
(function () {
    $.validator.addMethod('regexp', function (value, element, param) {
        var trimLeft = /^\s+/,
            trimRight = /\s+$/;
        value = value.toString().replace(trimLeft, "").replace(trimRight, "");
        return param.test(value);
    }, 'This value doesn\'t match the acceptable pattern.');

    $.validator.addMethod('dateExp', function (value, element, param) {
        var trimLeft = /^\s+/,
            trimRight = /\s+$/;
        value = value.toString().replace(trimLeft, "").replace(trimRight, "");

        if (!value) return true;

        var date = new Date(value);
        if (!isNaN(date.getTime())) {
            return param.test(value);
        }

        return false;
    }, 'This value doesn\'t match the acceptable pattern.');

    $.validator.addMethod("lengthGreaterThan", function (value, element, params) {
        return value.length > params;
    });

    $.validator.addMethod("requiredIf", function (value, element, params) {
        if (params.conditionFunction()) {
            return !(value==='');
        } else {
            return true;
        }
    });

    $.validator.addMethod("requiredIfNot", function (value, element, params) {
        return !params.conditionFunction() ? !(value === '') : true;
    });

    $.validator.addMethod("lengthLessThan", function (value, element, params) {
        return value.length < params;
    });

    $.validator.addMethod("lengthEqual", function (value, element, params) {
        if (!value) return true;

        return !params || value.length == params;
    }, 'Field must have {0} digits');

    $.validator.addMethod("ssn", function (value, element) {
        if (!value) return true;
        return this.optional(element) || value.match(/^\d{3}-?\d{2}-?\d{4}$/);
    }, 'Please specify a valid SSN');

    $.validator.addMethod("integer", function (value, element) {
        return this.optional(element) || /^-?\d+$/.test(value);
    }, "A positive or negative non-decimal number please");

    $.validator.addMethod("positiveInteger", function (value, element) {
        return this.optional(element) || /^\d+$/.test(value);
    }, "A positive or negative non-decimal number please");

    $.validator.addMethod('filesize', function (value, element, param) {
        return this.optional(element) || (element.files[0].size <= param)
    });

    $.validator.addMethod("phone", function (phone_number, element) {
        return this.optional(element) || phone_number.match(/^\+?[0-9]{10,15}$/);
    }, "A phone format is invalid: only digits allowed (no spaces or dashes), '+' may be leading symbol.");

    $.validator.addMethod("stateUS", function (value, element, options) {
            var isDefault = typeof options === "undefined",
                caseSensitive = ( isDefault || typeof options.caseSensitive === "undefined" ) ? false : options.caseSensitive,
                includeTerritories = ( isDefault || typeof options.includeTerritories === "undefined" ) ? false : options.includeTerritories,
                includeMilitary = ( isDefault || typeof options.includeMilitary === "undefined" ) ? false : options.includeMilitary,
                regex;

            if (!includeTerritories && !includeMilitary) {
                regex = "^(A[KLRZ]|C[AOT]|D[CE]|FL|GA|HI|I[ADLN]|K[SY]|LA|M[ADEINOST]|N[CDEHJMVY]|O[HKR]|PA|RI|S[CD]|T[NX]|UT|V[AT]|W[AIVY])$";
            } else if (includeTerritories && includeMilitary) {
                regex = "^(A[AEKLPRSZ]|C[AOT]|D[CE]|FL|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEINOPST]|N[CDEHJMVY]|O[HKR]|P[AR]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY])$";
            } else if (includeTerritories) {
                regex = "^(A[KLRSZ]|C[AOT]|D[CE]|FL|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEINOPST]|N[CDEHJMVY]|O[HKR]|P[AR]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY])$";
            } else {
                regex = "^(A[AEKLPRZ]|C[AOT]|D[CE]|FL|GA|HI|I[ADLN]|K[SY]|LA|M[ADEINOST]|N[CDEHJMVY]|O[HKR]|PA|RI|S[CD]|T[NX]|UT|V[AT]|W[AIVY])$";
            }

            regex = caseSensitive ? new RegExp(regex) : new RegExp(regex, "i");
            return this.optional(element) || regex.test(value);
        },
        "Please specify a valid state");

    $.validator.addMethod("emails", function (value, element, options) {
            var regexp = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/igm;
            return this.optional(element) || regexp.test(value);
        },
        "Please specify a valid Email");

    $.validator.addMethod("password1", function (value, element) {
            var regExp = new RegExp("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,255}$");
            // return this.optional(element) || /^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,255}$/.test(value);
            return this.optional(element) || regExp.test(value);
        },
        "Password must be at least 8 characters, contain at least 1 UPPERCASE, 1 lowercase, and one numeric digit ");

    $.validator.addMethod('fileIsPicture', function (value, element, param) {
        return this.optional(element) || (element.files[0].type == 'image/jpeg') || (element.files[0].type == 'image/gif')  || (element.files[0].type == 'image/png');
    });

    $.validator.addMethod("notEqual", function(value, element, param) {
        return this.optional(element) || value != $(param).val();
    }, "Please enter a different value");


    $.validator.addMethod('fileRatio', function (value, element, params) {
        var minRatio = params.min;
        var maxRatio = params.max;


        if ( this.optional( element ) ) {
            return true;
        }

        var previous = this.previousValue( element ),
            validator, data;

        if (!this.settings.messages[ element.name ] ) {
            this.settings.messages[ element.name ] = {};
        }
        previous.originalMessage = this.settings.messages[ element.name ].fileRatio;
        this.settings.messages[ element.name ].fileRatio = previous.message;


        if ( previous.old === value ) {
            return previous.valid;
        }

        previous.old = value;
        validator = this;
        this.startRequest( element );
        data = {};
        data[ element.name ] = value;

        var img = new Image();
        img.onload = function () {
            var width = this.width;
            var height = this.height;
            var ratio = width/height;
            console.log(width, height, ratio, params);
            var valid = (ratio>=params[0]) && (ratio<=params[1]);

            validator.settings.messages[ element.name ].fileRatio = previous.originalMessage;
            if ( valid ) {
                submitted = validator.formSubmitted;
                validator.prepareElement( element );
                validator.formSubmitted = submitted;
                validator.successList.push( element );
                delete validator.invalid[ element.name ];
                validator.showErrors();
            } else {
                errors = {};
                message = validator.defaultMessage( element, "fileRatio" );
                errors[ element.name ] = previous.message = $.isFunction( message ) ? message( value ) : message;
                validator.invalid[ element.name ] = true;
                validator.showErrors( errors );
            }
            previous.valid = valid;
            validator.stopRequest( element, valid );
        };

        img.src = window.URL.createObjectURL(element.files[0]);

        return "pending";
    });

    /* Add polyfills */
    if (!String.prototype.startsWith) {
        String.prototype.startsWith = function (searchString, position) {
            return this.substr(position || 0, searchString.length) === searchString;
        };
    }
})();
