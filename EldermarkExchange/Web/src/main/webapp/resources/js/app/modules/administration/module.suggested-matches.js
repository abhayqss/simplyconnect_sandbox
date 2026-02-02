ExchangeApp.modules.AdministrationSuggestedMatches = (function () {
    // history of loaded segments
    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'administration/suggested-matches'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: true
    };
    var holderContentId = "suggestedMatchesTabContent";
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('administrationSuggestedMatches');
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

    // ===============================================================================================================

    function _initPatientsList() {
        _getFragment().widgets.patientsList = _grid({
            tableId: "patientsListSuggested",
            searchFormId: "patientFilterSuggested",
            totalDisplayRows: 10,
            sort: false,

            fetchServerData: function (url, requestMethod, fnCallback) {
                // serialize filter form data and send it in request query

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

                ajaxOptions.data = $searchForm.serialize();

                var responseTotalElements = 0;
                var responseContent = [];
                var promiseChain = $.when();

                promiseChain
                    .then (function() {
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
                        var hasProbablyMatchedResidents = responseContent.filter(function(e) { return e.probablyMatchedId; }).length > 0;

                        if (hasProbablyMatchedResidents) {
                            $grid.find(":not(.mergedResident)").each(function () {
                                $(this).removeClass("even");
                                $(this).removeClass("odd");
                                $(this).addClass("odd");
                            });
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
                                    rowToAppend.append($('<td colspan="100" align="right"><a class="showMoreDetailsLabel">Show More Details</a></td>'));

                                    rowToAppend.removeClass("even");
                                    rowToAppend.removeClass("odd");
                                    rowToAppend.addClass("even");
                                    rowToAppend.addClass("showMoreDetails");

                                    /* handler for Show More Details link */
                                    rowToAppend.attr('data-maybe-matched-id', $(this).attr('data-maybe-matched-id'));
                                    rowToAppend.on('click', function() {
                                        var selected = $(this).attr('data-maybe-matched-id');
                                        if (selected) {
                                            showSuggestedMatchesStep2(selected);
                                        }
                                        return false;
                                    });
                                }
                            });
                        }
                    });
            },

            callbacks: {
                rowCallback: function (row, data) {
                    var $row = $(row);
                    if (data !== null && data['probablyMatchedId'] && data['probablyMatchedId'] !== 'null') {
                        $row.addClass('mergedResident');
                        $row.attr('data-id', data['id']);
                        $row.attr('data-maybe-matched-id', data['probablyMatchedId']);
                    }
                },
                errorCallback: function (error) {
                    alert(error.responseText);
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.patientsList.api().ajax.reload();
    }

    function _initPatientsComparedList(mainPatientId) {
        _getFragment().widgets.patientComparedList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "suggestedMatchesPatientsCompared",
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

                            return false;
                        });

                        return $link.prop('outerHTML');
                    }
                }
            },

            fetchServerData: function (url, requestMethod, fnCallback) {
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

                ajaxOptions.url += ajaxOptions.url.indexOf('?') > -1 ? "&" : "?";
                ajaxOptions.url += "id=" + mainPatientId;

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
                        responseTotalElements += response.totalElements;
                        responseContent = response.content.map(function (item) {
                            // construct selfUrl

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

                            item.select = false;

                            return item;
                        });

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
                        // UI tweaks for #suggestedMatchesPatientsCompared datatable
                        var $table = $("#suggestedMatchesPatientsCompared");
                        $table.find(".even").each(function() {
                            $(this).removeClass("even");
                            $(this).addClass("odd");
                        });
                        $table.find("thead").hide();
                        $table.find("tbody tr td.checkbox-col").each(function() {
                            $(this).append(" <label>Matching</label>");
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
                                $table.parent().width("84%");
                            }
                        }

                        // disable “RESOLVE” button
                        var $resolveBtn = mdlUtils.find(_getFragment().$html, '', "#suggestedMatchesResolve");
                        $resolveBtn.prop('disabled', true);

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
                ignoreRowClicks: true,
                callback: function () {
                    // watch for change of the checkboxes, and disable “RESOLVE” button until user has selected at least 2 matching records
                    var getSelectedRowsCount = function () {
                        return _getFragment().widgets.patientComparedList.api().rows(".selected").data().length;
                    };
                    var $resolveBtn = mdlUtils.find(_getFragment().$html, '', "#suggestedMatchesResolve");
                    $resolveBtn.prop('disabled', getSelectedRowsCount() < 2);
                }
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
            url: 'administration/suggested-matches/resolve',
            type: 'POST',
            data: JSON.stringify(data),
            beforeSend: function(xhr){
                mdlUtils.csrf(xhr);
            }
        }).success(function () {
            _getFragment().widgets.patientsList.api().ajax.reload();

            // go to the first step
            $find("#patientsComparedContent").hide();
            $find("#patientsContent").show();
        }).fail(function (response) {
            mdlUtils.onAjaxError(response, function () {
                bootbox.alert(response.responseText);
            });
        });
    }

    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

    function showSuggestedMatchesConfirmation(selected, unselected) {
        $.ajax({
            url: 'administration/suggested-matches/confirmation?matching=' + selected + '&mismatching=' + unselected,
            type: 'GET'
        }).success(function (data) {
            if (data) {
                var $container = mdlUtils.find(_getFragment().$html, '', '#suggestedMatchesConfirmationContainer');
                $container.empty();
                $container.append(data);

                var $suggestedMatchesConfirmationModal = $container.find('#suggestedMatchesConfirmationModal');
                /* handler for RESOLVE button */
                $container.find('#suggestedMatchesConfirmationResolveBtn').on('click', function () {
                    $suggestedMatchesConfirmationModal.modal('hide');
                    _resolveMatches(selected, unselected);
                });
                $suggestedMatchesConfirmationModal.modal({backdrop: 'static'});
                $suggestedMatchesConfirmationModal.on('hidden.bs.modal', function () {
                    $(this).remove();
                });
            }
        }).fail(function (response) {
            bootbox.alert(response.responseText);
        });
    }

    function showSuggestedMatchesStep2(mainPatientId, data) {
        $.ajax("administration/suggested-matches/step2").success(data, function (data) {
            if ($find("#patientsContent").is(":hidden")) {
                return; // belated response from server -> do nothing
            }

            $find("#patientsContent").hide();
            $find("#patientsComparedContent").hide();

            $find("#patientsComparedContent").empty();

            $find("#patientsComparedContent").append(data);
            $find("#patientsComparedContent").show();

            /* handler for back link */
            mdlUtils.find(_getFragment().$html, '', ".backToSuggestedMatches").on('click', function () {
                $find("#patientsComparedContent").hide();
                $find("#patientsContent").show();
            });

            /* handler for CANCEL button */
            mdlUtils.find(_getFragment().$html, '', "#suggestedMatchesCancel").on('click', function () {
                $find("#patientsComparedContent").hide();
                $find("#patientsContent").show();
            });

            /* handler for RESOLVE button */
            mdlUtils.find(_getFragment().$html, '', "#suggestedMatchesResolve").on('click', function () {
                var selected = _getFragment().widgets.patientComparedList.api().rows(".selected").data().toArray().map(function (row) {
                    return +row.id;
                });
                var unselected = _getFragment().widgets.patientComparedList.api().rows(":not(.selected)").data().toArray().map(function (row) {
                    return +row.id;
                });
                if (selected && selected.length > 1) {
                    showSuggestedMatchesConfirmation(selected, unselected);
                }
                return false;
            });

            // init table
            _initPatientsComparedList(mainPatientId);
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
                    _initPatientsList();
                    this.setPatients();
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
            $.each($('#administration').find('.nav li'), function() {
                $(this).removeClass('active');
            });
            $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
            // render content
            _getFragmentHolder().show();
            $('#content').removeClass('loading');
        },

        setPatients: function () {
            $find("#patientSearchSuggested").on('click', function () {
                _getFragment().widgets.patientsList.api().ajax.reload();
                return false;
            });

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