ExchangeApp.modules.AdministrationManualMatching = (function () {
    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'administration/manual-matching'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: true
    };
    var holderContentId = "manualMatchingTabContent";
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('administrationManualMatching');
    var pModule;

    /*-------------utils----------------*/

    function _prepare() {
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _ajaxify($trgFragment, trgRandom) {
        var cFragment = _getFragment();

        if (!cFragment.ajaxMap) {
            cFragment.ajaxMap = {};
        }

        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        var urls = mdlUtils.findAjaxUrls($fragment, random);
        $.each(urls, function (i, url) {
            cFragment.ajaxMap[_stringifyUrl(url, {params: true})] = url;
        });

        mdlUtils.find($fragment, random, '[data-ajax-load="true"]').on('click', function () {
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);

            router.route(url);
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function () {
            var $trg = $($(this).attr('href'));
            if ($trg.length) {
                $('html, body').animate({scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }

    function _grid(options) {
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _getFragment() {
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolder() {
        return $('[id^="' + _getFragmentHolderId() + '"]');
    }

    function _getFragmentHolderId() {
        return holderContentId;
    }

    function _stringifyUrl(url, excludeOptions) {
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _randomize($trgFragment, trgRandom) {
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }

    $.fn.clearForm = function () {
        return this.each(function () {
            var tag = this.tagName.toLowerCase();
            if (tag === 'form') {
                return $(':input:not([disabled="disabled"], [type="hidden"], [name="mode"])', this).clearForm();
            }

            var type = this.type;
            if (type === 'text' || type === 'password' || tag === 'textarea') {
                this.value = '';
                $(this).trigger('refresh');
            } else if (tag === 'select') {
                this.selectedIndex = 0;
                $(this).trigger('change');
            } else if (type === 'checkbox' || type === 'radio') {
                this.checked = false;
                $(this).trigger('refresh');
            }
        });
    };

    $.fn.dataTableExt.oApi.fnSortNeutral = function (oSettings) {
        /* Remove any current sorting */
        oSettings.aaSorting = [];

        /* Sort display arrays so we get them in numerical order */
        oSettings.aiDisplay.sort(function (x, y) {
            return x - y;
        });
        oSettings.aiDisplayMaster.sort(function (x, y) {
            return x - y;
        });

        /* Redraw */
        oSettings.oApi._fnReDraw(oSettings);
    };

    function toggleSuggestedRecords(show) {
        var $table = $find("#patientsListManual");
        if (show) {
            $table.find(".mergedResident").show();
        } else {
            $table.find(".mergedResident").hide();
        }
    }

    var getRows = function (table, selector) {
        selector = selector || ".selected";
        return table.api().rows(selector).data().toArray();
    };

    var getRowsIds = function (table, selector) {
        return getRows(table, selector).map(function (row) {
            return +row.id;
        });
    };

    var getSelectedRowsCount = function (table) {
        if (!table)
            return 0;
        else
            return table.api().rows(".selected").data().length;
    };

    function switchNextButtonDisabledState() {
        var $nextBtn = $find("#manualMatchingNext");
        var count = getSelectedRowsCount(_getFragment().widgets.patientList);
        $nextBtn.prop('disabled', count < 2);
    }

    function _initPatientList() {
        _getFragment().widgets.patientList = _grid({
            tableId: "patientsListManual",
            searchFormId: "patientsFilterManual",
            totalDisplayRows: 25,
            colSettings: {
                select: {bSortable: false}
            },

            fetchServerData: function (url, requestMethod, fnCallback) {
                // serialize filter form data and send it in request body

                var ajaxOptions = {
                    dataType: 'json',
                    type: requestMethod,
                    url: url
                };

                var callCustomCallback = function(response, callbackToCall) {
                    if (this.callbacks && this.callbacks[callbackToCall]) {
                        this.callbacks[callbackToCall](response);
                    }
                };

                var $searchForm = $find("#" + this.searchFormId);
                var $grid = $find("#" + this.tableId);

                if ($searchForm.valid()) {
                    $find("#manualMatchingShowSuggestedRecordsLayout").addClass('hidden');

                    ajaxOptions.data = $searchForm.serialize();
                    if (ajaxOptions.data.indexOf("gender=") === -1) {
                        ajaxOptions.data += "&gender=";
                    }

                    var responseTotalElements = 0;
                    var responseContent = [];
                    var promiseChain = $.when();

                    promiseChain
                        .then (function() {
                            $grid.find("tbody").hide();
                            return $.ajax(ajaxOptions);
                        })
                        .then (function (response) {
                            responseTotalElements += response.totalElements;
                            responseContent = responseContent.concat(response.content);

                            callCustomCallback(response, 'successCallback');
                            return response;
                        })
                        .fail (function (response) {
                            ExchangeApp.utils.module.onAjaxError(response);
                            callCustomCallback(response, 'errorCallback');
                            return {
                                iTotalDisplayRecords: 0,
                                content: []
                            };
                        })
                        .always (function (response) {
                            response.iTotalDisplayRecords = responseTotalElements;
                            response.content = responseContent;

                            // render result
                            fnCallback(response);
                        })
                        .always(function () {
                            $grid.find("tbody").show();

                            var hasMergedResidents = responseContent.filter(function(e) { return e.mergedId; }).length > 0;

                            if (hasMergedResidents) {
                                var $showSuggestedCheck = $find("#manualMatchingShowSuggestedRecords");

                                // PD with option show merged patients
                                $showSuggestedCheck.change(function() {
                                    toggleSuggestedRecords(this.checked);
                                });
                                $find("#manualMatchingShowSuggestedRecordsLayout").removeClass('hidden');

                                var rowToAppend;
                                $grid.find(".mergedResident").each(function() {
                                    var nextRow = $(this).next();
                                    var prevRow = $(this).prev();

                                    if (!prevRow.is(".mergedResident")) {
                                        rowToAppend = prevRow.clone();
                                    }
                                    if (nextRow.length === 0 || !nextRow.is(".mergedResident")) {
                                        $(this).after(rowToAppend);
                                        rowToAppend.empty();

                                        rowToAppend.removeClass("even");
                                        rowToAppend.removeClass("odd");
                                        if ($(this).is(".odd")) {
                                            rowToAppend.addClass("even");
                                        } else {
                                            rowToAppend.addClass("odd");
                                        }

                                        rowToAppend.on('click', function() {
                                            // do nothing
                                            return false;
                                        });
                                        rowToAppend.hide();
                                    }
                                });
                                toggleSuggestedRecords($showSuggestedCheck.prop('checked'));
                            }

                            // disable “NEXT” button if there's less than 2 records selected
                            switchNextButtonDisabledState();
                        });
                } else {
                    fnCallback({
                        iTotalDisplayRecords: 0,
                        content: []
                    });
                }
            },

            selectable: {
                type: 'multiple',
                callback: function () {
                    // watch for change of the checkboxes, and disable “NEXT” button until user has selected at least 2 matching records
                    switchNextButtonDisabledState();
                }
            },

            callbacks: {
                rowCallback: function (row, data) {
                    var $row = $(row);
                    var $showSuggestedCheck = $find("#manualMatchingShowSuggestedRecords");
                    var areMergedResidentsVisible = $showSuggestedCheck.checked;

                    if (data !== null) {
                        if (data['mergedId'] && data['mergedId'] !== 'null') {
                            $row.addClass('mergedResident');
                            if (!areMergedResidentsVisible) {
                                $row.hide();
                            }
                        }
                    }
                },

                errorCallback: function (error) {
                    mdlUtils.onAjaxError(error, function(e) {
                        _alert({
                            action: 'add',
                            placeSelector: '.patientFilterBox .boxBody',
                            message: error.responseText,
                            closable: {
                                timer: 45000,
                                btn: true
                            }
                        });
                    });
                },

                footerCallback: function (tfoot, data, start, end, display) {
                    // display table footer only when result list has more than 20 records
                    var isHidden = data.length < 20;
                    if (isHidden) {
                        $(tfoot).hide();
                    } else {
                        $(tfoot).show();
                    }
                }
            }
        });
    }

    function _initPatientsComparedList(patients) {
        _getFragment().widgets.patientComparedList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "manualMatchingPatientsCompared",
            sort: false,
            paginate: false,

            colSettings: {
                selfUrl: {
                    customRender: function(data, type, row) {
                        if (!data) {
                            return '';
                        }

                        var $link = $("<a />", {
                            id : "phr-" + row.id,
                            class : "recordLink",
                            href : '#' + data.template + '?' + (data.parameters || ''),
                            text : "View Health Record",
                            "data-ajax-load" : "true",
                            "data-ajax-url-tmpl" : data.template,
                            "data-ajax-url-vars" : data.variables,
                            "data-ajax-url-params" : data.parameters
                        });

                        // bind event to document in order to target a dynamically added element
                        $(document).on('click', '#phr-' + row.id, function() {
                            var template = $(this).attr('data-ajax-url-tmpl');
                            var vars = $(this).attr('data-ajax-url-vars');
                            var params = $(this).attr('data-ajax-url-params');
                            var url = mdlUtils.getUrl(template, vars, params);
                            router.route(url);

                            // activate PHR tab
                            if (ExchangeApp.modules["PatientSearch"]) {
                                ExchangeApp.modules["PatientSearch"].loaded();
                            }

                            return false;
                        });

                        return $link.prop('outerHTML');
                    }
                },
                matchedAutomatically: {
                    customRender: function(data) {
                        if (!data) {
                            return 'No';
                        }

                        return 'Yes';
                    }
                }
            },

            fetchServerData: function (url, requestMethod, fnCallback) {
                // serialize filter form data and send it in request body

                var ajaxOptions = {
                    dataType: 'json',
                    type: requestMethod,
                    url: url
                };

                var callCustomCallback = function(response, callbackToCall) {
                    if (this.callbacks && this.callbacks[callbackToCall]) {
                        this.callbacks[callbackToCall](response);
                    }
                };

                // a data from previous request is used to initialize the matchedAutomatically property
                var patientIds = patients.map(function (p) {
                    return +p.id;
                });

                ajaxOptions.url += ajaxOptions.url.indexOf('?') > -1 ? "&" : "?";
                ajaxOptions.url += "ids=" + patientIds;

                var patientsToMatchesMap = patients.reduce(function (map, obj) {
                    map[obj.id] = {
                        id: +obj.mergedId,
                        matchedAutomatically: obj.matchedAutomatically
                    };
                    return map;
                }, {});
                var matchesToPatientsMap = patients.reduce(function (map, obj) {
                    if (obj.mergedId) {
                        if (map[obj.mergedId]) {
                            map[obj.mergedId].push(+obj.id);
                        } else {
                            map[obj.mergedId] = [+obj.id];
                        }
                    }
                    return map;
                }, {});

                var $grid = $("#" + this.tableId);
                var responseTotalElements = 0;
                var responseContent = [];
                var promiseChain = $.when();

                promiseChain
                    .then (function() {
                        $grid.find("tfoot").hide(); // footer and header cause enormous page stretching if not hidden
                        $grid.find("thead").hide();

                        return $.ajax(ajaxOptions);
                    })
                    .then (function (response) {
                        var responsePatientIds = response.content.map(function (item) {
                            return +item.id;
                        });
                        var responseContains = function (patientId) {
                            return patientId && responsePatientIds.filter(function (id) {
                                    return id === patientId;
                                }).length > 0;
                        };

                        responseTotalElements += response.totalElements;
                        var mainPatientIdsMatchedAutomatically = [];
                        responseContent = response.content.map(function (item) {
                            // construct selfUrl for "View Health Record" link
                            var tmpl = 'patient-info/{residentId}';
                            var params = "hashKey=" + item.hashKey + "&databaseId=" + item.databaseId;
                            var vars = 'residentId=' + item.id;

                            item.selfUrl = {
                                template: tmpl,
                                parameters: params,
                                variables: vars
                            };

                            var objUrl = mdlUtils.getUrl(tmpl, vars, params);
                            _getFragment().ajaxMap[_stringifyUrl(objUrl, {params: true})] = objUrl;

                            // initialize properties for "Records Matched Automatically" and "Choose Matching Records" rows
                            item.select = false;
                            var mainPatient = patientsToMatchesMap[item.id];
                            if (responseContains(mainPatient.id)) {
                                item.select = true;
                                item.matchedAutomatically = mainPatient.matchedAutomatically;
                            }
                            var secondaryPatientIds = matchesToPatientsMap[item.id];
                            for (var i in secondaryPatientIds) {
                                if (responseContains(secondaryPatientIds[i])) {
                                    item.select = true;
                                    break;
                                }
                            }
                            if (!item.select) {
                                item.matchedAutomatically = false;
                            } else if (item.matchedAutomatically) {
                                mainPatientIdsMatchedAutomatically.push(mainPatient.id);
                            }

                            return item;
                        });
                        for (var i in responseContent) {
                            if (mainPatientIdsMatchedAutomatically.indexOf(+responseContent[i].id) > -1) {
                                responseContent[i].matchedAutomatically = true;
                            }
                        }

                        callCustomCallback(response, 'successCallback');
                        return response;
                    })
                    .fail (function (response) {
                        ExchangeApp.utils.module.onAjaxError(response);
                        callCustomCallback(response, 'errorCallback');
                        return {
                            iTotalDisplayRecords: 0,
                            content: []
                        };
                    })
                    .always (function (response) {
                        response.iTotalDisplayRecords = responseTotalElements;
                        response.content = responseContent;

                        // render result
                        fnCallback(response);
                        return response;
                    })
                    .always(function (response) {
                        // UI tweaks for #manualMatchingPatientsCompared datatable
                        var $table = $("#manualMatchingPatientsCompared");
                        $table.find(".even").each(function() {
                            $(this).removeClass("even");
                            $(this).addClass("odd");
                        });
                        $table.find("thead").hide();
                        $table.find("tbody tr td.checkbox-col").each(function(i) {
                            $(this).append(" <label>Matching</label>");
                            if (response.content[i].select) {
                                var $chk = $(this).find("input[type='checkbox']");
                                $chk.prop('checked', true).trigger("change");
                            }
                        });
                        if (responseTotalElements) {
                            $table.find("tbody tr").each(function () {
                                $(this).css({
                                    // td padding is 10px
                                    "min-width": ($(this).width() + 10 * 2) + "px"
                                });
                                var colWidth = 100 / responseTotalElements;
                                $(this).width(colWidth + "%");
                            });
                            if (responseTotalElements > 6) {
                                $table.parent().width("82%");
                            }
                        }

                        // highlight rows wherein the values differ
                        $table.find("tbody tr:first td").each(function (i) {
                            var value = $(this).text();
                            var differentValues = false;
                            if (i === 0 || $(this).hasClass("checkbox-col")) {
                                // skip check for the first and last rows
                            } else {
                                $(this).parent().siblings().each(function () {
                                    var $td = $(this).children().eq(i);
                                    if (value !== $td.text()) {
                                        differentValues = true;
                                    }
                                });
                            }

                            $table.find("tbody tr").each(function () {
                                var $td = $(this).children().eq(i);
                                if (differentValues) {
                                    $td.addClass("mismatchingProperty");
                                } else {
                                    $td.addClass("matchingProperty");
                                }
                            })
                        });
                    });
            },

            selectable: {
                type: 'multiple',
                ignoreRowClicks: true
            },

            callbacks: {
                rowCallback: function (row, data, index) {},
                errorCallback: function (error) {
                    alert(error.responseText);
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
    }

    function _resolveMatches(matchingResidents, mismatchingResidents) {
        var data = $.extend({},
            matchingResidents.reduce(function(map, obj) {
                map[obj] = true;
                return map;
            }, {}),
            mismatchingResidents.reduce(function(map, obj) {
                map[obj] = false;
                return map;
            }, {}));

        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: 'administration/manual-matching/resolve',
            type: 'POST',
            data: JSON.stringify(data),
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            }
        }).success(function () {
            _getFragment().widgets.patientList.api().ajax.reload();

            // go to the first step
            $find("#patientsComparedContent").hide();
            $find("#patientsContent").show();
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                bootbox.alert(response.responseText);
            });

        });
    }

    function showManualMatchingConfirmation(selected, unselected) {
        $.ajax({
            url: 'administration/manual-matching/confirmation?matching=' + selected + '&mismatching=' + unselected,
            type: 'GET'
        }).success(function (data) {
            if (data) {
                var $container = mdlUtils.find(_getFragment().$html, '', '#manualMatchingConfirmationContainer');
                $container.empty();
                $container.append(data);

                var $manualMatchingConfirmationModal = $container.find('#manualMatchingConfirmationModal');
                /* handler for SAVE button */
                $container.find('#manualMatchingConfirmationSaveBtn').on('click', function () {
                    $manualMatchingConfirmationModal.modal('hide');
                    _resolveMatches(selected, unselected);
                });
                $manualMatchingConfirmationModal.modal({backdrop: 'static'});
                $manualMatchingConfirmationModal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });
            }
        }).fail(function (response) {
            bootbox.alert(response.responseText);
        });
    }

    function showManualMatchingStep2(patientIds, data) {
        $.ajax("administration/manual-matching/step2").success(data, function (data) {
            if ($find("#patientsContent").is(":hidden")) {
                return; // belated response from server -> do nothing
            }

            $find("#patientsContent").hide();
            $find("#patientsComparedContent").hide();

            $find("#patientsComparedContent").empty();

            $find("#patientsComparedContent").append(data);
            $find("#patientsComparedContent").show();

            /* handler for back link */
            mdlUtils.find(_getFragment().$html, '', ".backToManualMatching").on('click', function () {
                $find("#patientsComparedContent").hide();
                $find("#patientsContent").show();
            });

            /* handler for CANCEL button */
            mdlUtils.find(_getFragment().$html, '', "#manualMatchingCancel").on('click', function () {
                $find("#patientsComparedContent").hide();
                $find("#patientsContent").show();
            });

            /* handler for SAVE button */
            mdlUtils.find(_getFragment().$html, '', "#manualMatchingSave").on('click', function () {
                var selected = getRowsIds(_getFragment().widgets.patientComparedList, ".selected");
                var unselected = getRowsIds(_getFragment().widgets.patientComparedList, ":not(.selected)");
                showManualMatchingConfirmation(selected, unselected);
                return false;
            });

            // init table
            _initPatientsComparedList(patientIds);
        });
    }

    // ====================================
    return {
        init: function (url, parentModule) {
            cFragmentUrl = url;
            pModule = parentModule;

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var fragment = _getFragment();

                    fragment.widgets = {};
                    _initPatientList();
                    this.setFormEvents();
                    fragment.inited = true;
                    this.render();
                    this.show();
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;
        },

        getFragment: function (url) {
            return fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function (url) {
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        loadFragment: function (callbacks) {
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks: {
                    onFragmentLoaded: function (fragment) {
                        state.reloadNeeded = false;

                        var $fragment = $(fragment).find('#content').children();

                        var urlTmpl = _stringifyUrl(cFragmentUrl, {params: true});
                        fragmentsMap[urlTmpl] = {
                            $html: $fragment,
                            url: mdlUtils.clone(cFragmentUrl)
                        };

                        callbacks.onFragmentLoaded.apply(self);
                    },
                    onResourcesLoaded: function () {
                        callbacks.onResourcesLoaded.apply(self);
                    }
                }
            })
        },

        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        hide: function () {
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            // activate tab
            $.each($('#administration').find('.nav li'), function () {
                $(this).removeClass('active');
            });
            $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
            // render content
            _getFragmentHolder().show();
            $('#content').removeClass('loading');
        },

        setFormEvents: function () {
            $find(".datepicker").datepicker();
            $find("input[type='radio']").styler();
            $find("input[type='checkbox']").styler();

            var doPatientSearch = function() {
                _getFragment().widgets.patientList.api().ajax.reload();
            };

            /* handler for Enter key down */
            $find('#patientsFilterManual input').on('keydown', function(event) {
                if (event.keyCode === 13) {
                    event.preventDefault();
                    doPatientSearch();
                    return false;
                }
            });

            /* handler for SEARCH button */
            $find("#patientSearchManual").on('click', function () {
                _getFragment().widgets.patientList.api().ajax.reload();
                return false;
            });

            /* handler for CLEAR button */
            $find("#patientSearchManualClear").on('click', function () {
                // reset form
                $find("#patientsFilterManual").clearForm();
                var grid = _getFragment().widgets.patientList;
                // reset table sorting
                grid.fnSortNeutral();
                // clear table selection
                grid.fnSettings().oInit.store.removeAllItems();
                return false;
            });

            /* handler for NEXT button */
            $find("#manualMatchingNext").on('click', function () {
                var selectedRows = getRows(_getFragment().widgets.patientList, ".selected");
                if (selectedRows && selectedRows.length > 1) {
                    showManualMatchingStep2(selectedRows);
                }
                return false;
            });
            // disable “NEXT” button until user has selected at least 2 records
            switchNextButtonDisabledState();

            return false;
        },

        reloadNeeded: function () {
            return state.reloadNeeded;
        },
        getParentModule: function () {
            return pModule;
        }
    };

})();