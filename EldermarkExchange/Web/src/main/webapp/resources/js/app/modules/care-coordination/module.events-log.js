ExchangeApp.modules.CareCoordinationEventsLog = (function () {
    // history of loaded segments
	var isIncidentReportPopupOpen;

    var fragmentsMap = {};
    // root url
    var cFragmentUrl = {
        template: 'care-coordination/events-log'
    };
    // flag if true then reloaded every time
    var state = {
        reloadNeeded: true
    };
    var holderContentId = "eventsLogTabContent";
    var eventActiveTab;
    // load root routers (map url to module)
    var router = ExchangeApp.routers.ModuleRouter;
    // Utility
    var mdlUtils = ExchangeApp.utils.module;
    var wgtUtils = ExchangeApp.utils.wgt;

    var loader = ExchangeApp.loaders.FragmentLoader.init('careCoordinationEventsLog');
    var pModule;


    var currentOrganizationFilter;
    var currentCommunityFilter;

    var followUpCodes = ['CM_24H', 'CM_14D', 'CM_ADDITIONAL'];
    var encounterType = ['FACE_TO_FACE_ENCOUNTER', 'NON_FACE_TO_FACE_ENCOUNTER'];

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

    function _initEventNotificationList() {
    	isIncidentReportPopupOpen = true;

    	$('#incidentReportPopup').css({'display': 'none'});

        _getFragment().widgets.eventNotificationList = wgtUtils.grid(_getFragment().$html, '', {
            tableId: "eventNotificationList",
            totalDisplayRows: 25,

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);
                    if (data) {
                        $row.children('td:eq(0)').html(moment(data.dateTime).format('<b>MM/DD/YYYY</b> <br/> hh:mm A'));
                    }
                    $row.popover({
                        html: true,
                        content: data.sentToText +  " with text : " + data.details,
                        trigger: 'hover',
                        delay: 300,
                        placement: function () {
                            $row.addClass('hasPopover');
                            return 'top';
                        }
                    });

                },
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO

                },
                footerCallback: function (tfoot, data, start, end, display) {
                    $(tfoot).hide();
                }
            }
        });
        _getFragment().widgets.eventNotificationList.api().ajax.reload();
    }

    function _initEventFilter () {
        _initDatePickers();

        $find('.event-filter-from input[type=checkbox]').each(function () {
            $(this).styler();
        });
    }

    function _initEventList() {
        eventActiveTab = null;
        _getFragment().widgets.eventList = _grid({
            tableId: "eventList",
            searchFormId: "eventsFilterFrom",
            totalDisplayRows: 25,
            selectable: {
                type: 'single'
            },

            callbacks: {
                rowCallback: function (row, data, index) {
                    var $row = $(row);

                    if (data !== null) {
                        var eventId = data.eventId;
                        // clear row content
                        $row.children('td:eq(0)').html('<div class="col-md-8">' + data.residentName + ' <br/> ' + data.eventType + '</div><div class="col-md-4"> ' + moment(data.eventDate).format('<b>MM/DD/YYYY</b> <br/> hh:mm A') + '</div>');
                        $row.attr("data-id",eventId);
                        $row.on('click', function () {
                            // Update Event details section
                            _showEventDetails(eventId);
                        });
                    }
                },
                "drawCallback": function (settings) {
                    if (cFragmentUrl.params&&cFragmentUrl.params.id) {
                        //case when user is redirected to events screen from event notification
                        var $row = $find("[data-id='" + cFragmentUrl.params.id + "']");
                        if ($row.length !== 0) {
                            $row.addClass("selected");
                        }
                    }
                    else {
                        $find("#eventDetails").hide();
                    }

                },
                errorCallback: function (error) {
                    alert(error.responseText);
                    // TODO
                },
                footerCallback: function (tfoot, data, start, end, display) {
                    //$(tfoot).show();
                    //
                    //var isHidden = data.length < 20;
                    //if (isHidden) {
                        $(tfoot).hide();
                    //}
                },
                headerCallback: function (thead, data, start, end, display) {
                    $(thead).hide();
                }
            }
        });
        _getFragment().widgets.eventList.api().ajax.reload();
        currentOrganizationFilter = ExchangeApp.modules.Header.getCurrentOrganizationFilter();
        currentCommunityFilter = ExchangeApp.modules.Header.getCurrentCommunityFilter();
    }

    function _showEventDetails(eventId, successCallback) {
        $.ajax({
            type: 'GET',
            url: "care-coordination/events-log/event/" + eventId + "/event-details",
            headers: {'X-Content-Compressing': 'enabled'}
        }).success(function (data) {
            var $eventDetails = $find("#eventDetails");
            $eventDetails.empty();
            $eventDetails.append(data);
            $eventDetails.show();

            _initEventNotificationList();

            $eventDetails.find('#downloadBtn').on('click', function (e) {
                $.fileDownload(
                    "care-coordination/events-log/event/" + eventId + "/download-pdf", {});
            });

            $eventDetails.find('#incidentReport').on('click', function (e) {
                $('#incidentReportPopup').css({'display': 'flex'});
            });

            $eventDetails.on('click', function (e) {
                if (!$(e.target).closest('#incidentReport').length) {
                    $('#incidentReportPopup').css({'display': 'none'});
                }
            });

            var $editIRBtn = $eventDetails.find('#editIncidentReport');

            if ($editIRBtn.length) {
                var reportId = $editIRBtn.data('reportId');
                _initIncidentReportModal(
                    mdlUtils.find(_getFragment().$html, '', '#editIncidentReport'), '', eventId, reportId
                );
            } else {
                _initIncidentReportModal(
                    mdlUtils.find(_getFragment().$html, '', '#createIncidentReport'), '', eventId
                );
            }

            var $viewIRBtn = $eventDetails.find('#viewIncidentReport');

            if ($viewIRBtn.length) {
                $viewIRBtn.on('click', function (e) {
                    var offset = new Date().getTimezoneOffset();
                    window.open('ir/events/' + eventId + '/pdf-incident-report?timeZoneOffset=' + offset);
                });
            }

            _initNotesModal(mdlUtils.find(_getFragment().$html, '', '#addEventNote'),
                'care-coordination/notes/event/' + eventId + '/new-note', eventId,
                'A note has been created', function (data) {
                    _showEventDetails(eventId, function () {
                        //go to just created note
                        mdlUtils.find($eventDetails, _getFragment().random, '[data-ajax-url-params^="note=' + data + '"].relatedNoteLink').click();
                    });
                }
            );

            mdlUtils.find($eventDetails, _getFragment().random, '.relatedNoteLink').on('click', function() {
                var template = $(this).attr('data-ajax-url-tmpl');
                var vars = $(this).attr('data-ajax-url-vars');
                var params = $(this).attr('data-ajax-url-params');

                var url = mdlUtils.getUrl(template, vars, params);
                router.route(url);
                return false;
            });

            if (successCallback !== undefined) {
                successCallback();
            }
        });
    }

    function _initDatePickers() {
        $find("#dateFrom").datepicker();
        $find("#dateTo").datepicker();
        $find("#dateFrom,#dateTo").change(function() {
            var dt = new Date($(this).val());
            if ( !isNaN( dt.getTime() ) ) {
                $(this).datepicker('setValue', dt);
            }
            else {
                $(this).val('');
            }
        });
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
                return $(':input:not([disabled="disabled"] ,[type="hidden"])', this).clearForm();
            }

            var type = this.type;
            if (type === 'text' || type === 'password' || tag === 'textarea') {
                this.value = '';
                $(this).trigger('refresh');
            }
            else if (tag === 'select') {
                this.selectedIndex = 0;
                $(this).trigger('change');
            }
            else if (type === 'checkbox' || type === 'radio') {
                this.checked = false;
                $(this).trigger('refresh');
            }
            if (this.id === 'dateFrom' + _getFragment().random) {
                this.value = $find("#defaultDateFrom").val();
            }
            if (this.id === 'dateTo' + _getFragment().random) {
                this.value = $find("#defaultDateTo").val();
            }
        });
    };

    function _initIncidentReportModal(element, endpoint, eventId, reportId) {
        requirejs(
            [
                'app/components/care-coordination/events/IncidentReportModal'
            ],
            function (Modal) {

                var modal = null;

                var options = {
                    isOpen: true,
                    container: document.body,
                    eventId: eventId,
                    reportId: reportId,
                    onHide: function (e) {
                        if (modal.isReportChanged() && !modal.isReportChangeIgnored()) {
                            e.preventDefault();

                            bootbox.confirm({
                                message: 'The changes will not be saved',
                                buttons: {
                                    confirm: {
                                        label: 'OK'
                                    },
                                    cancel: {
                                        label: 'CANCEL'
                                    }
                                },
                                callback: function (confirm) {
                                    if (confirm) {
                                        modal.ignoreReportChange();

                                        setTimeout(function () {
                                            modal.hide();
                                        })
                                    }
                                }
                            });
                        }
                    },
                    onSubmitReportSuccess: function (reportId) {
                        modal.hide();

                        options.reportId = reportId;

                        bootbox.confirm({
                            message: 'The updates have been saved. The pdf file sent to a mandated reporting governing body',
                            buttons: {
                                confirm: {
                                    label: 'View pdf file'
                                },
                                cancel: {
                                    label: 'Close'
                                }
                            },
                            callback: function (confirm) {
                                if (confirm) {
                                    var offset = new Date().getTimezoneOffset();
                                    window.open('ir/events/' + eventId + '/pdf-incident-report?timeZoneOffset=' + offset);
                                }
                            }
                        });

                        _showEventDetails(eventId);
                    },
                    onSubmitReportFailure: function () {
                        bootbox.alert('Cannot save the updates. Please try again later');
                    },
                    onSaveReportDraftSuccess: function (reportId) {
                        modal.hide();

                        options.reportId = reportId;

                        bootbox.confirm({
                            message: 'The incident report has been saved',
                            buttons: {
                                confirm: {
                                    label: 'Edit'
                                },
                                cancel: {
                                    label: 'Close'
                                }
                            },
                            callback: function (confirm) {
                                if (confirm) {
                                    modal = new Modal(options);
                                    modal.mount();
                                }
                            }
                        });

                        _showEventDetails(eventId);
                    },
                    onSaveReportDraftFailure: function () {
                        bootbox.alert('Cannot save the incident report. Please try again later');
                    }
                };

                element.on('click', function () {
                    modal = new Modal(options);
                    modal.mount();

                    setTimeout(function () {
                        $('#incidentReportPopup').css({'display': 'none'});
                    })
                });
            }
        );
    }

    function _initNotesModal(element, endpoint, eventId, successMsg, successCallback) {
        var $noteContainer = mdlUtils.find(_getFragment().$html, '', '#noteContainer');

        element.on('click', function () {
            var $button = $(this);
            if ($button.hasClass("pending")) {
                return;
            }
            $button.addClass("pending");
            $.ajax(endpoint)
                .success(function (data) {
                    $noteContainer.empty();
                    $noteContainer.append(data);
                    
                    $noteContainer.find('[id^="lastModifiedDate"]').datetimepicker({
                        defaultDate: new Date(),
                        format: 'YYYY-MM-DD hh:mm A Z',
                        maxDate: new Date()
                    });
                    
					$noteContainer.find('[id^="encounterDate"]').datetimepicker({
						defaultDate: new Date(),
						format: 'MM/DD/YYYY'
					});
					
					$noteContainer.find('[id^="from"]').timepicker({
						timeFormat: 'h:mm p',
						interval: 30,
						minTime: '00',
						maxTime: '11:30pm',
						defaultTime: getAdjustedDate($noteContainer, new Date()),
						startTime: '12:00am',
						dynamic: false,
						dropdown: true,
						scrollbar: true
					});
					$noteContainer.find('[id^="to"]').timepicker({
						timeFormat: 'h:mm p',
						interval: 30,
						minTime: '00',
						maxTime: '11:30pm',
						defaultTime: getAdjustedDate($noteContainer, new Date()),
						startTime: '12:00am',
						dynamic: false,
						dropdown: true,
						scrollbar: true
					});

                    mdlUtils.randomize($noteContainer, _getFragment().random, false);

                    var $noteModal =  mdlUtils.find($noteContainer, _getFragment().random, '#noteModal');
                    mdlUtils.find($noteContainer, _getFragment().random, '#lastModifiedDate').datetimepicker({
                        defaultDate: new Date(),
                        format: 'YYYY-MM-DD hh:mm A Z',
                        maxDate: new Date()
                    });
                    
                    $noteContainer.find('[id^="timeZoneOffset"]').val(new Date().getTimezoneOffset());
                    var subTypeDropdown = $noteContainer.find('[id^="subTypeId"]');
                    var admitDateDropdown = $noteContainer.find('[id^="noteResidentAdmittanceHistoryDtoId"]');
                    
                    var from = $noteContainer.find('[id^="from"]');
                    var to = $noteContainer.find('[id^="to"]');
                    _populateCalculatedFields($noteContainer);
                    from.on('blur', function () {
                    	//Unavoidable.. change event is not working so need to add timeout to let the value get populated.
                    	window.setTimeout(function(){
                    		_populateCalculatedFields($noteContainer,true);
                    	},500)
                    });
                    
                    to.on('blur', function () {
                    	//Unavoidable.. change event is not working so need to add timeout to let the value get populated.
                    	window.setTimeout(function(){
                    		_populateCalculatedFields($noteContainer,false);
                    	},500)
                    });   

                    subTypeDropdown.on('change', function () {
                        var selectedOption = $(this).find(':selected');                            
                        if ($.inArray(selectedOption.attr('data-encounter-code'), encounterType) >= 0){
                    		admitDateDropdown.attr('disabled', 'disabled');
                    		$noteContainer.find('*[id^="encounter-note-type-content"]').show()
                        }else{
                        	$noteContainer.find('*[id^="encounter-note-type-content"]').hide()
                        }
                    });
                    
                    var subTypeDropdown = mdlUtils.find($noteContainer, _getFragment().random, '#subTypeId');
                    var admitDateDropdown = mdlUtils.find($noteContainer, _getFragment().random, '#noteResidentAdmittanceHistoryDtoId');
                    if (admitDateDropdown.children('option').length === 1) {    // taking '-- Select --' into consideration
                        admitDateDropdown.attr('disabled', 'disabled');
                        subTypeDropdown.find('[data-follow-up-code]').attr('disabled', 'disabled');
                        subTypeDropdown.find('[data-follow-up-code]').attr('title', 'Please add the admittance information or the intake date on the "Patient Details" screen');
                    } else {
                        subTypeDropdown.on('change', function () {
                            var selectedOption = $(this).find(':selected');                            
                            if ($.inArray(selectedOption.attr('data-encounter-code'), encounterType) >= 0){
                        		admitDateDropdown.attr('disabled', 'disabled');
                        		$noteContainer.find('*[id^="encounter-note-type-content"]').show()
                            }else{
                            	$noteContainer.find('*[id^="encounter-note-type-content"]').hide()
                            }
                            if ($.inArray(selectedOption.attr('data-follow-up-code'), followUpCodes) === -1 ) {
                                admitDateDropdown.prop('selectedIndex', 0);
                                admitDateDropdown.attr('disabled', 'disabled');
                                admitDateDropdown.blur();   //trigger revalidation of the field
                                subTypeDropdown.children('option').removeAttr('disabled');
                                subTypeDropdown.children('option').removeAttr('title');
                            } else {
                                admitDateDropdown.removeAttr('disabled');
                                admitDateDropdown.children('option').removeAttr('title');
                                $.ajax('care-coordination/notes/event/' + eventId + '/followUp/'
                                    + selectedOption.attr('data-follow-up-code') + '/getTaken')
                                    .success(function (data) {
                                        admitDateDropdown.children('option').removeAttr('disabled');
                                        data.forEach(function (admitId) {
                                            if(admitDateDropdown.val() == admitId) {
                                                admitDateDropdown.prop('selectedIndex', 0);
                                            }
                                            var option = admitDateDropdown.find('[value=' + admitId + ']');
                                            option.attr('disabled', 'disabled');
                                            option.attr('title', '"' + selectedOption.text() + '" note has been already created for this admit/intake date.')
                                        });
                                    })
                            }
                        });

                        admitDateDropdown.on('change', function () {
                            var options = subTypeDropdown.children('option');
                            options.removeAttr('disabled');
                            options.removeAttr('title');
                            if ($(this).val()) {
                                $.ajax('care-coordination/notes/event/' + eventId + '/admit/'
                                    + $(this).val() + '/getTaken')
                                    .success(function (data) {
                                        data.forEach(function (followUpCode) {
                                            if (subTypeDropdown.find(':selected').attr('data-follow-up-code') === followUpCode) {
                                                subTypeDropdown.prop('selectedIndex', 0);
                                            }
                                            var option = subTypeDropdown.find('[data-follow-up-code=' + followUpCode + ']');
                                            option.attr('disabled', 'disabled');
                                            option.attr('title', '"' + option.text() + '" note has been already created for this admit/intake date.')
                                        });
                                    });
                            }
                        });
                    }

                    _addNoteValidation($noteContainer);

                    //var clicked = false;
                    mdlUtils.find($noteContainer, _getFragment().random, "#submitNoteBtn").on('click', function () {
                        var $form = mdlUtils.find($noteContainer, _getFragment().random, "#noteForm");
                        if (!$form.valid()) {
                            return false;
                        }
                        var $th = $(this);
                        if (!$th.hasClass("pending")) {
                            $th.addClass("pending");
                            $.ajax({
                                url: $form.attr('action'),
                                data: new FormData($form[0]),
                                type: 'POST',
                                enctype: 'multipart/form-data',
                                processData: false,
                                contentType: false,
                                beforeSend: function(xhr){
                                    mdlUtils.csrf(xhr);
                                },
                                success: function (data) {
                                    $noteModal.modal('hide');
                                    bootbox.alert(successMsg, function () {
                                        if (successCallback !== undefined) {
                                            successCallback(data);
                                        }
                                    });
                                },
                                error: function (error) {
                                    mdlUtils.onAjaxError(error, function () {
                                        alert(error.responseText);
                                    });
                                    $noteModal.modal('hide');
                                    $th.removeClass("pending");
                                }
                            }).complete(function () {
                                //$noteModal.hide();
                            });
                        }
                        return false;
                    });

                    mdlUtils.find($noteContainer, _getFragment().random, '.cancelBtn').on('click', function () {
                        $noteModal.modal('hide');
                        return false;
                    });

                    $button.removeClass("pending");
                    $noteModal.modal('show');

                })
                .fail(function (response) {
                    $button.removeClass("pending");
                    alert(response.responseText); // TODO
                });
            return false;
        });
    }

    function _addNoteValidation($container) {
        return mdlUtils.find($container, _getFragment().random, "#noteForm").validate(
            new ExchangeApp.utils.wgt.Validation({
                rules: {
                    subjective: {
                        required: function (element) {
                            return  mdlUtils.find($container, _getFragment().random, "#objective").is(':blank') &&
                                mdlUtils.find($container, _getFragment().random, "#assessment").is(':blank') &&
                                mdlUtils.find($container, _getFragment().random, "#plan").is(':blank');
                        },
                        maxlength: 20000
                    },
                    objective: {maxlength: 20000},
                    assessment: {maxlength: 20000},
                    plan: {maxlength: 20000},
                    lastModifiedDate: {
                        required: true
                    },
                    'subType.id': {
                        required: true
                    },
                    'noteResidentAdmittanceHistoryDto.id': {
                        required: function (element) {
                            return !element.disabled;
                        }
                    },
                    encounterDate: {
                        required: true
                    },
                    from: {
                        required: true
                    },
                    to: {
                        required: true
                    }
                },
                messages: {
                    subjective: {
                        required: getErrorMessage("field.empty")
                    },
                    lastModifiedDate: {
                        required: getErrorMessage("field.empty")
                    },
                    'subType.id': {
                        required: getErrorMessage("field.empty")
                    },
                    'noteResidentAdmittanceHistoryDto.id': {
                        required: getErrorMessage("field.empty")
                    },
                    encouterNoteTypeId: {
                        required: getErrorMessage("field.empty")
                    },
                    encounterDate: {
                        required: getErrorMessage("field.empty")
                    },
                    from: {
                        required: getErrorMessage("field.empty")
                    },
                    to: {
                        required: getErrorMessage("field.empty")
                    }
                }
            })
        )
    }
    function getAdjustedDate($container, date) {

        if($container.find('[id^="from"]')[0].value != "" || $container.find('[id^="to"]')[0].value != "")
            return;
        
        var m = date.getMinutes();
        var dt = date;
        if(m != 0)
        {
            var diff = (m > 30)?60-m:30-m;
            dt = new Date(date.getTime() + diff*60000);
        }
        return formatAMPM(dt);
    }

    function formatAMPM(date) {
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var ampm = hours >= 12 ? 'pm' : 'am';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        minutes = minutes < 10 ? '0'+minutes : minutes;
        return hours + ':' + minutes + ' ' + ampm;
    }
        
    function _populateCalculatedFields($container,resetByFrom){
        var from = $container.find('[id^="from"]');
        var to = $container.find('[id^="to"]');
        
        var totalTimeSpent  = $container.find('[id^="totalTimeSpent"]');
        var range = $container.find('[id^="range"]');
        var unit = $container.find('[id^="unit"]');

    	var startTime = moment(from.val(), "hh:mm A");
    	
    	if(resetByFrom){
    		endTime = moment(startTime).add(30, 'minutes').format("h:mm A");
    		to.val(endTime);
    	}
    	var endTime = moment(to.val(), "hh:mm A");    	
    	var duration = moment.duration(endTime.diff(startTime)).asMinutes();    	
    	if(duration < 1){
    		endTime = moment(startTime).add(30, 'minutes').format("h:mm A");
    		to.val(endTime);
    		duration = 30;
    	}    	
    	totalTimeSpent.val(duration);    	
    	var m = Math.floor(duration / 15);
    	var r = duration % 15;    	
    	if(r > 7){
    		m += 1;
    	}    	
    	var startRange = m*15 - 7;
    	var endRange = m*15 + 7;    	
    	if(startRange < 0){
    		startRange = 0;
    	}
    	range.val( startRange +" mins - " + endRange + " mins");
    	unit.val(m);
    }
    

    return {
        init: function (url, parentModule) {
            cFragmentUrl = url;
            pModule = parentModule;
            //this.renderHolder();

            this.loadFragment({
                onFragmentLoaded: function () {
                    _prepare();
                },
                onResourcesLoaded: function () {
                    var fragment = _getFragment();
                    fragment.widgets = {};

                    _initEventFilter();
                    _initEventList();

                    this.setEvents();

                    fragment.inited = true;

                    this.render();

                     if (cFragmentUrl.params && cFragmentUrl.params.id) {
                         //case when user is redirected to events screen from event notification
                         var eventId = cFragmentUrl.params.id;
                         $.ajax({
                                 url: "care-coordination/events-log/event/" + eventId + "/event-details",
                                 success: function (data) {
                                     $find("#eventDetails").empty();
                                     $find("#eventDetails").append(data);
                                     $find("#eventDetails").show();

                                     _initEventNotificationList();

                                     var row = _getFragment().widgets.eventList.api().row("[data-id='" + eventId + "']");
                                     if (row.length ==0) {
                                         //if event is not at first page of list - find page number and switch to it
                                         $.ajax("care-coordination/events-log/event/" + eventId + "/page-number").success(function (data) {
                                             _getFragment().widgets.eventList.api().page(data).draw(false);
                                         });

                                     }
                                     else {
                                        row.nodes().to$().addClass("selected");
                                 }
                                 },
                                 error: function (error) {
                                     bootbox.alert(error.responseText);
                                 }
                             }
                         );
                     }
                     else {
                         $find("#eventDetails").hide();
                         this.show();
                         if (this.getParentModule()) {
                             this.getParentModule().show();
                         }
                     }

                }
            });
        },

        update: function (url) {
            if ((currentOrganizationFilter !== ExchangeApp.modules.Header.getCurrentOrganizationFilter())
                || (JSON.stringify(currentCommunityFilter) !== JSON.stringify(ExchangeApp.modules.Header.getCurrentCommunityFilter()))){
                ExchangeApp.routers.ModuleRouter.reload();
            }

            cFragmentUrl = url;
            //$.each($('#care-coordination').find('.nav li'), function(){$(this).removeClass('active')});
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
            $.each($('#care-coordination').find('.eventTabs'), function () {
                if ($(this).hasClass('active')) {
                    var id = $(this).attr('id');
                    switch (id) {
                        case 'eventsDescriptionTab':
                            eventActiveTab = 'eventsDescriptionContent';
                            break;
                        case 'sentNotificationsTab':
                            eventActiveTab = 'sentNotificationsContent';
                            break;
                    }
                }
            });
            _getFragmentHolder().hide();
            $('#content').addClass('loading');
        },

        show: function () {
            // activate tab
            if ($('#content').find(".tab-pane:visible").length == 0) {
                $.each($('#care-coordination').find('.nav li'), function () {
                    $(this).removeClass('active');
                });
                $('a[data-target^="#' + holderContentId + '"]').parent('li').addClass('active');
                // render content

                _getFragmentHolder().show();
            }
            if (eventActiveTab) {
                $('a[href^="#' + eventActiveTab + '"]').parent('li').addClass('active');
            }
            $('#content').removeClass('loading');
        },

        setEvents: function () {
            $find("#eventSearch").on('click', function () {
                _getFragment().widgets.eventList.api().ajax.reload();
                return false;
            });
            $find("#eventSearchClear").on('click', function () {
                $find("#eventsFilterFrom").clearForm();
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