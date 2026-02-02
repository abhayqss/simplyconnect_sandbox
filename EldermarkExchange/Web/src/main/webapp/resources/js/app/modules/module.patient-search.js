ExchangeApp.modules.PatientSearch = (function () {

    var fragmentsMap = {};

    var cFragmentUrl = {
        template: 'patient-search'
    };

    var state = {
        reloadNeeded: false
    };

    var router = ExchangeApp.routers.ModuleRouter;

    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('patientSearch');

    var patientSearchFormValidator;

    var isOddRow = false;
    var fromCC = false;

    //var showMergedPatients = false;

    /*-------------utils----------------*/

    function _prepare(){
        var cFragment = _getFragment();
        cFragment.random = mdlUtils.rand();
        _randomize();
        _ajaxify();
    }

    function _ajaxify($trgFragment, trgRandom){
        var cFragment = _getFragment();

        if(!cFragment.ajaxMap){
            cFragment.ajaxMap = {};
        }

        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        var urls = mdlUtils.findAjaxUrls($fragment, random);
        $.each(urls, function(i, url){
            cFragment.ajaxMap[_stringifyUrl(url, {params: true})] = url;
        });

        mdlUtils.find($fragment, random, '[data-ajax-load="true"]').on('click', function(){
            var template = $(this).attr('data-ajax-url-tmpl');
            var vars = $(this).attr('data-ajax-url-vars');
            var params = $(this).attr('data-ajax-url-params');

            var url = mdlUtils.getUrl(template, vars, params);
            router.route(url);
            return false;
        });

        mdlUtils.find($fragment, random, '[data-ajax-anchor="true"]').on('click', function(){
            var $trg = $($(this).attr('href'));
            if($trg.length){
                $('html, body').animate({ scrollTop: $trg.offset().top}, 200);
            }
            return false;
        });
    }

    function _grid(options){
        var fragment = _getFragment();
        return wgtUtils.grid(fragment.$html, fragment.random, options);
    }

    function _getFragment(){
        return fragmentsMap[_stringifyUrl(cFragmentUrl, {params: true})];
    }

    function _getFragmentHolderId(){
        return _stringifyUrl(cFragmentUrl, {params: true}).replace(/[/&\?=]/g, '_');
    }

    function _getFragmentHolder(){
        return $(document.getElementById(_getFragmentHolderId()));
    }

    function _stringifyUrl(url, excludeOptions){
        return mdlUtils.stringifyUrl(url, excludeOptions);
    }

    function $find(selector) {
        var fragment = _getFragment();
        return mdlUtils.find(fragment.$html, fragment.random, selector);
    }

    function _randomize($trgFragment, trgRandom){
        var cFragment = _getFragment();
        var $fragment = $trgFragment ? $trgFragment : cFragment.$html;
        var random = trgRandom ? trgRandom : cFragment.random;

        $fragment = mdlUtils.randomize($fragment, random, false);
        return $fragment;
    }


    /*-------------widgets----------------*/

    function _initFormStyler(){
        $find("input[type='radio']").styler();
        $find("input[type='checkbox']").styler();
        //$find('#moreDetails').collapse("show");
    }

    function _getSearchScopesDict() {
        return $find("input[name='searchScopes']").toArray().reduce(function(dict, current) {
            dict[current.value] = current.checked;
            return dict;
        }, {});
    }

    //function toggleMergedResidents(show) {
    //    var $table = $find('#patientList');
    //    if (show) {
    //        $table.find(".mergedResident, .showAggregatedRecord").show();
    //    } else {
    //        $table.find(".mergedResident, .showAggregatedRecord").hide();
    //    }
    //}

    function _initDatePicker(){
        $find("input.datepicker").datepicker();
        $find("input.datepicker").change(function() {
            var dt = new Date($(this).val());
            if ( !isNaN( dt.getTime() ) ) {
                $(this).datepicker('setValue', dt);
            }
            else {
                $(this).val('');
            }
        });

        $find("input[name='searchScopes']:checked").change(function (event) {
            var searchScopes = _getSearchScopesDict();

            //updateMandatoryFields
            var nwhinFieldsSelector = [
                'label[for="street"] span.mandatory-asterisk',
                'label[for="city"] span.mandatory-asterisk',
                'label[for="state"] span.mandatory-asterisk',
                'label[for="postalCode"] span.mandatory-asterisk'].join();

            if (searchScopes['NWHIN']) {
                $(nwhinFieldsSelector).show();
            } else {
                $(nwhinFieldsSelector).hide();
            }

            var eldermarkFieldsSelecor = '.ssn-xxxx span.mandatory-asterisk';
            if (searchScopes['ELDERMARK']) {
                $(eldermarkFieldsSelecor).show();
            } else {
                $(eldermarkFieldsSelecor).hide();
            }

            // enable ssn1, ss2
            var ssnInputsSelector = ['#ssn1', '#ssn2'].join();
            if (searchScopes['NWHIN']) {
                $find(ssnInputsSelector).prop('disabled', false);
            } else {
                $find(ssnInputsSelector).prop('disabled', true);
            }

            // show ssn help
            if (searchScopes['NWHIN'] && searchScopes['ELDERMARK']) {
                $('.ssn-help-msg').show();
            } else {
                $('.ssn-help-msg').hide();
            }

            patientSearchFormValidator.resetForm();
        });
    }

    function _initPatientSearchListWgt(){
        _getFragment().widgets.patientSearchList = _grid({
            tableId: "patientList",
            searchFormId: "patientFilterForm",
            totalDisplayRows: 25,
            colSettings: {
                residentNumber: {order: 'asc'},
                actions: {width: '75px'}
            },
            // order by Last Name, then by First Name
            order: [[1, 'asc'], [0, 'asc']],
            fetchServerData : function (url, requestMethod, fnCallback) {
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

                var $grid = $find("#" + this.tableId);
                var $searchForm = $find("#" + this.searchFormId);
                if ($searchForm.valid()) {

                   $find("#searchMergedCheckLayout").addClass('hidden');
                    var formDataDict = $searchForm.serializeArray().reduce(function(dict, current) {
                        if (current.name === 'searchScopes') {
                            dict[current.name] = dict.hasOwnProperty(current.name) ? dict[current.name].concat(current.value) : [current.value];
                        } else {
                            dict[current.name] = current.value;
                        }
                        return dict;
                    }, {});

                    var uniteSearchResults = function (localResults, nwhinResults) {
                        var indexNwhin = -1,
                            indexLocal,
                            lengthLocal = localResults.length,
                            lengthNwhin = nwhinResults.length;

                        while (++indexNwhin < lengthNwhin) {
                            var patientNwhin = nwhinResults[indexNwhin],
                                matches = false;

                            indexLocal = -1;
                            while (++indexLocal < lengthLocal) {
                                var patientLocal = localResults[indexLocal];
                                if (patientLocal.id === patientNwhin.id && patientLocal.hashKey === patientNwhin.hashKey) {
                                    matches = true;
                                    break;
                                }
                            }

                            if (matches) {
                                localResults[indexLocal] = patientNwhin;
                            } else {
                                localResults.push(patientNwhin);
                            }
                        }

                        return localResults;
                    };

                    var ssn1 = formDataDict.ssn1;
                    var ssn2 = formDataDict.ssn2;
                    var ssnVal;
                    if (typeof ssn1 !== 'undefined' && typeof ssn2 !== 'undefined') {
                        ssnVal = ssn1 + ssn2 + formDataDict.lastFourDigitsOfSsn;
                    } else {
                        ssnVal = formDataDict.lastFourDigitsOfSsn;
                    }
                    ajaxOptions.data = $searchForm.serialize();
                    ajaxOptions.data += '&ssn=' + ssnVal;

                    var combinedTotalElements = 0;
                    var combinedContent = [];

                    var promiseChain = $.when();

                    if (formDataDict.searchScopes.indexOf('ELDERMARK') > -1) {
                        promiseChain = promiseChain
                            .then (function() {

                                var ajaxOptionsClone = $.extend({}, ajaxOptions);
                                ajaxOptionsClone.url += '&searchScope=ELDERMARK';

                                return $.ajax(ajaxOptionsClone);

                            })
                            .then (function (response) {
                                combinedTotalElements += response.totalElements;
                                combinedContent = combinedContent.concat(response.content);

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
                                response.iTotalDisplayRecords = combinedTotalElements;
                                response.content = combinedContent;

                                // render result
                                fnCallback(response);
                            });
                    }

                    if (formDataDict.searchScopes.indexOf('NWHIN') > -1) {
                        promiseChain
                            .then(function () {
                                $grid.find('tbody').append($('<tr><td id="process_msg" colspan="7">Searching the patient in NwHIN...</td></tr>'));

                                var ajaxOptionsClone = $.extend({}, ajaxOptions);
                                ajaxOptionsClone.url += '&searchScope=NWHIN';

                                return $.ajax(ajaxOptionsClone);
                            })
                            .then(function (response) {
                                combinedTotalElements += response.totalElements;
                                combinedContent = uniteSearchResults(combinedContent, response.content);

                                callCustomCallback(response, 'successCallback');

                                return response;
                            })
                            .fail(function (response) {
                                ExchangeApp.utils.module.onAjaxError(response);

                                callCustomCallback(response, 'errorCallback');

                                return {
                                    iTotalDisplayRecords: 0,
                                    content: []
                                };
                            })
                            .always(function (response) {
                                response.iTotalDisplayRecords = combinedTotalElements;
                                response.content = combinedContent;

                                // render result
                                fnCallback(response);
                            });
                    }

                    promiseChain.always(function () {

                        var hasEldermarkResidents = combinedContent.filter(function(e) { return e.searchScope === 'ELDERMARK'; }).length > 0;
                        var hasMergedResidents = combinedContent.filter(function(e) { return e.mergedId; }).length > 0;

                        if (hasEldermarkResidents && hasMergedResidents) {
                            //var searchMergedCheck = $find("#searchMergedCheck");
                            //
                            //// PD with option show merged patients
                            //searchMergedCheck.change(function() {
                            //    toggleMergedResidents(this.checked);
                            //});
                            //$find("#searchMergedCheckLayout").removeClass('hidden');

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
                                    rowToAppend.append($('<td colspan="100" align="right"><a class="showAggregatedRecordLabel">Show Aggregated Record</a></td>'));
                                    rowToAppend.removeClass("even");
                                    rowToAppend.removeClass("odd");
                                    if ($(this).is(".odd")) {
                                        rowToAppend.addClass("even");
                                    } else {
                                        rowToAppend.addClass("odd");
                                    }
                                    rowToAppend.addClass("showAggregatedRecord");
                                    //rowToAppend.attr('valign', 'top');
                                    rowToAppend.on('click', rowToAppend, function (event) {
                                        var rowToAppend = event.data;
                                        var tmpl = rowToAppend.attr('data-ajax-url-tmpl');
                                        tmpl += '/aggregated';
                                        var vars = rowToAppend.attr('data-ajax-url-vars');
                                        var params = rowToAppend.attr('data-ajax-url-params');
                                        var url = mdlUtils.getUrl(tmpl, vars, params);
                                        router.route(url);
                                        return false;
                                    });
                                    rowToAppend.hide();
                                }
                            });
                            //toggleMergedResidents(searchMergedCheck.prop('checked'));
                        }
                    });
                } else {
                    fnCallback({
                        iTotalDisplayRecords: 0,
                        content: []
                    });
                }
            },
            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    var $trPopoverContent = $find('#patientPreviewTemplate').clone();
                    var popoverId = 'patientPreview-' + index;
                    $trPopoverContent.attr('id', popoverId);

                    if (data !== null) {
                        var $recordLnk = $trPopoverContent.find('.recordLink');

                        $recordLnk.attr('href', 'patient-info');

                        var url = $recordLnk.attr('href') + '/{residentId}';
                        var params = "hashKey=" + data.hashKey +"&databaseId=" + data.databaseId;
                        var variables = 'residentId=' + data.id;

                        $recordLnk.attr('href', '#' + url + '?' + params);

                        var objUrl = mdlUtils.getUrl(url, variables, params);
                        _getFragment().ajaxMap[_stringifyUrl(objUrl, {params: true})] = objUrl;

                        $recordLnk.attr('data-ajax-url-tmpl', url);
                        $recordLnk.attr('data-ajax-load', true);
                        $recordLnk.attr('data-ajax-url-params', params);
                        $recordLnk.attr('data-ajax-url-vars', variables);

                        $recordLnk.on('click', function(){
                            var tmpl = $(this).attr('data-ajax-url-tmpl');
                            var vars = $(this).attr('data-ajax-url-vars');
                            var params = $(this).attr('data-ajax-url-params');
                            var url = mdlUtils.getUrl(tmpl, vars, params);
                            router.route(url);
                            return false;
                        });

                        $row.attr('data-ajax-url-tmpl', url);
                        $row.attr('data-ajax-load', true);
                        $row.attr('data-ajax-url-vars', variables);
                        $row.attr('data-ajax-url-params', params);

                        $row.on('click', function(){
                            var tmpl = $(this).attr('data-ajax-url-tmpl');
                            var vars = $(this).attr('data-ajax-url-vars');
                            var params = $(this).attr('data-ajax-url-params');
                            var url = mdlUtils.getUrl(tmpl, vars, params);
                            router.route(url);
                            return false;
                        });

                        var patientInfo = ['fullName',
                            'dateOfBirth',
                            'genderDisplayName',
                            'ssn',
                            'streetAddress',
                            'cityStateAndPostalCode'];

                        for (var i = 0; i < patientInfo.length; i++) {
                            var key = patientInfo[i];
                            var value ='';
                            if (data[key] && data[key] != 'null') {
                                //if (key == 'ssn') {
                                //    value = data[key].substring(0, 3) + "-" + data[key].substring(3, 5) + "-" + data[key].substring(5, 9);
                                //}
                                //else {
                                    value = data[key];
                                //}
                            }
                            var $value = mdlUtils.find($trPopoverContent, _getFragment().random, '#' + key);
                            $value.text(value);
                        }

                        $trPopoverContent.removeClass('hidden');
                        $row.popover({
                            html: true,
                            content: $trPopoverContent[0],
                            trigger: 'hover',
                            delay: 300,
                            placement: function () {
                                $row.addClass('hasPopover');
                                return 'bottom';
                            }
                        });

                        $row.on('show.bs.popover',
                            function () {
                                $find('.popover').trigger('mouseout');
                            }
                        ).on('shown.bs.popover',
                            function () {
                                $find('.popover').on('mouseout', function () {
                                        if (!$(this).is(':hover')) {
                                            $row.removeClass('hasPopover').popover('hide');
                                        }
                                    }
                                );
                            }
                        ).on('hide.bs.popover',
                            function () {
                                if ($(this).hasClass('hasPopover')) return false;
                            }
                        );

                        if (data['mergedId'] && data['mergedId'] !== 'null') {
                            $row.addClass('mergedResident');
                            $row.attr('data-merged-id', data['mergedId']);
                            if (!fromCC) {
                                $row.hide();
                            }
                        } else {
                            isOddRow = !isOddRow;
                        }
                        if (isOddRow){
                            $row.removeClass('even');
                            $row.addClass('odd');
                        }
                        else {
                            $row.removeClass('odd');
                            $row.addClass('even');
                        }

                        var $actionsTd = $row.children('td:eq(7)');
                        $actionsTd.empty();
                        if (data.hasMerged) {
                            var $tdRowActions = $('<a class="showMergedLink"></a>');
                            if (!fromCC){
                                $tdRowActions.text("Show Matches")
                            }
                            else {
                                $tdRowActions.text("Hide Matches")
                            }
                            $actionsTd.on('click', function () {
                                    showMergedPatients(data.id, $row);
                                    return false;
                                }
                            );
                            $actionsTd.append($tdRowActions);
                        }
                    }
                },
                errorCallback: function(error){
                    mdlUtils.onAjaxError(error, function(e){
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
                footerCallback: function( tfoot, data, start, end, display) {
                    $(tfoot).show();

                    var isHidden = data.length < 20;
                    if(isHidden){
                        $(tfoot).hide();
                    }
                }
            }
        });
    }

    function showMergedPatients(patientId, $row) {

        var $mergedResidentRows = $("[data-merged-id='" + patientId + "'], .showAggregatedRecord[data-ajax-url-vars='residentId=" +patientId + "']");
        //if ($row.hasClass("even")) {
        //    $mergedResidentRows.addClass('even');
        //}
        //else {
        //    $mergedResidentRows.addClass('odd');
        //}
        if ($mergedResidentRows.is(':visible')) {
            $mergedResidentRows.hide();
            $row.find('.showMergedLink').text("Show Matches");
            $row.removeClass('hasMergedResidents');
        }
        else {
            $mergedResidentRows.show();
            $row.find('.showMergedLink').text("Hide Matches");
            $row.addClass('hasMergedResidents');
        }
    }

    function _setDefaultsToPatientSearchForm() {
        var setDefaults = function (values) {
            $find('#firstName').val(values.firstName);
            $find('#lastName').val(values.lastName);
            $find('input[id^=gender][id*=' + _getFragment().random + ']').each(function(i) {
                var $item = $(this);
                $item.prop("checked", $item.val() == values.gender);
                $item.trigger('refresh');
            });
            $find('#dateOfBirth').data('datepicker').setValue(values.birthDate);
            $find('#lastFourDigitsOfSsn').val(values.ssn);
        };

        if (cFragmentUrl.params && cFragmentUrl.params.firstName && cFragmentUrl.params.lastName && cFragmentUrl.params.birthDate
            &&cFragmentUrl.params.gender &&cFragmentUrl.params.ssn) {
            fromCC=true;
            //$find('#patientFilterForm').clearForm();
            var dt = new Date(cFragmentUrl.params.birthDate);
            var birthDate = dt.getMonth() + 1 +'/'+ dt.getDate() + '/' +dt.getFullYear();
            setDefaults({
                firstName: cFragmentUrl.params.firstName,
                lastName: cFragmentUrl.params.lastName,
                birthDate: birthDate,
                gender: cFragmentUrl.params.gender.toUpperCase(),
                ssn: cFragmentUrl.params.ssn
            });
            $find(":checkbox[value='NWHIN']").prop("checked",false).trigger('refresh');
            //$find(":checkbox[value='NWHIN']").trigger("change");
            //$find(":checkbox[value='NWHIN']").trigger('refresh');
            //var searchScopes = _getSearchScopesDict();
            //searchScopes['NWHIN']=false;
            _getFragment().widgets.patientSearchList.api().ajax.reload();
        }
        else {
            $.ajax({
                type: 'GET',
                contentType: 'json',
                url: $find('#loggedInEmployeeUrl').attr('href'),
                success: function (data) {
                    if (data.alternativeDatabaseId.toUpperCase() == 'EMTest_21250'.toUpperCase()) {
                        if (data.login.toUpperCase() == 'Greg'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Greg',
                                lastName: 'Bertagnoli',
                                birthDate: '9/27/1970',
                                gender: 'MALE',
                                ssn: '1111'
                            });
                        }
                        if (data.login.toUpperCase() == 'MAnderson'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Mark',
                                lastName: 'Anderson',
                                birthDate: '2/4/1995',
                                gender: 'MALE',
                                ssn: '1111'
                            });
                        }
                        if (data.login.toUpperCase() == 'GRobertson'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Greg',
                                lastName: 'Robertson',
                                birthDate: '6/18/1973',
                                gender: 'MALE',
                                ssn: '1111'
                            });
                        }
                        if (data.login.toUpperCase() === 'Craig'.toUpperCase() ||
                            data.login.toUpperCase() === 'aduzhynskaya'.toUpperCase() ||
                            data.login.toUpperCase() === 'phomal@scnsoft.com'.toUpperCase() ||
                            data.login.toUpperCase() === 'dweber'.toUpperCase() ||
                            data.login.toUpperCase() === 'nate'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Craig',
                                lastName: 'Patnode',
                                birthDate: '12/19/1969',
                                gender: 'MALE',
                                ssn: '6379'
                            });
                        }
                        if (data.login.toUpperCase() == 'Kevin'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Kevin',
                                lastName: 'King',
                                birthDate: '8/8/1953',
                                gender: 'MALE',
                                ssn: '1111'
                            });
                        }
                        if (data.login.toUpperCase() == 'Ben'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Ben',
                                lastName: 'Kelly',
                                birthDate: '10/1/1975',
                                gender: 'MALE',
                                ssn: '1111'
                            });
                        }
                    } else if (data.alternativeDatabaseId.toUpperCase() == 'RBA'.toUpperCase()) {
                        if (data.login.toUpperCase() == 'nate.tyler@eldermark.com'.toUpperCase()) {
                            setDefaults({
                                firstName: 'Craig',
                                lastName: 'Patnode',
                                birthDate: '12/19/1969',
                                gender: 'MALE',
                                ssn: '6379'
                            });
                        }
                    }
                }
            });
        }
    }



    /*-------------validation----------------*/

    function _addPatientSearchFormValidation() {
        var isSsnRequired = $find('#ssnRequired').val() == 'true';
        var isDateOfBirthRequired = $find('#dateOfBirthRequired').val() == 'true';

        var validationSettings = new ExchangeApp.utils.wgt.Validation({
            rules: {
                searchScopes: {required: true},
                firstName: {required: true},
                lastName: {required: true},
                gender: {required: true},
                dateOfBirth: {
                    dateExp: /^\d{2}\/\d{2}\/\d{4}$/,
                    required: function(element) {
                        return  _getSearchScopesDict().ELDERMARK ? isDateOfBirthRequired : true;
                    }
                },
                ssn1: {
                    integer: true,
                    required : {
                        depends: function(element) {
                            return $find('#ssn2').val() != '';
                        }
                    }
                },
                ssn2: {
                    integer: true,
                    required : {
                        depends: function(element) {
                            return $find('#ssn1').val() != '';
                        }
                    }
                },
                lastFourDigitsOfSsn: {
                    integer: true,
                    required: function(element) {
                        return _getSearchScopesDict().ELDERMARK ? isSsnRequired : false;
                    }
                },
                phone: {phone: true},
                city: {
                    required: function(element) {
                        return _getSearchScopesDict().NWHIN;
                    }
                },
                street: {
                    required: function(element) {
                        return _getSearchScopesDict().NWHIN;
                    }
                },
                state: {
                    stateUS: true,
                    required: function(element) {
                        return _getSearchScopesDict().NWHIN;
                    }
                },
                postalCode: {
                    required: function(element) {
                        return _getSearchScopesDict().NWHIN;
                    }
                }
            },
            messages: {
                lastName: {
                    required: 'Please enter Last Name that matches ID record'
                },
                firstName: {
                    required: 'Please enter First Name that matches ID record'
                },
                gender: {
                    required: getErrorMessage("field.empty")
                },
                dateOfBirth: {
                    dateExp: getErrorMessage("field.dateFormat"),
                    required: getErrorMessage("field.empty")
                },
                ssn: {
                    integer: getErrorMessage("field.integer"),
                    required: getErrorMessage("field.empty")
                },
                state: {
                    stateUS: getErrorMessage("field.state"),
                    required: getErrorMessage("field.empty")
                },
                postalCode: {
                    required: getErrorMessage("field.empty")
                },
                street: {
                    required: getErrorMessage("field.empty")
                },
                city: {
                    required: getErrorMessage("field.empty")
                }
            }
        });
        $.extend(validationSettings, {ignore: ""});

        return $find("#patientFilterForm").validate(
            validationSettings
        );
    }

    $.fn.clearForm = function() {
        return this.each(function () {
            var tag = this.tagName.toLowerCase();
            if (tag === 'form') {
                return $(':input:not(.hidden)', this).clearForm();
            }

            var type = this.type;
            if (type === 'text' || type === 'password' || tag === 'textarea') {
                this.value = '';
                $(this).trigger('refresh');
            } else if (type === 'checkbox') {
                this.checked = false;
                $(this).trigger('refresh');
            } else if (tag === 'select') {
                this.selectedIndex = 0;
                $(this).trigger('change');
            }
        });
    };

    return {
        init: function (url) {
            cFragmentUrl = url;
            $('#content').addClass('loading');

            this.renderHolder();

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var cFrg = _getFragment();

                    // add validation rules
                    patientSearchFormValidator = _addPatientSearchFormValidation();

                    // initialize widgets
                    cFrg.widgets = {};
                    _initPatientSearchListWgt();
                    _initDatePicker();
                    _initFormStyler();

                    _setDefaultsToPatientSearchForm();

                    this.setEvents();

                    $('#content').removeClass('loading');

                    this.render();
                    this.show();

                    cFrg.inited = true;
                    this.loaded();
                }
            });
        },

        update: function (url) {
            cFragmentUrl = url;
            _getFragment().widgets.patientSearchList.api().ajax.reload();
            this.loaded();
        },

        loadFragment: function(callbacks){
            var self = this;
            loader.load({
                url: cFragmentUrl,
                callbacks: {
                    onFragmentLoaded: function (fragment) {
                        state.reloadNeeded = false;

                        var $fragment = $(fragment).find('#content .markup-frg');

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
                }})
        },
        loaded: function() {
            $.each($('.baseHeader .ldr-head-lnk.active'), function(){
                 $(this).removeClass('active');
                 $(this).removeClass('bottom');
             });
             var link = $('.baseHeader .ldr-head-lnk.personalHealthRecordLnk ');
             link.addClass('active bottom');
        },
        render: function () {
            var $moduleContent = _getFragment().$html;
            _getFragmentHolder().empty();
            _getFragmentHolder().append($moduleContent);
        },

        renderHolder: function() {
            if(document.getElementById(_getFragmentHolderId()))
                return;

            var $holder = $('<div/>');
            $holder.attr('id', _getFragmentHolderId());
            $holder.hide();

            $('#content').append($holder);
        },

        hide: function () {
            _getFragmentHolder().hide();

            var fragment = _getFragment();
            if (fragment) {
                $find('.popover').addClass('hidden');

                var hashKeyErrorBox = $find('#hashKeyError');
                if(hashKeyErrorBox) hashKeyErrorBox.remove();
            }
            $('#content').addClass('loading');
        },

        show: function () {
            if ($('#content > div:visible').length == 0) {
                _getFragmentHolder().show();
            }
            $('#content').removeClass('loading');
        },

        getFragment: function(url){
            return  fragmentsMap[_stringifyUrl(url, {params: true})];
        },

        isFragmentInited: function(url){
            var fragment = this.getFragment(url);
            return fragment && fragment.inited;
        },

        setEvents: function () {
            var doPatientSearch = function() {
                fromCC = false;
                isOddRow = false;
                _getFragment().widgets.patientSearchList.api().order([[1, 'asc'], [0, 'asc']]);
                _getFragment().widgets.patientSearchList.api().ajax.reload();
            };

            /* handler for Enter key down*/
            $find('#patientFilterForm input').on('keydown', function(event) {
                if(event.keyCode == 13) {
                    //$find('#showMergedPatients').val(false);
                    //showMergedPatients = false;
                    event.preventDefault();
                    doPatientSearch();
                    return false;
                }
            });

            /* handler for SEARCH button*/
            $find("#search").on('click', function () {
                //$find('#showMergedPatients').val(false);
                //showMergedPatients = false;
                doPatientSearch();
                return false;
            });

            /* handler for CLEAR button*/
            $find("#clear").on('click', function () {
                $find('#patientFilterForm').clearForm();

                var $patientList = $find('#patientList');
                var oSettings = $patientList.dataTable().fnSettings();
                $patientList.dataTableExt.oApi.fnClearTable(oSettings);
                return false;
            });
        },

        reloadNeeded: function() {
            return state.reloadNeeded;
        }
    };
})();