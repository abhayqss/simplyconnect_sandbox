/**
 * Created by stsiushkevich on 10.09.18.
 */

var Grid = (function($){

    function breakWord(string, max, length) {
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

    function formatFileSize(size) {
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

    function Grid () {
        Widget.apply(this, arguments)
    }

    Grid.prototype = Object.create(Widget.prototype);
    Grid.prototype.constructor = Grid;

    Grid.prototype.getDefaultProps = function () {
        return {
            hasHeader: true,
            hasFooter: true,

            columns: [],

            order: [],

            noDataText: 'No results found',

            hasSorting: true,
            hasPagination: true,

            numbersLength: 5,
            totalDisplayRows: 25,

            getExtraAjaxData: function () {
                return null
            }
        };
    };

    Grid.prototype.getApi = function () {
        return this.$element.dataTable().api();
    };

    Grid.prototype.reload = function () {
        this.getApi().ajax.reload();
    };

    Grid.prototype.isEmpty = function () {
        return this.getApi().data().length === 0;
    };

    Grid.prototype.componentDidMount = function () {

        var me = this;
        var props = this.props;

        this.$element = $('[cmp-id="'+ this.$$id +'"]');
        this.$pagination = this.$element.find('[class *= "_paginate"]');


        /*init columns*/
        var columns = [];

        var checkboxColNames = [];
        var fakeColNames = [];

        var aoColumnDefs = [];

        var colSorting = [];

        $.each(props.columns, function (i, o) {
            var column = {mDataProp: o.name, sName: o.name};

            column.mRender = function (data) {
                return breakWord(data, 15, 10);
            };

            switch (o.format) {
                case 'fake' :
                {
                    fakeColNames.push(o.name);
                    aoColumnDefs.push({orderable: false, aTargets: [i], sName: o.name});
                    break;
                }
                case 'ssn' :
                {
                    column.mRender = function(data) {
                        if (!data) {
                            return "";
                        }
                        return data.substring(0, 3) + "-" + data.substring(3, 5) + "-" + data.substring(5, 9);
                    };
                    break;
                }
                case 'checkbox':
                {
                    column.mRender = function (data, type, row) {
                        if (type === 'display') {
                            return '<input type="checkbox" class="editor-active">';
                        }
                        return data;
                    };
                    column.className = "checkbox-col";
                    column.sWidth = "10px";
                    checkboxColNames.push(o.name);
                    aoColumnDefs.push({orderable: false, aTargets: [i], sName: o.name});
                }
                    break;
                case 'filesize':
                {
                    column.mRender = formatFileSize;
                }
                    break;
                case 'date':
                {
                    column.mRender = function(data) {
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
            }

            if (o.render) column.mRender = o.render;

            if (o.order) colSorting.push([i, o.order]);

            columns.push(column);
        });

        /*init table*/

        $.fn.DataTable.ext.pager.numbers_length = props.numbersLength;

        var pathContext = location.pathname.match(/\/\w+/);
        var context = (pathContext) ? pathContext[0] : "";

        var dataSource = me.getDataSource();

        var config = {
            sAjaxSource: dataSource.ajax.url,

            destroy: true,

            sAjaxDataProp: 'content',
            aoColumns: columns,
            aoColumnDefs: aoColumnDefs,
            iDisplayLength: props.totalDisplayRows,
            iDisplayStart: 0,
            bServerSide: true,
            bProcessing: true,
            iDeferLoading: props.isLoadingDeferred,

            sort: props.hasSorting,
            paginate: props.hasPagination,
            order: props.order,

            language: {
                paginate: {
                    next: "<div/>",
                    previous: "<div/>"
                },
                sZeroRecords: props.noDataText,
                sProcessing: "<img src='" + context + "/resources/images/ajax-loader.gif'>",
                sInfoEmpty: '',
                sInfoFiltered: '',
                sInfo: ''
            },
            store: new ItemsStore(),
            fnServerData: function (sSource, aoData, fnCallback) {
                me.$pagination.hide();

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

                var requestMethod = dataSource.ajax.method;

                if (props.fetchServerData) {
                    props.fetchServerData(url, requestMethod, fnCallback);
                    return;
                }

                var ajaxProps = {
                    dataType: 'json',
                    type: requestMethod,
                    url: url,
                    data: props.getExtraAjaxData(),
                    beforeSend: function(xhr){
                        var token = $("meta[name='_csrf']").attr("content");
                        var header = $("meta[name='_csrf_header']").attr("content");
                        xhr.setRequestHeader(header,token);
                    },
                    success: function (response) {
                        response.iTotalDisplayRecords = response.totalElements;

                        /*init checkbox columns*/
                        $.each(response.content, function (i, rowData) {
                            $.each(checkboxColNames, function (j, name) {
                                rowData[name] = false;
                            });

                            $.each(fakeColNames, function (j, name) {
                                rowData[name] = false;
                            });
                        });

                        if (props.callbacks && props.callbacks.successCallback) {
                            props.callbacks.successCallback(response);
                        }

                        fnCallback(response);
                    },
                    error: function (response) {
                        ExchangeApp.utils.module.onAjaxError(response, function () {
                            response.iTotalDisplayRecords = 0;
                            response.content = [];

                            if (props.callbacks && props.callbacks.errorCallback) {
                                props.callbacks.errorCallback(response);
                            }

                            fnCallback(response);
                        });
                    }
                };

                /*clear the table*/
                me.$element.find('tbody tr').remove();

                $.ajax(ajaxProps);
            }
        };

        $.extend(true, config.language, props.language);
        $.extend(true, config, props.scroll);
        $.extend(true, config, props.callbacks);

        config.drawCallback = function (settings) {

            if (settings.aoData.length == 0) {
                $(settings.nTHead).hide();
                $(settings.nTFoot).hide();
                me.$pagination.hide();
            } else {
                $(settings.nTHead).show();
                $(settings.nTFoot).show();
                me.$pagination.show();
            }

            /*row selection feature*/
            if (settings.selectable) {
                me.$element.find('input:checkbox').styler();

                /* mark items from store as selected*/
                if (config.store.items.length > 0) {
                    me.$element.find('tbody tr').each(function (index, tr) {
                        var Api = me.getApi();
                        var data = Api.row(index).data();

                        if (data) {
                            if (config.store.hasItem(data.id)) {
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
                me.$element.find('tbody').off('click').on('click', clickableAreaSelector, function (e) {
                    var $tr = $(e.currentTarget).parent();

                    var Api = me.getApi();

                    var index = Api.row($tr).index();
                    var data = Api.row(index).data();

                    if (data === undefined) return;

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
                        settings.selectable.callback.call(me, $tr, data, index);
                    }
                });

                if (settings.selectable.ignoreRowClicks) {
                    me.$element.find('tbody').off('click');
                }

                /* checkboxes selection handling*/
                me.$element.find('tbody tr').off('change').on('change', 'input:checkbox', function (e) {
                    var $tr = $(e.target).closest('tr');

                    var Api = me.getApi();

                    var index = Api.row($tr).index();
                    var data = Api.row(index).data();

                    var addOrRemove = $(e.target).is(':checked');

                    updateStore(data, addOrRemove);

                    if (settings.selectable.type === 'multiple') {
                        updateRow($tr, addOrRemove);
                    }

                    if (settings.selectable.boxForStore) {
                        updateBox(data, data[settings.selectable.boxForStore.dataColumn], addOrRemove)
                    }

                    if (settings.selectable.callback) {
                        settings.selectable.callback.call(me, $tr, data, index);
                    }
                });

                function updateStore(data, addOrRemove) {
                    if (addOrRemove) {
                        config.store.addItem(data.id);
                    } else {
                        config.store.removeItem(data.id);
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
                        config.store.removeItem(id);

                        $itemBox.remove();

                        me.$element.find('tbody tr').each(function (index, tr) {
                            var $tr = $(tr);

                            var rowData = me.getApi().row(index).data();

                            if (rowData.id === id) {
                                updateCheckbox($tr.find('input:checkbox'), false);

                                if (settings.selectable.type === 'multiple') {
                                    updateRow($tr, false);
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
                settings.callbacks.drawCallback.call(me, settings);
            }
        };

        var grid = me.$element.dataTable(config);

        if (colSorting.length) {
            grid.api().order(colSorting);
        }

        return grid;
    };

    Grid.prototype.renderColTitleRow= function () {
        return {
            '<>': 'tr', 'html': [
                $.map(this.props.columns, function (o) {
                    return {'<>': 'th', 'text': o.title}
                })
            ]
        }
    };

    Grid.prototype.getDataSource= function () {
        return {}
    };

    Grid.prototype.render = function () {
        return {
            '<>': 'table',
            'id': this.props.id,
            'class': 'display',
            'cellspacing': 0,
            'width': '100%',
            'html': [
                this.props.hasHeader ? {
                    '<>': 'thead',
                    'html': [this.renderColTitleRow()]
                } : null,
                this.props.hasFooter ? {
                    '<>': 'tfoot',
                    'html': [this.renderColTitleRow()]
                } : null
            ]
        }
    };

    return Grid
})($);