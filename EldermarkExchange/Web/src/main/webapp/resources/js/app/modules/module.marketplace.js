$(document).on('ready', function(){
    var mdlUtils = ExchangeApp.utils.module;
     mdlUtils.showLoader();
    /*
     clearable input text with search icon - BEGIN
     */
    function tog(v){
        return v?'addClass':'removeClass';
    }

    function refreshClearable(input) {
        $(input)[tog(input.value)]('x');
    }

    $(document).on('input', '.clearable', function () {
        refreshClearable(this);
    }).on('mousemove', '.x', function (e) {
        $(this)[tog(this.offsetWidth - 38 < e.clientX - this.getBoundingClientRect().left)]('onX');//hover over x button
    }).on('click', '.onX', function () {
        $(this).removeClass('x onX').val('').change();//clear input
    }).on('change paste keyup', function() {
        refreshClearable(this);
    });

    /*
     clearable input text with search icon - END

     ellipse text with '...' symbol BEGIN
     */
    function ellipsizeTextBox(el) {
        var wordArray = el.innerHTML.split(' ');
        while(el.scrollHeight > el.offsetHeight) {
            wordArray.pop();
            el.innerHTML = wordArray.join(' ') + '...';
        }
    }

    function truncateText() {
        $(".truncated-text").each(function () {
            ellipsizeTextBox(this);
        });
    }

    truncateText();

    /*
     ellipse text with '...' symbol END
     */

    var INIT_ZOOM = 8;


    var pageNumber = 1;
    var map, autocomplete, places;
    var infowindow;
    var selectedLatLng;
    var selectedViewport;
    var clickedMarker;
    var clickedMarkerCount;
    var imageBase="resources/images/pins/";
    var icons = [imageBase+"pin.png",imageBase+"pin_2.png",imageBase+"pin_3.png",imageBase+"pin_4.png",imageBase+"pin_5.png",imageBase+"pin_5_.png"];
    var greenIcons = [imageBase+"pingreen.png",imageBase+"pin_2green.png",imageBase+"pin_3green.png",imageBase+"pin_4green.png",imageBase+"pin_5green.png",imageBase+"pin_5_green.png"];
    var initMapCenter;


    // multi selection comboboxes
    var $form = $("#marketplaceFilterFrom");
    var $selectPickers = $form.find(".spicker");
    $selectPickers.selectpicker();
    $selectPickers.on('changed.bs.select', mdlUtils.noneOptionHandler);
    $selectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);

    // select with input field
    // var $serviceSelect = $form.find("#serviceId");
    // $serviceSelect.select2({
    //     placeholder: 'Select Service',
    //     width: '100%'
    // });

    var markers = [];
    // $(".spicker").on('changed.bs.select', mdlUtils.noneOptionHandler);
    // $(".spicker").on('changed.bs.select', mdlUtils.allOptionHandler);

    // var $selectPickers =  $(".spicker");
    // $selectPickers.selectpicker();
    // var mdlUtils = ExchangeApp.utils.module;
    // $selectPickers.on('changed.bs.select', mdlUtils.noneOptionHandler);
    // $selectPickers.on('changed.bs.select', mdlUtils.allOptionHandler);


    //Don't know why it not toggled automatically. If you find it out remove it.
    // $(".bootstrap-select").click(function () {
    //     $(this).toggleClass( "open" );
    //     alert(1);
    // });


    function onPlaceChanged() {
        var place = autocomplete.getPlace();
        if (place.geometry) {
            selectedViewport = place.geometry.viewport;
            selectedLatLng = place.geometry.location;
            var location = {lat: place.geometry.location.lat(), lng: place.geometry.location.lng()};
            storeInitLocation(location);
            console.log("onPlaceChanged selectedLatLng " + place.geometry.location.lat() + " " + place.geometry.location.lng());
        }
    }
    
    function showSelectLocationDialog() {
        console.log("showSelectLocationDialog");
        $("#startLocationModalId").modal();
        $("#startLocationModalId").on("hide.bs.modal", function () {
            $("#startLocationAutocompleteId").val('');
        });
        $("#marketLocationButtonIdBack").click(function () {
            window.location.replace("login");
        });
        $("#marketLocationButtonIdGo").click(function () {
            if (typeof selectedLatLng !== 'undefined') {
                // console.log("show map for  selectedLatLng");
                // storeInitLocation(selectedLatLng);
                var location = {lat: selectedLatLng.lat(), lng: selectedLatLng.lng()};
                storeInitLocation(location);
                postFilterForm();
                // showMap(selectedLatLng);
                // map.fitBounds(selectedViewport);
            } else {
                // console.log("show map for MINNEAPOLIS");
                // var NORTH_EAST = {lat: 45.05125, lng: -93.193794};
                // var SOUTH_WEST = {lat: 44.890144, lng: -93.329163};
                // var MINNEAPOLIS_BOUNDS = new google.maps.LatLngBounds(SOUTH_WEST, NORTH_EAST);
                var MINNEAPOLIS_CENTER = {lat: 44.977753, lng: -93.26501080};
                storeInitLocation(MINNEAPOLIS_CENTER);
                postFilterForm();
                // showMap(MINNEAPOLIS_CENTER);
                // map.fitBounds(MINNEAPOLIS_BOUNDS);
            }
            $("#startLocationModalId").val('').modal('toggle');
        });
        autocomplete = new google.maps.places.Autocomplete(
            document.getElementById('startLocationAutocompleteId'),
            {
                 types : ['(regions)']
            }
        );
        autocomplete.addListener('place_changed', onPlaceChanged);
    }

    // var clientLocationUndefined;
    var detectedLatLng;

    function storeInitLocation(initLocation)  {
        // console.log("storeInitLocation " + initLocation.lat + " " + initLocation.lng);
        sessionStorage.lat = initLocation.lat;
        sessionStorage.lng = initLocation.lng;
        var $filterForm = $("#marketplaceFilterFrom");
        $filterForm.find("input[name='initLatitude']").val(initLocation.lat);
        $filterForm.find("input[name='initLongitude']").val(initLocation.lng);
    }

    function showMap(initLocation) {
        // console.log("show map " + initLocation + " " + initLocation.lat  + " " + initLocation.lng);
        map = new google.maps.Map(document.getElementById('map'), {
            center: initLocation,
            fullscreenControl: false,
            streetViewControl: false,
            mapTypeControl: false,
            zoomControl: true
        });
        google.maps.event.addListenerOnce(map, 'idle', function(){
            mdlUtils.hideLoader();
        });
        initMapCenter = initLocation;
        addYourLocationButton(map, initLocation);
        showMarkers();
    }

    function postFilterForm() {
        console.log("postFilterForm");
        $('#marketplaceFilterFrom').submit();
    }

    function initLocationSelector() {
        // console.log("initLocationSelector");
        // clientLocationUndefined = true;
        if ($(".marketplaceMap").attr("data-init-location-was-set") == "false") {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    function (position) {
                        detectedLatLng = {lat: position.coords.latitude, lng: position.coords.longitude};
                        storeInitLocation(detectedLatLng);
                        postFilterForm();
                        // showMap(detectedLatLng);
                        // map.setZoom(INIT_ZOOM);
                    },
                    function (error) {
                        showSelectLocationDialog();
                    }
                );
            } else {
                showSelectLocationDialog();
            }
        } else {
            var vLat = parseFloat(sessionStorage.lat);
            var vLng = parseFloat(sessionStorage.lng);
            // console.log("show map at previous location " + vLat + " " + vLng);
            var location = {lat: vLat, lng: vLng};
            showMap(location);
            map.setZoom(INIT_ZOOM);
        }
    }

    function showMarkers() {
        $('#communityList').children('.communityItem ').each(function () {

            // var $popup = $(".popup:hidden").clone();
            // // $popup.toggleClass("odd").toggleClass("even");
            // $popup.find(".communityName").text(communityList[0].communityName);
            // $popup.find(".communityTypes").text(communityList[i].communityTypes.join());
            // $popup.find(".address").text(communityList[i].address);
            var $addressField = $(this).find(".communityAddress");
             addMarkerForAddress($addressField);
        });
    }

    function addMarkerForAddress($addressField) {
        if ($addressField.data("addmarker")) {
            addMarker($addressField.data("latitude"), $addressField.data("longitude"), $addressField.data("sameaddrids"));
        }
    }

    window.initMap = function(){
        // console.log("initMap");
         initLocationSelector();
    };

    $('input[id="searchField"]').val($('input[name="searchText"]').val());
    $("#searchField.clearable").each(function() {
        var $input = $(this);
        if ($input.val() != '') {
            $input.addClass('x');
        }
    });
    highlightMatchesInListing();
    $('#communitySearchButton').on('click', function() {
        $('input[name="searchText"]').val($('input[id="searchField"]').val());
        postFilterForm();
    });
    $("#communitySearchForm").on("submit", function(e){
        e.preventDefault();
        $('#communitySearchButton').click();
    });

    function highlightMatchesInListing() {
        var searchText = $("#searchField").val();
        // console.log("highlightMatchesInListing " + searchText);
        if (!searchText || searchText === '') {
            return;
        }
        $(".communityName, .marketplace-community-type, .communityAddress").each(function() {
            var $this = $(this);
            var textBefore = $this.text();
            var textAfter = textBefore.replace(new RegExp('(' + searchText + ')', 'gi'), "<span class='highlight-green'>$1</span>");
            $this.html(textAfter);
        });
    }

    function showCommunityDetails(obj) {
        $("#market-place-filter-content").hide();
        $("#market-place-community-details-header").show();
        $("#filter #communityId").html('#' + obj.data("id"));

        $.ajax("marketplace/" + obj.data("id")+ "/details").success(function (data) {
            $("#marketplaceMenuContent").hide();
            var $detailsContent = $("#marketplaceDetailsContent");
            $detailsContent.hide();
            $detailsContent.empty();
            $detailsContent.append(data);
            $detailsContent.show();
            $("#content").find(".backToCommunity").on('click', function () {
                $detailsContent.hide();
                $("#marketplaceMenuContent").show();
                $("#market-place-filter-content").show();
                $("#market-place-community-details-header").hide();
            });

            $('.marketplace-service-description .more').on('click', function() {
                var $this = $(this);
                var $descriptionDivContainer = $this.parent(".marketplace-service-description");
                var $additionalTextSpan = $descriptionDivContainer.find(".additionalTextSpan");
                var $moreLink = $descriptionDivContainer.find(".more");
                $additionalTextSpan.toggle();
                $moreLink.toggle();
            });

            $(".community-details-services-panel").first().click();
        });

    }

    function initMarketPlaceLinks() {
        $(".marketplaceDetailsLink").on('click', function() {
            showCommunityDetails($(this));
        });
    }

    initMarketPlaceLinks();

    $('#communityList').on('scroll', function() {
        var $communityListDiv = $(this);
        if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight
            && !$("#marketplaceMenuContent").data("scroll-last")) {
            mdlUtils.showLoader();
            // alert('end reached');
            //$(this).find(".communityItem:last")
            $('input[name="searchText"]').val($('input[id="searchField"]').val());
            // $('#marketplaceFilterFrom').submit();
            var data = $('#marketplaceFilterFrom').serialize();
            // data.pageNumber = pageNumber++;
            $('input[name="pageNumber"]').val(pageNumber);
            $.ajax({
                url: 'marketplace/scroll',
                // data: new FormData($('#marketplaceFilterFrom')[0]),
                data: $('#marketplaceFilterFrom').serialize(),
                type: 'POST',
                // beforeSend: function(xhr){
                //     mdlUtils.csrf(xhr);
                // },
                success: function (data) {
                    if (data.last) {
                        $("#marketplaceMenuContent").data("scroll-last", true);
                    }
                   var communityList = data.content;

                   if (communityList.length>0) {
                       var i;
                       for (i = 0; i < communityList.length; i++) {
                           var $item = $communityListDiv.find(".communityItem:last").clone();
                           $item.toggleClass("odd").toggleClass("even");
                           $item.find(".communityName").text(communityList[i].communityName);
                           //var communityTypes = "";
                           // for (var j=0; j<data[0].communityTypes.length;j++) {
                           //     communityTypes = communityTypes + data[0].communityTypes[j];
                           //     if (j<data[0].communityTypes.length-1) {
                           //         communityTypes+=",";
                           //     }
                           // }

                           $item.find(".marketplace-community-type").text(communityList[i].communityTypes.join());
                           var $addedAddressField = $item.find(".communityAddress");

                           $addedAddressField.text(communityList[i].address);
                           $addedAddressField.attr("data-markercount", communityList[i].markerCount);
                           $addedAddressField.data("sameaddrids", communityList[i].sameAddrIds);
                           $addedAddressField.data("latitude", communityList[i].location.latitude);
                           $addedAddressField.data("longitude", communityList[i].location.longitude);
                           $addedAddressField.attr("data-addmarker", communityList[i].addMarker);

                           $item.find(".marketplaceDetailsLink").attr("data-id", communityList[i].id);

                           $item.find(".community-distance").text(communityList[i].location.displayDistanceMiles
                               //+ ' ' + communityList[i].location.longitude + ' ' + communityList[i].location.latitude
                           );
                           $communityListDiv.append($item);
                       }
                       pageNumber++;
                       refreshItemList();

                   }
                    mdlUtils.hideLoader();
                },
                error: function (error) {
                        alert(error.responseText);
                }
            });

        }
    });

    function refreshItemList() {
        // truncateText();
        initMarketPlaceLinks();
        clearMarkers();
        showMarkers();
        truncateText();
    }

    function clearActiveMarkerIcon() {
        if (clickedMarker) {
            clickedMarker.setIcon(icons[clickedMarkerCount-1]);
        }
    }

    function addMarker(latitude,longitude,ids) {
        var markerCount = ids.length;
        if (latitude && longitude) {
            var marker = new google.maps.Marker({
                position: {lat: latitude, lng: longitude},
                map: map,
                icon: icons[markerCount-1]
            });
            google.maps.event.addListener( marker, 'mouseover', function () {
                closeInfoWindow();
                google.maps.event.trigger(this, 'click');
            });
            marker.addListener('click', function() {
                closeInfoWindow();
                clearActiveMarkerIcon();
                this.setIcon(greenIcons[markerCount-1]);
                clickedMarker = this;
                clickedMarkerCount = markerCount;

                $.ajax({
                    url: 'marketplace/popup',
                    // data: new FormData($('#marketplaceFilterFrom')[0]),
                    data: {ids:ids},
                    type: 'POST',
                    // beforeSend: function(xhr){
                    //     mdlUtils.csrf(xhr);
                    // },
                    success: function (data) {

                         // $popup.empty();
                         // $popup.append(data);
                        var $data = $(data);
                        $data.find(".viewDetails").click(function (e) {
                            e.preventDefault();
                            showCommunityDetails($(this));
                        });
                        closeInfoWindow();
                        infowindow = new google.maps.InfoWindow({
                            content: $data[ 0 ],
                            maxWidth : 400
                        });
                        infowindow.open(map, marker);
                        setTimeout(truncateText, 200);
                        // var $popup = $("#organizationInfoPopup");
                        //todo hide close button

                        // $popup.find("#organizationInfoPopupModal").modal('show');
                        // $popup.find("#organizationInfoPopupModal").on('hidden.bs.modal', function () {
                        //     $(this).remove();
                        // });
                    }
                });
            });
            markers.push(marker);
        }
    }

    // Removes the markers from the map, but keeps them in the array.
    function clearMarkers() {
        setMapOnAll(null);
    }

    // Sets the map on all markers in the array.
    function setMapOnAll(map) {
        for (var i = 0; i < markers.length; i++) {
            markers[i].setMap(map);
        }
    }

    function closeInfoWindow() {
        if (typeof infowindow !== 'undefined') {
            infowindow.close();
        }
    }

    function closePopup(e) {
        var container = $("#organizationInfoPopup");
        // console.log("closePopup()" + container);

        // if the target of the click isn't the container nor a descendant of the container
        if (!container.is(e.target)
            && container.has(e.target).length === 0
            && typeof infowindow !== 'undefined'
        )
        {
            closeInfoWindow();
            clearActiveMarkerIcon();
        }
    }

    function clearSelectPicker($select) {
        $select.val('0');
        $select.selectpicker("refresh");
    }

    $(document).mouseup(function(e)
    {
        //close info window when click outside it
        closePopup(e);
    });
    
    $("#filterClear").click(function () {
        console.log("filterClear");
        var $clearButton = $(this);
        var $form = $clearButton.closest("form");
        console.log("filterClear " + $form.find("#orgPrimaryFocus").val());
        clearSelectPicker($form.find("#orgPrimaryFocus"));
        clearSelectPicker($form.find("#orgCommunityType"));
        clearSelectPicker($form.find("#serviceId"));
        $form.find(".choose-different-insurance").trigger("click", true);
    });

    $("#marketplaceFilterFrom").submit( function () {
        mdlUtils.showLoader();
    });

});